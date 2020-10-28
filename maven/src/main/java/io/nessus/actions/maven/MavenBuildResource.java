package io.nessus.actions.maven;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

import io.nessus.actions.core.jaxrs.AbstractResource;
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
	
	@POST
	@javax.ws.rs.Path("/schedule")
	@Consumes(value = MediaType.MULTIPART_FORM_DATA)
	@Operation(summary = "Schedule a maven project build")	
	@ApiResponse(responseCode = "200", description = "[OK] Accepted the project for build.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MavenBuildHandle.class)))
	@ApiResponse(responseCode = "401", description = "[Unauthorized] If the provided credentials were not valid.")
	
	public Response scheduleProjectBuild(MultipartFormDataInput input) {

		Map<String, List<InputPart>> formDataMap = input.getFormDataMap();
		
		List<InputPart> projNameParts = formDataMap.get("projName");
		AssertState.notNull(projNameParts, "Null 'projName' part");
		AssertState.isEqual(1, projNameParts.size(), "Expected single 'projName' part: " + projNameParts.size());
		
		List<InputPart> projZipParts = formDataMap.get("projZip");
		AssertState.notNull(projZipParts, "Null 'projZip' part");
		AssertState.isEqual(1, projZipParts.size(), "Expected single 'projZip' part: " + projZipParts.size());

		InputPart projNamePart = projNameParts.get(0);
		InputPart projZipPart = projZipParts.get(0);
		
		MavenBuildHandle buildHandle = null;
		
		try {
			InputStream inputStream = projNamePart.getBody(InputStream.class, null);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String projName = br.readLine();

			Path targetDir = config.getWorkspace().resolve(projName);
			FileUtils.recursiveDelete(targetDir);
			targetDir.toFile().mkdirs();
			
			logInfo("Storing project in workspace: {}", targetDir);
			
			Map<ArchivePath, Node> content = ShrinkWrap.create(ZipImporter.class, projName)
					.importFrom(projZipPart.getBody(InputStream.class, null))
					.as(GenericArchive.class)
					.getContent();
			
			for (Entry<ArchivePath, Node> en : content.entrySet()) {
				String path = en.getKey().get();
				if (path.startsWith("/")) 
					path = path.substring(1);
				Asset asset = en.getValue().getAsset();
				if (asset != null) {
					Path assetPath = targetDir.resolve(path);
					assetPath.getParent().toFile().mkdirs();
					try (OutputStream outs = new FileOutputStream(assetPath.toFile())) {
						StreamUtils.copyStream(asset.openStream(), outs);
						logInfo("{} => {}", path, asset);
					}
					if (path.equals("pom.xml")) {
						buildHandle = new MavenBuildHandle(assetPath.toUri());
					}
				}
			}
			
			AssertState.notNull(buildHandle, "Null buildHandle");
			
		} catch (IOException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		
		return Response.ok(buildHandle, MediaType.APPLICATION_JSON).build();
	}
}