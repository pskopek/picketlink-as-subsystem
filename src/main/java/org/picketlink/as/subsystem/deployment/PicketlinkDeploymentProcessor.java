package org.picketlink.as.subsystem.deployment;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceRegistry;
import org.picketlink.as.subsystem.service.IDPConfigurationService;
import org.picketlink.as.subsystem.service.SPConfigurationService;

/**
 * <p>A custom deployment unit processor to handle application deployments, usually WAR files, and configuring them
 * based in the configuration defined for the PicketLink subsystem.</p>
 * 
 * @author pedroigor
 * @sice Mar 9, 2012
 */
public class PicketlinkDeploymentProcessor implements DeploymentUnitProcessor {

    /**
     * See {@link Phase} for a description of the different phases
     */
    public static final Phase PHASE = Phase.DEPENDENCIES;

    /**
     * The relative order of this processor within the {@link #PHASE}. The current number is large enough for it to happen after
     * all the standard deployment unit processors that come with JBoss AS.
     */
    public static final int PRIORITY = 0x4000;

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#deploy(org.jboss.as.server.deployment.DeploymentPhaseContext)
     */
    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        String name = phaseContext.getDeploymentUnit().getName();
        IDPConfigurationService idpService = getIdentityProviderService(phaseContext.getServiceRegistry(), name);

        if (idpService != null) {
            ResourceRoot root = phaseContext.getDeploymentUnit().getAttachment(Attachments.DEPLOYMENT_ROOT);
            idpService.configure(root);
        }

        SPConfigurationService spService = getServiceProviderService(phaseContext.getServiceRegistry(), name);

        if (spService != null) {
            ResourceRoot root = phaseContext.getDeploymentUnit().getAttachment(Attachments.DEPLOYMENT_ROOT);
            spService.configure(root);
        }
}

    /* (non-Javadoc)
     * @see org.jboss.as.server.deployment.DeploymentUnitProcessor#undeploy(org.jboss.as.server.deployment.DeploymentUnit)
     */
    @Override
    public void undeploy(DeploymentUnit context) {
        //TODO: handle undeploy
    }

    /**
     * Returns a instance of the service responsible to configure applications as an IDP.
     * 
     * @param registry
     * @param name
     * @return
     */
    private IDPConfigurationService getIdentityProviderService(ServiceRegistry registry, String name) {
        ServiceController<?> container = registry.getService(IDPConfigurationService.createServiceName(name));
        
        if (container != null) {
            return (IDPConfigurationService) container.getValue();
        }
        
        return null;
    }
    
    /**
     * Returns a instance of the service responsible to configure applications as an IDP.
     * 
     * @param registry
     * @param name
     * @return
     */
    private SPConfigurationService getServiceProviderService(ServiceRegistry registry, String name) {
        ServiceController<?> container = registry.getService(SPConfigurationService.createServiceName(name));
        
        if (container != null) {
            return (SPConfigurationService) container.getValue();
        }
        
        return null;
    }

}