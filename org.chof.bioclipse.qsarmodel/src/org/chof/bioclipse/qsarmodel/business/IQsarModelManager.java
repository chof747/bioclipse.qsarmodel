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

import org.eclipse.core.resources.IFile;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.managers.business.GuiAction;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass(
    value="TODO: Describe the manager here."
)
public interface IQsarModelManager extends IBioclipseManager {
	
	@Recorded
	@PublishedMethod(
		params="String filePath",
		methodSummary="Loads an existing QSAR model resoure which will be used for further operations")
	public boolean load(String filePath);
	
	public boolean load(IFile file);
	
	@Recorded
	@PublishedMethod(
		params="",
		methodSummary="Saves the current QSAR model")
	public boolean save();
	
	@Recorded
	@PublishedMethod(
		params="Boolean saveChanges",
		methodSummary="Closes the current QSAR Model")
	public boolean close(Boolean saveChanges);
	
	@Recorded
	@PublishedMethod(
        params="String structurePath",
        methodSummary="Adds a structure from a structure file")
	public String addStructure(String structurePath);
	public String addStructure(IFile structure);
	
	@Recorded
	@PublishedMethod(
		params="String structurePath, String name",
		methodSummary="Adds a structure with a given name to the structure file")
	public String addStructure(String structurePath, String name);
	public String addStructure(IFile structure, String name);
	
	
	@Recorded
	@PublishedMethod(
		params="String structureResource, String unit, String value",
		methodSummary="Adds a response to the specified structure")
	public boolean addResponse(String structureResource,
			                   String unit,
			                   String value);
}
