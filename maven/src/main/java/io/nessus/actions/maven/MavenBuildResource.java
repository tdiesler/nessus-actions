package io.nessus.actions.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

import io.nessus.actions.core.jaxrs.AbstractResource;
import io.nessus.actions.core.types.MavenBuildHandle;
import io.nessus.actions.core.types.MavenBuildHandle.BuildStatus;
import io.nessus.actions.maven.service.TaskExecutorService;
import io.nessus.common.AssertState;
import io.nessus.common.CheckedExceptionWrapper;
import io.nessus.common.utils.FileUtils;
import io.nessus.common.utils.StreamUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@javax.ws.rs.Path("/build")
public class MavenBuildResource extends AbstractResource {
	
	private static final int MAVEN_WORKER_POOL_SIZE = 1;

	private static ExecutorService executorService;
	
	synchronized ExecutorService getExecutorService() {
		if (executorService == null) {
			TaskExecutorService execsvc = getService(TaskExecutorService.class);
			executorService = execsvc.newExecutorService("maven-worker", MAVEN_WORKER_POOL_SIZE);
		}
		return executorService;
	}
	
	// Schedule Maven Build
	
	// POST http://localhost:8100/maven/api/build/schedule
	// 
	
	@POST
	@javax.ws.rs.Path("/schedule")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Operation(summary = "Schedule a maven project build")	
	@ApiResponse(responseCode = "200", description = "[OK] Accepted the project for build.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MavenBuildHandle.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided credentials were not valid.")
	
	public Response scheduleMavenBuild(MultipartFormDataInput input) {

		Map<String, List<InputPart>> formDataMap = input.getFormDataMap();
		
		List<InputPart> projIdParts = formDataMap.get("projId");
		AssertState.notNull(projIdParts, "Null 'projId' part");
		AssertState.isEqual(1, projIdParts.size(), "Expected single 'projId' part: " + projIdParts.size());
		
		List<InputPart> projZipParts = formDataMap.get("projZip");
		AssertState.notNull(projZipParts, "Null 'projZip' part");
		AssertState.isEqual(1, projZipParts.size(), "Expected single 'projZip' part: " + projZipParts.size());

		InputPart projIdPart = projIdParts.get(0);
		InputPart projZipPart = projZipParts.get(0);
		
		String projId;
		URI pomXmlUri = null;
		
		try {
			
			InputStream inputStream = projIdPart.getBody(InputStream.class, null);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			projId = br.readLine();
			
			Path workspace = getWorkspace(projId);
			FileUtils.recursiveDelete(workspace);
			workspace.toFile().mkdirs();
			
			logInfo("Storing project in workspace: {}", workspace);
			
			Map<ArchivePath, Node> content = ShrinkWrap.create(ZipImporter.class)
					.importFrom(projZipPart.getBody(InputStream.class, null))
					.as(GenericArchive.class)
					.getContent();
			
			for (Entry<ArchivePath, Node> en : content.entrySet()) {
				String path = en.getKey().get();
				if (path.startsWith("/")) 
					path = path.substring(1);
				Asset asset = en.getValue().getAsset();
				if (asset != null) {
					Path assetPath = workspace.resolve(path);
					assetPath.getParent().toFile().mkdirs();
					try (OutputStream outs = new FileOutputStream(assetPath.toFile())) {
						StreamUtils.copyStream(asset.openStream(), outs);
						logInfo("{} => {}", path, asset);
					}
					if (path.equals("pom.xml")) {
						pomXmlUri = assetPath.toUri();
					}
				}
			}

			AssertState.notNull(pomXmlUri, "Null pom.xml");
			
		} catch (IOException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		
		MavenBuildHandle handle = new MavenBuildHandle(projId, pomXmlUri, BuildStatus.Scheduled);
		writeBuildStatus(projId, BuildStatus.Scheduled);
		
		getExecutorService().submit(() -> {
			
			try {
				
				ProcessBuilder builder = new ProcessBuilder();
				String pomPath = handle.getLocation().getPath();
			    builder.command(findMvn(), "clean", "package", "-f", pomPath);
			    
				Process process = builder.start();
				
				Executors.newSingleThreadExecutor()
					.submit(new BuildProgressMonitor(projId, process));
				
				int exitCode = process.waitFor();
				return exitCode;
				
			} catch (Exception ex) {
				logError(ex);
				return 1;
			}
		});
		
		return Response.ok(handle, MediaType.APPLICATION_JSON).build();
	}

	// Get Build Status
	
	// GET http://localhost:8100/maven/api/build/{majorId}/{minorId}/status
	//
	
	@GET
	@javax.ws.rs.Path("/{majorId}/{minorId}/status")
	@Operation(summary = "Get the current build status")	
	@ApiResponse(responseCode = "200", description = "[OK] Found the status for the requested project.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MavenBuildHandle.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided credentials were not valid.")
	@ApiResponse(responseCode = "404", description = "[Not Found] The project for the given id was not found.")
	
	public Response getBuildStatus(@PathParam("majorId") String majorId, @PathParam("minorId") String minorId) {

		String projId = majorId + "/" + minorId;
		MavenBuildHandle handle = getMavenBuildHandle(projId);
		if (handle.getStatus() == BuildStatus.NotFound) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		return Response.ok(handle, MediaType.APPLICATION_JSON).build();
	}

	// Download the Target File
	
	// GET http://localhost:8100/maven/api/build/{majorId}/{minorId}/download
	//
	
	@GET
	@javax.ws.rs.Path("/{majorId}/{minorId}/download")
	@Operation(summary = "Download the build target")	
	@ApiResponse(responseCode = "200", description = "[OK] Found the requested build target.",
			content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided credentials were not valid.")
	@ApiResponse(responseCode = "404", description = "[Not Found] The target file was not found.")
	
	public Response downloadBuildTarget(@PathParam("majorId") String majorId, @PathParam("minorId") String minorId) {

		String projId = majorId + "/" + minorId;
		MavenBuildHandle handle = getMavenBuildHandle(projId);
		if (handle.getStatus() == BuildStatus.NotFound)
			return Response.status(Status.NOT_FOUND).build();
		
		String runtime = projId.substring(projId.indexOf('/') + 1);
		
		List<String> supported = Arrays.asList("standalone");
		AssertState.isTrue(supported.contains(runtime), "Supported runtimes are: " + supported);
		
		Response res = null;
		
		if (runtime.equals("standalone")) {
			
			File buildDir = getWorkspace(projId).resolve("target").toFile();
			File targetFile = Arrays.asList(buildDir.listFiles()).stream()
				.filter(f -> f.getName().endsWith("-runner.jar"))
				.findAny().orElse(null);
			
			if (targetFile == null)
				return Response.status(Status.NOT_FOUND).build();
			
			res = Response.ok(targetFile)
				.header("Content-Disposition", "attachment;filename=" + targetFile.getName())
				.build();
		}
		
		AssertState.notNull(res, "Null response");
		
		return res;
	}

	private MavenBuildHandle getMavenBuildHandle(String projId) {
		BuildStatus status = readBuildStatus(projId);
		URI uri = getWorkspace(projId).resolve("pom.xml").toUri();
		if (status == BuildStatus.Success) {
			File buildDir = getWorkspace(projId).resolve("target").toFile();
			File targetFile = Arrays.asList(buildDir.listFiles()).stream()
				.filter(f -> f.getName().endsWith("-runner.jar"))
				.findAny().orElse(null);
			uri = targetFile != null ? targetFile.toURI() : uri;
		}
		MavenBuildHandle handle = new MavenBuildHandle(projId, uri, status);
		return handle;
	}
	
	private BuildStatus readBuildStatus(String projId) {
		File statusFile = getWorkspace(projId).resolve("build-status").toFile();
		if (!statusFile.isFile()) return BuildStatus.NotFound;
		try (FileReader rd = new FileReader(statusFile)) {
			String line = new BufferedReader(rd).readLine();
			return BuildStatus.valueOf(line);
		} catch (IOException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
	
	private void writeBuildStatus(String projId, BuildStatus status) {
		File statusFile = getWorkspace(projId).resolve("build-status").toFile();
		try (FileWriter wr = new FileWriter(statusFile)) {
			logInfo("Status change: {} => {}", projId, status);
			wr.write(status.toString());
		} catch (IOException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
	}
	
	private Path getWorkspace(String projId) {
		Path workspace = config.getWorkspace().resolve("maven/" + projId);
		return workspace;
	}

	private String findMvn() {
		String mvn = Arrays.asList("/usr/local/bin/mvn", "/usr/bin/mvn").stream()
			.filter(p -> new File(p).exists())
			.findAny().orElse(null);
		return mvn;
	}

	class BuildProgressMonitor implements Runnable {

		private final String projId;
		private final Process process;
		private String lastMessage;
		
		BuildProgressMonitor(String projId, Process process) {
			this.process = process;
			this.projId = projId;
		}

		@Override
		public void run() {
			
			Reader procReader = new InputStreamReader(process.getInputStream());
			new BufferedReader(procReader).lines().forEach(msg -> {
				try {
					
					logInfo("{}", msg);
					
					if (lastMessage == null) {
						writeBuildStatus(projId, BuildStatus.Running);
					}
					
					else if (msg.contains("BUILD SUCCESS")) {
						writeBuildStatus(projId, BuildStatus.Success);
					}
					
					else if (msg.contains("BUILD FAILURE")) {
						writeBuildStatus(projId, BuildStatus.Failure);
					}
					
					lastMessage = msg;
					
				} catch (Exception ex) {
					logError(ex);
				}
			});
		}
	}
}