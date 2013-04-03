/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketlink.as.subsystem.idm.deployment;

import static org.picketlink.as.subsystem.PicketLinkLogger.ROOT_LOGGER;

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.modules.ModuleIdentifier;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class IdentityMarkerProcessor implements DeploymentUnitProcessor {

    public static final AttachmentKey<Boolean> ENABLE_IDENTITY_KEY = AttachmentKey.create(Boolean.class);

    public static final String IDENTITY_IDENTIFIER_NAME = "org.picketlink";

    public static final ModuleIdentifier IDENTITY_IDENTIFIER = ModuleIdentifier.create(IDENTITY_IDENTIFIER_NAME);

    public static final Phase PHASE = Phase.STRUCTURE;

    public static final int PRIORITY = 0x3000;

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        if (enabledIdentity(deploymentUnit)) {

            if (deploymentUnit.getParent() != null) {
                deploymentUnit = deploymentUnit.getParent();
            }

            Boolean existingValue = deploymentUnit.putAttachment(ENABLE_IDENTITY_KEY, true);

            if (existingValue == null) {
                ROOT_LOGGER.infov("Enabling identity for {0}", deploymentUnit.getName());
            }
        }
    }

    private boolean enabledIdentity(DeploymentUnit deploymentUnit) {
        ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
        for (ModuleDependency d : moduleSpecification.getUserDependencies()) {
            if (d.getIdentifier().equals(IDENTITY_IDENTIFIER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void undeploy(DeploymentUnit context) {
    }

}
