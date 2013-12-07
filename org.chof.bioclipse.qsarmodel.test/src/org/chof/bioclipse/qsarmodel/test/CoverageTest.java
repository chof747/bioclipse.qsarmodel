/*******************************************************************************
 * Copyright (c) 2013  Christian Hofbauer <chof@gmx.at>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package org.chof.bioclipse.qsarmodel.test;

import net.bioclipse.core.tests.coverage.AbstractCoverageTest;
import net.bioclipse.managers.business.IBioclipseManager;
import org.chof.bioclipse.qsarmodel.business.IQsarModelManager;
import org.chof.bioclipse.qsarmodel.business.QsarModelManager;

/**
 * JUnit tests for checking if the tested Manager is properly tested.
 */
public class CoverageTest extends AbstractCoverageTest {
    
    private static QsarModelManager manager = new QsarModelManager();

    public IBioclipseManager getManager() {
        return manager;
    }

    public Class<? extends IBioclipseManager> getManagerInterface() {
        return IQsarModelManager.class;
    }

}