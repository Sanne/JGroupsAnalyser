package org.jgroups.tools.analyser.preferences;

import javax.swing.JLabel;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.jgroups.tools.analyser.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("JGroupsAnalyser preferences");
	}

	public void createFieldEditors() {
		
		addField(new RadioGroupFieldEditor("PAYLOAD","JGroups Payload treatment", 1,
				new String[][] { { "&Don't display payload", "nodisplay" },
								 { "&Binary to string", "binaryToString" },
								 { "&Binary to string, uncompress if COMPRESS header detected", "uncompress" },
								 { "&Uncompressed and unserialized payload (.toString())", "unserialize" } }, getFieldEditorParent()));		
		
		addField(new DirectoryFieldEditor("JAR_PATH", "&Jar path, used for deserialization:", getFieldEditorParent()));
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}