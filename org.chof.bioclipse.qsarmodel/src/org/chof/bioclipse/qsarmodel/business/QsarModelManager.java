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
package org.chof.bioclipse.qsarmodel.business;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.jobs.IReturner;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.qsar.DocumentRoot;
import net.bioclipse.qsar.QsarPackage;
import net.bioclipse.qsar.QsarType;
import net.bioclipse.qsar.ResourceType;
import net.bioclipse.qsar.StructurelistType;
import net.bioclipse.qsar.business.IQsarManager;
import net.bioclipse.qsar.util.QsarAdapterFactory;
import net.bioclipse.qsar.util.QsarResourceFactoryImpl;
import net.sf.bibtexml.BibtexmlPackage;

import org.apache.log4j.Logger;
import org.chof.bioclipse.qsarmodel.domain.QsarModelHandle;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;


public class QsarModelManager implements IBioclipseManager {

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(QsarModelManager.class);
    
    private ICDKManager cdkManager;
    private IQsarManager qsarManager;

	private EditingDomain editingDomain;

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "qsarmodel";
    }
    
    /**
     * Standard constructor initializing qsarModel and qsarModelFile
     */
    public QsarModelManager() {
    	super();
    	
    	cdkManager = net.bioclipse.cdk.business.Activator.getDefault().getJavaCDKManager();
    	qsarManager = net.bioclipse.qsar.init.Activator.getDefault().getJavaQsarManager();
    	editingDomain = null;
    }    
    
    public QsarModelHandle load(final IFile file) {
		
		DocumentRoot qsarRoot = (DocumentRoot) obtainResource(file).getContents().get(0);
		return new QsarModelHandle(qsarRoot, file);
    }

	/**
	 * Obtains a concrete resource from a eclipse project resource handle 
	 * represented by IResource
	 * @param resource
	 * @return the concrete resource handle
	 */
	private Resource obtainResource(final IResource resource) {
		ResourceSet resourceSet = createQsarModelResourceSet();
		
		return resourceSet.getResource(
		   URI.createURI(resource.getLocationURI().toString()), true);
	}

    public boolean save(QsarModelHandle modelHandle) {
    	Resource resource = obtainResource(modelHandle.getResource());
    	resource.getContents().clear();
    	resource.getContents().add(modelHandle.getRoot());
    	try {
			resource.save(null);
			return true;	
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    }
    
    public QsarModelHandle close(QsarModelHandle modelHandle, Boolean saveChanges) {
    	if (saveChanges) {
    		save(modelHandle);
    	}
    	
    	modelHandle.close();
    	
    	return modelHandle;
    }
    
    /**
     * Clears all structures and all responses
     * @param modelHandle the descriptor for the model 
     * @param returner the returner for bioclipse job handling
     * @param monitor the monitor for bioclipse job handling
     * @throws BioclipseException
     */
    public void clear(QsarModelHandle modelHandle, IReturner<Integer> returner,
    		          IProgressMonitor monitor) throws BioclipseException {
    	
    	QsarType qsarModel = modelHandle.getModel();
    	Integer result = new Integer(0);
    	
    	if (qsarModel != null) {
    	
        	if (monitor == null) {
        		monitor = new NullProgressMonitor();
        	}
        	
        	if (!monitor.isCanceled()) {
        		qsarModel.getStructurelist().getResources().clear();
        		qsarModel.getResponselist().getResponse().clear();
        	}

    	}
    	
    	returner.completeReturn(result);
    }
    
    
    /**
     * Adds a structure only.
     * 
     * @param modelHandle the descriptor of the model to add a structure
     * @param structure the structure to add to the qsar model
     * @param returner the returner for bioclipse job handling
     * @param monitor the monitor for bioclipse job handling
     * @throws BioclipseException
     */
    public void addStructure(QsarModelHandle modelHandle, IFile structure, 
    		                   IReturner<String> returner,
    		                   IProgressMonitor monitor) throws BioclipseException {
    	addStructureWithValue(modelHandle, structure, null,
    			returner, monitor);
    }
    
    /**
     * Adds a structure to the qsar model with a given name
     * @param structure
     * @throws CoreException 
     * @throws BioclipseException 
     */
    public void addStructureWithValue(QsarModelHandle modelHandle, 
    		                 IFile structure, String responseValue,
    		                 IReturner<String> returner,
    		                 IProgressMonitor monitor) throws BioclipseException {
    	
		ICDKMolecule molecule = cdkManager.loadMolecule(structure);
		String property = "Response";
		molecule.getAtomContainer().setProperty(property, responseValue);
		try {
			cdkManager.saveMolecule(molecule, true);
		} catch (CoreException e1) {
			throw new BioclipseException("Could not save back the molecules file", e1);
		}

		addStructureToQSARModel(modelHandle, structure, returner, monitor, property);
    }

    /**
     * Adds a structure to the qsar model with a given name
     * @param structure
     * @throws CoreException 
     * @throws BioclipseException 
     */
    public void addStructureWithProperty(QsarModelHandle modelHandle, 
    		                 IFile structure, String propertyName,
    		                 IReturner<String> returner,
    		                 IProgressMonitor monitor) throws BioclipseException {
    	
		addStructureToQSARModel(modelHandle, structure, returner, monitor, propertyName);
    }

	/**
	 * @param structure
	 * @param returner
	 * @param monitor
	 * @param property
	 * @throws BioclipseException
	 */
	private void addStructureToQSARModel(QsarModelHandle modelHandle, 
			IFile structure,
			IReturner<String> returner, IProgressMonitor monitor,
			Object property) throws BioclipseException {
		
		QsarType qsarModel = modelHandle.getModel();
    	
    	if (qsarModel != null) {    		
    		//QSAR Model loaded
	
        	StructurelistType structureList = qsarModel.getStructurelist();
        	Map<IFile, Object> molprops=new HashMap<IFile, Object>();
        	if (monitor == null) {
        		monitor = new NullProgressMonitor();
        	}
    		
    		//check if file exists in resource set of the model and skip it
    		for (ResourceType existingRes : structureList.getResources()){
                if (existingRes.getName().equals(structure.getName())){
                    throw new UnsupportedOperationException(
                                           "File: " + structure.getName() 
                                         + " already exists in QSAR analysis.");
                }
            }			
    		
			molprops.put(structure, property);
			
            try {
				qsarManager.addResourcesAndResponsesToQsarModel( 
						qsarModel, 
						getEditingDomain(), 
				        molprops, 
				        true, 
				        monitor);
				
				returner.completeReturn(structure.getName());
				
			} catch (IOException e) {
				throw new BioclipseException("Problem with IO of Resources", e);
			} catch (CoreException e) {
				throw new BioclipseException("Problem with cdk core", e);
			}

			
    	} else {
    		// no QSAR Model loaded
			returner.completeReturn(null);
    	}
	}
    
	/**
	 * Initializes the resource set for the qsar model
	 * @return the initialized resource set
	 */
	public static ResourceSet createQsarModelResourceSet() {
		// Create a resource set to hold the resources.
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register the appropriate resource factory to handle all file extensions.
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put
        (Resource.Factory.Registry.DEFAULT_EXTENSION, 
         new QsarResourceFactoryImpl());

        // Register the package to ensure it is available during loading.
        resourceSet.getPackageRegistry().put
        (QsarPackage.eNS_URI, 
         QsarPackage.eINSTANCE);

        // Register the package to ensure it is available during loading.
        resourceSet.getPackageRegistry().put
        (BibtexmlPackage.eNS_URI, 
         BibtexmlPackage.eINSTANCE);

        EcoreUtil.resolveAll( resourceSet );
		return resourceSet;
	}
    
	/**
	 * Checks wether the editing domain is already defined and returns it or 
	 * sets it up
	 * 
	 * @return the editing domain for this manager
	 */
	private EditingDomain getEditingDomain() {
		if (editingDomain == null) {
			editingDomain = new AdapterFactoryEditingDomain(
				new QsarAdapterFactory(), 
				new BasicCommandStack());
		}
		return editingDomain;
	}
}
