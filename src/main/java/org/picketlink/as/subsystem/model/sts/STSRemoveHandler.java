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

package org.picketlink.as.subsystem.model.sts;

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.picketlink.as.subsystem.service.FederationService;
import org.picketlink.as.subsystem.service.SecurityTokenServiceService;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class STSRemoveHandler extends AbstractRemoveStepHandler {

    public static final STSRemoveHandler INSTANCE = new STSRemoveHandler();

    private STSRemoveHandler() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.as.controller.AbstractRemoveStepHandler#performRuntime(org.jboss.as.controller.OperationContext,
     * org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode)
     */
    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model)
            throws OperationFailedException {
        String fedAlias = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getElement(1).getValue();
        String stsAlias = PathAddress.pathAddress(operation.get(ModelDescriptionConstants.ADDRESS)).getLastElement().getValue();
        
        FederationService federationService = FederationService.getService(context.getServiceRegistry(true), operation);
        SecurityTokenServiceService stsService = SecurityTokenServiceService.getService(context.getServiceRegistry(true), stsAlias);
        
        federationService.getEventManager().removeObserver(stsService);
        
        context.removeService(SecurityTokenServiceService.createServiceName(stsAlias));
    }

}
