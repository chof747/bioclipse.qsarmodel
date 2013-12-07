/*******************************************************************************
 * Copyright (c) 2013  Christian Hofbauer <chof@gmx.at>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package org.chof.bioclipse.qsarmodel.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.tests.AbstractManagerTest;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.qsar.DocumentRoot;
import net.bioclipse.qsar.MetadataType;
import net.bioclipse.qsar.QsarFactory;
import net.bioclipse.qsar.QsarType;
import net.bioclipse.qsar.ResourceType;
import net.bioclipse.qsar.ResponseType;
import net.bioclipse.qsar.StructureType;
import net.bioclipse.qsar.StructurelistType;
import net.bioclipse.qsar.util.QsarResourceFactoryImpl;
import net.bioclipse.ui.business.IUIManager;

import org.chof.bioclipse.qsarmodel.business.IQsarModelManager;
import org.chof.bioclipse.qsarmodel.domain.QsarModelHandle;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public abstract class AbstractQsarModelManagerPluginTest
extends AbstractManagerTest {

    protected static IQsarModelManager qsarModel;
    protected static IUIManager ui;
    protected static ICDKManager cdk;
	private IFile qsarFile;
    private IFolder molecules;
    private String[] molfiles = {
      "propane.cml",
      "butane.cml"
    };
    
    @Before
    public void setupTestQSARProject() throws CoreException, BioclipseException, IOException, URISyntaxException {
    	ui = net.bioclipse.ui.business.Activator.getDefault().getUIManager();
    	ResourceSet resourceSet = 
    	  org.chof.bioclipse.qsarmodel.business.QsarModelManager.createQsarModelResourceSet();
    	cdk = net.bioclipse.cdk.business.Activator.getDefault().getJavaCDKManager();
    	
		//Create a qsar file --------------------------------
		DocumentRoot docRoot=QsarFactory.eINSTANCE.createDocumentRoot();
		QsarType qsar=QsarFactory.eINSTANCE.createQsarType();
		docRoot.setQsar(qsar);

	    MetadataType meta=QsarFactory.eINSTANCE.createMetadataType();
	    meta.setDatasetname( "qsarmodelmanager unit test" );
	    meta.setAuthors( "Christian Hofbauer" );
	    meta.setDescription( "A dataset to test the qsar model manager for bioclipse" );
	    meta.setLicense( "chof.org" );
	    meta.setURL( "http://www.bioclipse.net" );
	    
	    qsar.setMetadata(meta);
	    
	    qsar.setStructurelist(QsarFactory.eINSTANCE.createStructurelistType());
	    qsar.setResponselist(QsarFactory.eINSTANCE.createResponsesListType());
	   
	    
	    //write file
	    IProject project = ui.getProject(ui.newProject("qsartest"));
	    qsarFile = ui.newFile(project.getFullPath().append("qsar.xml").toString());
	    
		URI fileURI = URI.createFileURI(qsarFile.getLocation().toOSString());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xml", new QsarResourceFactoryImpl());

		Resource resource=resourceSet.createResource(fileURI);
		resource.getContents().add(docRoot);
		
		//add molecules folder
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IPath path = project.getFullPath();
        
        path = path.append("molecules");
        molecules = root.getFolder(path);
        molecules.create(false, true, null);
        
        //add molecules
        for (String molFile : molfiles) {
            java.net.URI uri = getClass().getResource("/testFiles/" + molFile).toURI();
            String sourcePath = FileLocator.toFileURL(uri.toURL()).getFile();
            IFile destFile = root.getFile(molecules.getFullPath().append(molFile));
            destFile.create(new FileInputStream(sourcePath), true, null);
        }
           
       
		
		resource.save(null);
    }
       
    @After
    public void tearDownQSARProject() throws CoreException, BioclipseException {
        IProject project = ui.getProject("qsartest");
        project.delete(true, null);    	
    }    
    
    @Test
    public void testLoad() {
    	checkModelHandle(qsarModel.load(qsarFile.getFullPath().toString()));
    }
    
    @Test
    public void testLoad_IFile() {
    	checkModelHandle(qsarModel.load(qsarFile));
    }

	/**
	 * @param handle
	 */
	private void checkModelHandle(QsarModelHandle handle) {
		MetadataType meta = handle.getModel().getMetadata();
    	Assert.assertEquals(qsarFile, handle.getResource());
    	Assert.assertEquals("qsarmodelmanager unit test", meta.getDatasetname());
	}
	
	@Test
	public void testClose() {
	    QsarModelHandle handle = qsarModel.load(qsarFile);
	    checkModelHandle(handle);
	    
	    handle = qsarModel.close(handle, false);
	    Assert.assertNull(handle.getModel());
	    Assert.assertNull(handle.getRoot());
	    
	    //change model
	    handle = qsarModel.load(qsarFile);
	    handle.getModel().getMetadata().setLicense("gnu");
        handle = qsarModel.close(handle, true);
        //check if model was saved
        handle = qsarModel.load(qsarFile);
        Assert.assertEquals("gnu", handle.getModel().getMetadata().getLicense());
	}
	
	@Test
	@Ignore
	public void testSave() {
	    //save implicitly tested by close method
	}
	
	@Test
	public void testAddStructureWithProperty() throws BioclipseException {
	   String[] testInput = { "propane.cml", "butane.cml"};
	   String[] testRespones = new String[2];
	   
	   Map<IFile, String> data = createMoleculeSet(testInput);
       QsarModelHandle handle = qsarModel.load(qsarFile);

       int i = 0;
	   for (IFile file : data.keySet()) {
	       qsarModel.addStructureWithProperty(handle, file, "Activity");
	       testRespones[i++] = data.get(file);
	   }
	   
       StructurelistType structures = handle.getModel().getStructurelist();
       i = 0;
       for(ResourceType resource : structures.getResources()) {
           Assert.assertEquals(testInput[i], resource.getId());
           Assert.assertEquals(molecules.getFile(testInput[i]).getFullPath().toString(),
               resource.getFile());
           StructureType structure = resource.getStructure().get(0);
           String struct_id = structure.getId();
           
           for(ResponseType response : handle.getModel().getResponselist().getResponse()) {
               if (response.getStructureID() == struct_id) {
                   Assert.assertEquals(testRespones[i], response.getValue());
               }
           }
           i++;
       }
    }

    private Map<IFile, String> createMoleculeSet(String[] testInput) {
        HashMap<IFile, String> result = new HashMap<IFile, String>();
        
        for(String input : testInput) {
            IFile molfile = molecules.getFile(input);
            ICDKMolecule mol = cdk.loadMolecule(molfile);
            String property = mol.getAtomContainer().getProperty("Activity");
            result.put(molfile, property);
        }
        
        return result;
    }

    public Class<? extends IBioclipseManager> getManagerInterface() {
    	return IQsarModelManager.class;
    }
}
