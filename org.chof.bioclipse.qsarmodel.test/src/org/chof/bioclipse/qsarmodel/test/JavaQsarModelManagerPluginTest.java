/*******************************************************************************
 * Copyright (c) 2013  Christian Hofbauer <chof@gmx.at>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contact: http://www.bioclipse.net/    
 ******************************************************************************/
package org.chof.bioclipse.qsarmodel.test;

import net.bioclipse.managers.business.IBioclipseManager;

import org.junit.BeforeClass;

public class JavaQsarModelManagerPluginTest
    extends AbstractQsarModelManagerPluginTest {

    @BeforeClass public static void setup() {
        qsarModel = org.chof.bioclipse.qsarmodel.Activator.getDefault()
            .getJavaQsarModelManager();
    }

	@Override
	public IBioclipseManager getManager() {
		return qsarModel;
	}
}
