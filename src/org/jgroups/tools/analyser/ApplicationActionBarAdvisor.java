package org.jgroups.tools.analyser;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		
		Action myAction = new Action("eee", null) {
		    public void run() {
		        // Perform action
		    }
		};
		
		configurer.getCoolBarManager().add(myAction);
	}
	protected void makeActions(IWorkbenchWindow window) {


	}
	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		super.fillCoolBar(coolBar);

//		ControlContribution control = new ControlContribution("myfilter") {
//			
//			@Override
//			protected Control createControl(Composite parent) {
//				CCombo combo = new CCombo(parent, SWT.NONE | SWT.DROP_DOWN | SWT.READ_ONLY | SWT.CHECK);
//
//				combo.add("String 1");
//				combo.add("String 2");
//				combo.add("String 3");
//				combo.add("String 4");
//				combo.setTextLimit(10);
//				combo.select(0);
//
//				combo.addSelectionListener(
//				new SelectionListener()
//				{
//
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						MessageDialog.openInformation(null, "My App", "Item at "  + ((Combo)e.getSource()).getItem(((Combo)e.getSource()).getSelectionIndex()) + " clicked.");
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void widgetDefaultSelected(SelectionEvent e) {
//						// TODO Auto-generated method stub
//						
//					}
//				});
//
//				return combo;
//
//			}
//		};
//
//		IToolBarManager toolbar1 = new ToolBarManager(SWT.FLAT | SWT.RIGHT_TO_LEFT);
//		coolBar.add(new ToolBarContributionItem(toolbar1, "label"));
//		toolbar1.add(control);
//		toolbar1.add(control);
		
	}
}
