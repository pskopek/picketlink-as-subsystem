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
package org.picketlink.as.subsystem.model.idp;

import static org.picketlink.as.subsystem.model.ModelElement.COMMON_SECURITY_DOMAIN;
import static org.picketlink.as.subsystem.model.ModelElement.COMMON_URL;
import static org.picketlink.as.subsystem.model.ModelElement.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES;
import static org.picketlink.as.subsystem.model.ModelElement.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES;

import java.util.Locale;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.model.SubsystemDescriber;
import org.picketlink.as.subsystem.model.event.IdentityProviderURLEvent;
import org.picketlink.as.subsystem.service.FederationService;
import org.picketlink.as.subsystem.service.IDPConfigurationService;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class IDPReloadHandler implements OperationStepHandler, DescriptionProvider{

    public static final String OPERATION_NAME = "reload";
    
    public static final IDPReloadHandler INSTANCE = new IDPReloadHandler();

    private IDPReloadHandler() {
        
    }
    
    @Override
    public ModelNode getModelDescription(Locale locale) {
        return SubsystemDescriber.getOperationDescription(OPERATION_NAME, "Relodas the IDP configuration.");
    }

    @Override
    public void execute(OperationContext context, ModelNode operation) throws OperationFailedException {
        ModelNode node = context.readResource(PathAddress.EMPTY_ADDRESS).getModel();
        
        final String alias = node.get(ModelElement.COMMON_ALIAS.getName()).asString();
        String url = node.get(COMMON_URL.getName()).asString();
        boolean signOutgoingMessages = node.get(IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getName()).asBoolean();
        boolean ignoreIncomingSignatures = node.get(IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getName()).asBoolean();
        String securityDomain = node.get(COMMON_SECURITY_DOMAIN.getName()).asString();

        IDPConfigurationService service = (IDPConfigurationService) context.getServiceRegistry(true).getRequiredService(IDPConfigurationService.createServiceName(alias)).getValue();
        
        service.getIdpConfiguration().setIdentityURL(url);
        service.getIdpConfiguration().setSignOutgoingMessages(signOutgoingMessages);
        service.getIdpConfiguration().setIgnoreIncomingSignatures(ignoreIncomingSignatures);
        service.getIdpConfiguration().setSecurityDomain(securityDomain);
        
        String fedAlias = operation.get(ModelDescriptionConstants.ADDRESS).asPropertyList().get(1).getValue().asString();

        FederationService federationService = FederationService.getService(context.getServiceRegistry(true), fedAlias);
        
        federationService.getEventManager().raise(new IdentityProviderURLEvent(url));
    }

}