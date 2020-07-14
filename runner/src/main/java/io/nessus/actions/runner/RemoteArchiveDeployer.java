package io.nessus.actions.runner;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.as.arquillian.container.ArchiveDeployer;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.ModelControllerClientConfiguration;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Allows deployment operations to be executed on a running server.
 *
 * <p>
 * The client is not closed by an instance of this and is the responsibility of the user to clean up the client instance.
 * </p>
 *
 * @author tdiesler@redhat.com
 * @since 14-Jul-2020
 */
public class RemoteArchiveDeployer implements Closeable {

	private final ModelControllerClient modelControllerClient;
	private final ManagementClient managementClient;
	private final ArchiveDeployer archiveDeployer;
	
	public RemoteArchiveDeployer(ModelControllerClientConfiguration clientConfig) {
		
		modelControllerClient = ModelControllerClient.Factory.create(clientConfig);
		
        managementClient = new ManagementClient(modelControllerClient, 
        		clientConfig.getHost(), 
        		clientConfig.getPort(), 
        		clientConfig.getProtocol());
        
        archiveDeployer = new ArchiveDeployer(managementClient);
	}
	
	public ModelControllerClient getModelControllerClient() {
		return modelControllerClient;
	}

	public ManagementClient getManagementClient() {
		return managementClient;
	}

	public ArchiveDeployer getArchiveDeployer() {
		return archiveDeployer;
	}

    /**
     * Checks whether or not the server is running.
     *
     * @return {@code true} if the server is running, otherwise {@code false}
     *
     * @throws IllegalStateException if this has been {@linkplain #close() closed}
     */
    public boolean isServerInRunningState() {
    	return managementClient.isServerInRunningState();
    }

    /**
     * @return The base URI or the web susbsystem. Usually http://localhost:8080
     *
     * @throws IllegalStateException if this has been {@linkplain #close() closed}
     */
	public URI getWebUri() {
		return managementClient.getWebUri();
	}

    /**
     * Deploys the archive to a running container.
     *
     * @param archive the archive to deploy
     *
     * @return the runtime name of the deployment
     *
     * @throws DeploymentException   if an error happens during deployment
     * @throws IllegalStateException if the client has been closed
     */
    public String deploy(WebArchive archive) throws DeploymentException {
    	return archiveDeployer.deploy(archive);
	}

    /**
     * Deploys the archive to a running container.
     *
     * @param name  the runtime for the deployment
     * @param input the input stream of a deployment archive
     *
     * @return the runtime name of the deployment
     *
     * @throws DeploymentException   if an error happens during deployment
     * @throws IllegalStateException if the client has been closed
     */
    public String deploy(String name, InputStream input) throws DeploymentException {
    	return archiveDeployer.deploy(name, input);
    }

    /**
     * Removes an archive from the running container.
     * <p>
     * All exceptions are caught and logged as a warning. {@link Error Errors} will still be thrown however.
     * </p>
     *
     * @param runtimeName the runtime name for the deployment
     */
    public void undeploy(String runtimeName) {
    	archiveDeployer.undeploy(runtimeName);
    }

    /**
     * Removes an archive from the running container.
     * <p>
     * All exceptions are caught and logged as a warning. {@link Error Errors} will still be thrown however.
     * </p>
     *
     * @param runtimeName   the runtime name for the deployment
     * @param failOnMissing {@code true} if the undeploy should fail if the deployment was not found on the server,
     *                      {@code false} if the deployment does not exist and the undeploy should be ignored
     */
    public void undeploy(final String runtimeName, final boolean failOnMissing) {
    	archiveDeployer.undeploy(runtimeName, failOnMissing);
    }

    /**
     * Checks if the deployment content is on the server.
     *
     * @param runtimeName the name of the deployment
     *
     * @return {@code true} if the deployment content exists otherwise {@code false}
     *
     * @throws IOException if a failure occurs communicating with the server
     */
    public boolean hasDeployment(final String runtimeName) throws IOException {
    	return archiveDeployer.hasDeployment(runtimeName);
    }

	@Override
	public void close() throws IOException {
		modelControllerClient.close();
	}
}