package io.nessus.actions.portal;

import static io.nessus.actions.model.Model.CAMEL_ACTIONS_RESOURCE_NAME;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.as.controller.client.ModelControllerClientConfiguration;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.model.Model;
import io.nessus.actions.model.Model.TargetRuntime;
import io.nessus.actions.model.utils.AssertState;
import io.nessus.actions.model.utils.CheckedExceptionWrapper;
import io.nessus.actions.model.utils.UsernamePasswordHandler;
import io.nessus.actions.runner.RemoteArchiveDeployer;

@SuppressWarnings("serial")
@WebServlet(value = "/action/*")
public class PortalServlet extends HttpServlet {

	static final Logger LOG = LoggerFactory.getLogger(PortalServlet.class);

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		String pathInfo = req.getPathInfo();
		LOG.warn("pathInfo: {}", pathInfo);

		try {
			
			// Store model content in the user session
			
			if (pathInfo.startsWith("/yaml-submit")) {

				handleModelContentSubmit(req);
				
				res.sendRedirect("../step2-eap.html");
			}
			
			else if (pathInfo.startsWith("/step2-eap")) {

				handleAchiveDeploy(req);
				
				res.sendRedirect("../success.html");
			}
			
		} catch (Throwable th) {
			
			OutputStream out = res.getOutputStream();
			th.printStackTrace(new PrintStream(out));
			out.close();
		}
	}

	private void handleModelContentSubmit(HttpServletRequest req) throws IOException {
		
		String content = req.getParameter("content");
		AssertState.notNull(content, "Cannot obtain value for parameter: content");
		
		content = content.replace("\t", "   ");
		LOG.warn("YAML Content ------------ \n{}", content);
		
		Model model = Model.read(content);
		HttpSession session = req.getSession();
		session.setAttribute(Model.class.getName(), model);
	}
	
	private void handleAchiveDeploy(HttpServletRequest req) throws IOException, ServletException {
		
		HttpSession session = req.getSession();
		Model model = (Model) session.getAttribute(Model.class.getName());
		AssertState.notNull(model, "Cannot obtain model from session");
		
		TargetRuntime rt = model.getRuntime();
		AssertState.isTrue(rt.isWildFly(), "Invalid runtime: " + rt);
		
		LOG.warn("YAML Content ------------ \n{}", model.toString());
		
		String modelName = model.getName();
		String deploymentName = modelName.replace(' ', '-').toLowerCase() + ".war";
		
    	WebArchive archive = ShrinkWrap.create(WebArchive.class, deploymentName)
        	.addAsWebInfResource("jboss-deployment-structure.xml")
    		.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
        	.addAsResource(new StringAsset(model.toString()), CAMEL_ACTIONS_RESOURCE_NAME);
        
    	addClasses(archive, ApplicationScopedRouteBuilder.class);
    	addLibs(archive, "nessus-actions-model", "nessus-actions-runner");
    	
    	LOG.warn("{}", archive.toString(true));
    	
		String address = req.getParameter("address");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		URL addrUrl = new URL(address);
		LOG.warn("Using: {}", addrUrl);
		
		ModelControllerClientConfiguration.Builder clientConfigBuilder = new ModelControllerClientConfiguration.Builder()
                .setHandler(new UsernamePasswordHandler(username, password.toCharArray()))
                .setProtocol("remote+" + addrUrl.getProtocol())
                .setHostName(addrUrl.getHost())
                .setPort(addrUrl.getPort());

        ModelControllerClientConfiguration clientConfig = clientConfigBuilder.build();
        try (RemoteArchiveDeployer deployer = new RemoteArchiveDeployer(clientConfig)) {
        	
            LOG.warn("WebUri: {}", deployer.getWebUri());
            LOG.warn("Running: {}", deployer.isServerInRunningState());
            
            if (deployer.hasDeployment(archive.getName()))
            	deployer.undeploy(archive.getName());

            String runtimeName = deployer.deploy(archive);
            session.setAttribute("runtimeName", runtimeName);
            
        } catch (DeploymentException ex) {
        	throw new ServletException(ex);
		}
	}

	private void addClasses(WebArchive archive, Class<?>... clazzes) {
		for (Class<?> clazz : clazzes) {
			String clazzPath = clazz.getName().replace('.', '/');
			String resPath = "/WEB-INF/classes/" + clazzPath + ".class";
			archive.addAsResource(assertResourceUrl(resPath), clazzPath + ".class");
		}
	}

	private void addLibs(WebArchive archive, String... libs) throws IOException {
		for (String libName : libs) {
			String jarPath = getServletContext().getResourcePaths("/WEB-INF/lib").stream()
				.filter(path -> path.contains(libName))
				.findFirst().orElse(null);
			AssertState.notNull(jarPath, "Cannot find library: " + libName);
			URL jarUrl = assertResourceUrl(jarPath);
			String jarName = jarPath.substring(jarPath.lastIndexOf('/') + 1);
			archive.addAsLibrary(ShrinkWrap.create(JavaArchive.class, jarName)
		    		.as(ZipImporter.class).importFrom(jarUrl.openStream())
		    		.as(JavaArchive.class));
		}
	}

	private URL assertResourceUrl(String resource) {
		URL resurl = getResourceUrl(resource);
		AssertState.notNull(resurl, "Cannot obtain: " + resource);
		return resurl;
	}
	
	private URL getResourceUrl(String resource) {
		URL resURL;
		try {
			resURL = getServletContext().getResource(resource);
		} catch (MalformedURLException ex) {
			throw CheckedExceptionWrapper.create(ex);
		}
		return resURL;
	}

}
