/*******************************************************************************
 * Copyright (c) 2000, 2005 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.debug.internal.ui.preferences;

import org.eclipse.cdt.debug.ui.ICDebugUIConstants;

/**
 * Constants defining the keys to be used for accessing preferences inside the debug ui plugin's preference bundle.
 * In descriptions (of the keys) below describe the preference stored at the given key. 
 * The type indicates type of the stored preferences
 * The preference store is loaded by the plugin (CDebugUIPlugin).
 * @see CDebugUIPlugin.initializeDefaultPreferences(IPreferenceStore) - for initialization of the store
 */
public interface ICDebugPreferenceConstants {

	/**
	 * Boolean preference controlling whether the debugger shows full paths. When <code>true</code> the debugger will show full paths in newly opened views.
	 */
	public static final String PREF_SHOW_FULL_PATHS = ICDebugUIConstants.PLUGIN_ID + ".cDebug.show_full_paths"; //$NON-NLS-1$

	/**
	 * Boolean preference controlling whether primitive types types display hexidecimal values.
	 */
	public static final String PREF_SHOW_HEX_VALUES = ICDebugUIConstants.PLUGIN_ID + ".cDebug.showHexValues"; //$NON-NLS-1$

	/**
	 * Boolean preference controlling whether primitive types types display char values.
	 */
	public static final String PREF_SHOW_CHAR_VALUES = ICDebugUIConstants.PLUGIN_ID + ".cDebug.showCharValues"; //$NON-NLS-1$

    public static final String PREF_DISASM_SHOW_INSTRUCTIONS = ICDebugUIConstants.PLUGIN_ID + ".disassembly.showInstructions"; //$NON-NLS-1$

    public static final String PREF_DISASM_SHOW_SOURCE = ICDebugUIConstants.PLUGIN_ID + ".disassembly.showSource"; //$NON-NLS-1$

    /**
     * Specifies the conditions under which the disassembly editor will be activated
     */
    public static final String PREF_OPEN_DISASSEMBLY_MODE = ICDebugUIConstants.PLUGIN_ID + ".openDisassemblyMode"; //$NON-NLS-1$
}
