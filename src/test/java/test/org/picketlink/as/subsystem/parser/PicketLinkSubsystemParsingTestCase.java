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
package test.org.picketlink.as.subsystem.parser;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIBE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.picketlink.as.subsystem.PicketLinkExtension;

/**
 * <p>
 * Tests that the PicketLink Subsystem parsing mechanism is functional.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 9, 2012
 */
public class PicketLinkSubsystemParsingTestCase extends AbstractSubsystemTest {

    private static final String FAKE_AS7_INSTALLATION_DIR = "target/jboss-as7-fake";
    private static final String FAKE_AS7_DEPLOYMENTS = FAKE_AS7_INSTALLATION_DIR + "/deployments";

    @Before
    public void onSetup() {
        configureFakeAS7Installation();
    }

    /**
     * <p>
     * Creates a directory at FAKE_AS7_INSTALLATION_DIR to be used as a fake as7 installation.
     * </p>
     */
    private void configureFakeAS7Installation() {
        File fakeAsInstallation = new File(FAKE_AS7_INSTALLATION_DIR);
        
        if (fakeAsInstallation.exists()) {
            fakeAsInstallation.delete();
        }
        
        fakeAsInstallation.mkdir();
        
        File deploymentsDir = new File(FAKE_AS7_DEPLOYMENTS);
        
        deploymentsDir.mkdir();
        
        System.setProperty("jboss.server.base.dir", fakeAsInstallation.getAbsolutePath());
    }
    
    public PicketLinkSubsystemParsingTestCase() {
        super(PicketLinkExtension.SUBSYSTEM_NAME, new PicketLinkExtension());
    }

    /**
     * Returns a valid XML for the subsystem.
     * 
     * @return
     */
    private String getValidSubsystemXML() {
        String content = null;
        
        try {
            content = FileUtils.readFileToString(new File(Thread.currentThread().getContextClassLoader()
                    .getResource("picketlink-subsystem.xml").getFile()));
        } catch (IOException e) {
            Assert.fail("Error while reading the subsystem configuration file.");
        }

        return content;
    }

    /**
     * Tests that the xml is parsed into the correct operations.
     */
    @Test
    public void testParseSubsystem() throws Exception {
        List<ModelNode> operations = super.parse(getValidSubsystemXML());

        Assert.assertNotNull("No operations found. Check if the XML used is valid.", operations);
    }

    /**
     * Tests that the xml installs properly into the controller.
     * 
     * @throws Exception
     */
    @Test
    public void testInstallIntoController() throws Exception {
        KernelServices services = super.installInController(getValidSubsystemXML());

        ModelNode model = services.readWholeModel();
        
        Assert.assertNotNull("Expected a populated model.", model);
        Assert.assertNotNull("ModelNode instance is not defined.", model.isDefined());
        
        Assert.assertTrue("PicketLinkSTS.war was not generated.", new File(FAKE_AS7_DEPLOYMENTS + "/PicketLinkSTS.war").exists());
    }

    /**
     * Tests that the SubsystemParser.writeContent() works as expected.
     * 
     * @throws Exception
     */
    @Test
    public void testParseAndMarshalModel() throws Exception {
        String subsystemXml = getValidSubsystemXML();

        KernelServices servicesA = super.installInController(subsystemXml);

        ModelNode modelA = servicesA.readWholeModel();
        String marshalled = servicesA.getPersistedSubsystemXml();

        System.out.println(marshalled);

        KernelServices servicesB = super.installInController(marshalled);

        ModelNode modelB = servicesB.readWholeModel();

        super.compare(modelA, modelB);
    }

    @Test
    public void testDescribeHandler() throws Exception {
        String subsystemXml = getValidSubsystemXML();
        KernelServices servicesA = super.installInController(subsystemXml);

        ModelNode modelA = servicesA.readWholeModel();
        ModelNode describeOp = new ModelNode();
        describeOp.get(OP).set(DESCRIBE);
        describeOp.get(OP_ADDR).set(
                PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, PicketLinkExtension.SUBSYSTEM_NAME)).toModelNode());
        List<ModelNode> operations = super.checkResultAndGetContents(servicesA.executeOperation(describeOp)).asList();

        KernelServices servicesB = super.installInController(operations);

        ModelNode modelB = servicesB.readWholeModel();

        super.compare(modelA, modelB);
    }

}
