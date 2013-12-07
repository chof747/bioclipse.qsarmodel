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

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.TestClasses;
import net.bioclipse.core.TestMethods;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.jobs.BioclipseUIJob;
import net.bioclipse.managers.business.IBioclipseManager;

import org.chof.bioclipse.qsarmodel.domain.QsarModelHandle;
import org.eclipse.core.resources.IFile;

@PublishedClass(value = "TODO: Describe the manager here.")
@TestClasses("org.chof.bioclipse.qsarmodel.test.QsarModelManagerTest,"
    + "org.chof.bioclipse.qsarmodel.test.AbstractQsarModelManagerPluginTest")
public interface IQsarModelManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
        params = "String filePath",
        methodSummary = "Loads an existing QSAR model resoure which will be used for further operations")
    @TestMethods("testLoad")
    public
        QsarModelHandle load(String filePath);

    @TestMethods("testLoad_IFile")
    public QsarModelHandle load(IFile file);

    @Recorded
    @PublishedMethod(params = "QsarModelHandle modelHandle",
        methodSummary = "Saves the QSAR model provided by the model descriptor")
    public
        boolean save(QsarModelHandle modelHandle);

    @Recorded
    @PublishedMethod(params = "QsarModelHandle modelHandle",
        methodSummary = "Removes all structures and responses stored in the ")
    public Integer clear(QsarModelHandle modelHandle) throws BioclipseException;

    public void
        clear(QsarModelHandle modelHandle, BioclipseUIJob<Integer> uiJob)
            throws BioclipseException;

    @Recorded
    @PublishedMethod(
        params = "QsarModelHandle modelHandle, Boolean saveChanges",
        methodSummary = "Closes the QSAR Model provided by the model descriptor")
    public
        QsarModelHandle close(QsarModelHandle modelHandle, Boolean saveChanges);

    @Recorded
    @PublishedMethod(
        params = "QsarModelHandle modelHandle, String structurePath",
        methodSummary = "Adds a structure from a structure file")
    public String
        addStructure(QsarModelHandle modelHandle, String structurePath)
            throws BioclipseException;

    public String addStructure(QsarModelHandle modelHandle, IFile structure)
        throws BioclipseException;

    public void addStructure(QsarModelHandle modelHandle,
        String structurePath,
        BioclipseUIJob<String> uiJob) throws BioclipseException;

    public void addStructure(QsarModelHandle modelHandle,
        IFile structure,
        BioclipseUIJob<String> uiJob) throws BioclipseException;

    @Recorded
    @PublishedMethod(
        params = "QsarModelHandle modelHandle, String structurePath, String responseValue",
        methodSummary = "Adds a structure with a given response Value")
    public
        String addStructureWithValue(QsarModelHandle modelHandle,
            String structurePath,
            String responseValue) throws BioclipseException;

    public String addStructureWithValue(QsarModelHandle modelHandle,
        IFile structure,
        String responseValue) throws BioclipseException;

    public void addStructureWithValue(QsarModelHandle modelHandle,
        String structurePath,
        String responseValue,
        BioclipseUIJob<String> uiJob) throws BioclipseException;

    public void addStructureWithValue(QsarModelHandle modelHandle,
        IFile structure,
        String responseValue,
        BioclipseUIJob<String> uiJob) throws BioclipseException;

    @Recorded
    @PublishedMethod(
        params = "QsarModelHandle modelHandle, String structurePath, String propertyName",
        methodSummary = "Adds a structure with and sets the response value to a "
            + "specifc property of the molecule")
    public
        String addStructureWithProperty(QsarModelHandle modelHandle,
            String structurePath,
            String propertyName) throws BioclipseException;

    public String addStructureWithProperty(QsarModelHandle modelHandle,
        IFile structure,
        String propertyName) throws BioclipseException;

    public void addStructureWithProperty(QsarModelHandle modelHandle,
        String structurePath,
        String propertyName,
        BioclipseUIJob<String> uiJob) throws BioclipseException;

    public void addStructureWithProperty(QsarModelHandle modelHandle,
        IFile structure,
        String propertyName,
        BioclipseUIJob<String> uiJob) throws BioclipseException;

}
