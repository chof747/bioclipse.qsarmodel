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

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.qsar.DocumentRoot;
import net.bioclipse.qsar.QsarFactory;
import net.bioclipse.qsar.QsarPackage;
import net.bioclipse.qsar.QsarType;
import net.bioclipse.qsar.ResourceType;
import net.bioclipse.qsar.ResponseType;
import net.bioclipse.qsar.StructureType;
import net.bioclipse.qsar.StructurelistType;
import net.bioclipse.qsar.TypeType;
import net.bioclipse.qsar.util.QsarAdapterFactory;
import net.bioclipse.qsar.util.QsarResourceFactoryImpl;
import net.sf.bibtexml.BibtexmlPackage;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;


public class QsarModelManager implements IBioclipseManager {

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(QsarModelManager.class);
    
    private DocumentRoot qsarRoot;
    private Resource qsarModelResource;
    
    private ICDKManager cdkManager;

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
    	qsarRoot = null;
    	qsarModelResource = null;
    	
    	cdkManager = net.bioclipse.cdk.business.Activator.getDefault().getJavaCDKManager();
    	editingDomain = null;
    }    
    
    /**
     * Provides access to the qsar model loaded by file
     * @return a reference to the qsar model or null if no model is loaded
     */
    protected QsarType getQsarModel() {
    	if (qsarRoot != null) {
    		return qsarRoot.getQsar();
    	} else {
    		return null;
    	}
    }
    
    public boolean load(final IFile file) {
		
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

        // Get the URI of the model file.
        URI fileURI = URI.createURI(file.getLocationURI().toString());

        qsarModelResource = resourceSet.getResource(fileURI, true);
		
		qsarRoot = (DocumentRoot) qsarModelResource.getContents().get(0);
		return true;
    }
    
    public boolean save() {
    	qsarModelResource.getContents().clear();
    	qsarModelResource.getContents().add(qsarRoot);
    	try {
			qsarModelResource.save(null);
			return true;	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
    
    public boolean close(Boolean saveChanges) {
    	//TODO: Implement close method
    	return false;
    }
    
    
    public String addStructure(IFile structure) {
    	return addStructure(structure, null);
    }
    
    /**
     * Adds a structure to the qsar model with a given name
     * @param structure
     * @param name
     */
    public String addStructure(IFile structure, String name) {
    	
    	QsarType qsarModel = getQsarModel();
    	
    	if (qsarModel != null) {    		
    		//QSAR Model loaded
	
			//obtain the structure list
			StructurelistType structureList = getQsarModel().getStructurelist();
			
			//Load structure to obtain name and id
			try {
				ICDKMolecule molecule = cdkManager.loadMolecule(structure);
				
				if (name != null) {
					molecule.setName(name);
				}
				
				//setup the structure
				ResourceType resStructure = createResource(
						structure.getFullPath().toString(), 
						molecule);
				StructureType molStructure = createStructure(molecule);
				
				CompoundCommand cCmd = new CompoundCommand();

				//add the structure to the qsar model
				cCmd.append(AddCommand.create(getEditingDomain(), 
			              structureList, 
			              QsarPackage.Literals.STRUCTURELIST_TYPE__RESOURCES, 
			              resStructure));		
			    cCmd.append(AddCommand.create(getEditingDomain(), 
  					  resStructure, 
  					  QsarPackage.Literals.RESOURCE_TYPE__STRUCTURE, 
  					  molStructure));
				cCmd.execute();
				
				return resStructure.getId();
				
			} catch (BioclipseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}			
			
    	} else {
    		// no QSAR Model loaded
    		return null;
    	}
    }
    
    public boolean addResponse(String structureResource,
			                   String unit,
			                   String value) {
    	
    	QsarType qsarModel = getQsarModel();
    	
    	if (qsarModel != null) {    		   	
    		//QSAR Model loaded
    		    		
    		ResponseType response=QsarFactory.eINSTANCE.createResponseType();
    		response.setStructureID( structureResource);
    		response.setValue(value);
    		response.setUnit(unit);    		
			CompoundCommand cCmd = new CompoundCommand();

			//add the structure to the qsar model
			cCmd.append(AddCommand.create(getEditingDomain(), 
		              qsarModel.getResponselist(), 
		              QsarPackage.Literals.RESPONSES_LIST_TYPE__RESPONSE, 
		              response));		
			cCmd.execute();
    		
    		return true;
    	} else {
    		//QSAR Model not loaded
    		return false;
    	}
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

    /**
     * Creates a structure for a specific molecule
     * @param molecule
     * @return
     * @throws BioclipseException
     */
	private StructureType createStructure(ICDKMolecule molecule)
			throws BioclipseException {
		StructureType molStructure = QsarFactory.eINSTANCE.createStructureType();
		
		molStructure.setId(molecule.getUID());
		molStructure.setInchi(molecule.getInChIKey(null));
		molStructure.setResourceindex(0);
		return molStructure;
	}

    /**
     * Creates a resource for a specific molecule in a specific file location 
     * @param path the path to the molecule's file
     * @param molecule a reference to the molecule
     * @return
     */
	private ResourceType createResource(String path, ICDKMolecule molecule) {
		
		ResourceType resStructure=QsarFactory.eINSTANCE.createResourceType();
		resStructure.setId("res-"+molecule.getUID());
		resStructure.setName(molecule.getName());
		resStructure.setFile(path);
		resStructure.setType( TypeType.TEXT );
		resStructure.setChecksum( "N/A" );
		return resStructure;
	}
}
