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

package org.picketlink.as.subsystem.parser;

import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.jboss.dmr.Property;
import org.jboss.staxmapper.XMLExtendedStreamWriter;
import org.picketlink.as.subsystem.model.ModelDefinition;
import org.picketlink.as.subsystem.model.XMLElements;

/**
 * @author pedroigor
 * @sice Mar 9, 2012
 */
public class FederationWriter extends AbstractModelWriter {

    /**
     * @param register
     */
    public FederationWriter(Map<String, ModelWriter> register) {
        super(register);
    }

    /* (non-Javadoc)
     * @see org.picketlink.as.subsystem.parser.ModelWriter#write(org.jboss.staxmapper.XMLExtendedStreamWriter, org.jboss.dmr.Property)
     */
    @Override
    public void write(XMLExtendedStreamWriter writer, Property property) throws XMLStreamException {
        writer.writeStartElement(ModelDefinition.FEDERATION.getKey());

        if (property.getValue().hasDefined(ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey())) {
            ModelDefinition.FEDERATION_ALIAS.getDefinition().marshallAsAttribute(property.getValue(), writer);
        }
        
        get(ModelDefinition.IDENTITY_PROVIDER.getKey()).write(writer, property);
        
        get(ModelDefinition.SERVICE_PROVIDER.getKey()).write(writer, property);
        
        writer.writeEndElement();
    }

}