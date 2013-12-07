package org.chof.bioclipse.qsarmodel.domain;

import org.eclipse.core.resources.IResource;

import net.bioclipse.qsar.DocumentRoot;
import net.bioclipse.qsar.QsarType;

public class QsarModelHandle {
	
	protected DocumentRoot root;
	protected IResource resource;
	
	public QsarModelHandle() {
		super();
		root = null;
		resource = null; 
	}
	
	public QsarModelHandle(DocumentRoot root) {
		super();
		setRoot(root);
		resource = null;
	}

	public QsarModelHandle(DocumentRoot root, IResource file) {
		super();
		setRoot(root);
		setResource(file);
	}

	/**
	 * @return the document root of the qsarModel
	 */
	public synchronized DocumentRoot getRoot() {
		return root;
	}

	/**
	 * @param root the document root to set for the qsar model
	 */
	public synchronized void setRoot(DocumentRoot root) {
		this.root = root;
	}

	/**
	 * @return the file containing the qsar model
	 */
	public synchronized IResource getResource() {
		return resource;
	}

	/**
	 * @param file the file to set the qsar model
	 */
	public synchronized void setResource(IResource file) {
		this.resource = file;
	}
	
	/**
	 * Provides a handle to the actual qsar model of the document root
	 * @return a reference to the qsar model
	 */
	public synchronized QsarType getModel() {
		if (root != null) {
			return root.getQsar();
		}
		else {
			return null;
		}
	}
	
	/**
	 * Clears the information of this model handle and therefore invalidates the handle
	 */
    public void close() {
        root = null;
        resource = null;
        
    }
}
