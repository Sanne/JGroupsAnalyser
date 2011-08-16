package org.jgroups.tools.analyser.views;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jgroups.tools.analyser.Activator;
import org.jgroups.tools.analyser.model.FullPacket;
import org.jgroups.tools.analyser.model.Payload;

public class PacketTableView extends ViewPart {
	public PacketTableView() {
	}
	public static final String ID = "JGroupsAnalyser.PacketTableView";

	private TableViewer viewer;
	boolean revealed = true;
	private int nbPacket = 0;
	final int TEXT_MARGIN = 0;
	ArrayList<FullPacket> input = new ArrayList<FullPacket>();
	
	/**
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
//			viewer.refresh();

		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent instanceof ArrayList) {
				return ((ArrayList) parent).toArray();
			}
	        return null;
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			FullPacket fp = (FullPacket)obj;
			switch(index) {
			case 0 : return ""+fp.getPacketNumber();
			case 1 : return fp.getDateTime();
			case 2 : return fp.getFrom();
			case 3 : return fp.getTo();
			case 4 : return fp.getGlobalSize();
			case 5 : return fp.getSize();
			case 6 : return fp.getDescription();
			case 7 : return fp.getPayload();
			default : return "";
			
			}
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		
		viewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		for(int i = 0; i < FullPacket.columns.length; i ++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(FullPacket.columns[i]);
			column.getColumn().setWidth(FullPacket.bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}
		
//		viewer.addFilter(new ViewerFilter() {
//			
//			@Override
//			public boolean select(Viewer viewer, Object parentElement, Object element) {
//				return false;
//			}
//		});
//		
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		
		viewer.setInput(input);

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		table.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem)event.item;
				String text = item.getText(event.index);
				Point size = event.gc.textExtent(text);
				event.width = size.x + 2 * TEXT_MARGIN; 
				event.height = Math.max(event.height, size.y + TEXT_MARGIN);
			}
		});
		table.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {
				event.detail &= ~SWT.FOREGROUND;
			}
		});
		table.addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem)event.item;
				String text = item.getText(event.index);
				/* center column 1 vertically */
				int yOffset = 0;
				event.gc.drawText(text, event.x + TEXT_MARGIN, event.y + yOffset, true);
			}
		});
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty() == "PAYLOAD") {
					Payload.treatment = Activator.getDefault().getPreferenceStore().getString("PAYLOAD");
				}
			}
		});
	
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getProperty() == "JAR_PATH") {
					Payload.jarPath = event.getNewValue().toString();
					Payload.setClassLoader();
				}
				
			}
		});
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
	
	public ArrayList getInput() {
		return input;
	}
	
	public void switchReveal() {
		revealed = ! revealed;
	}
	public int getNbPacket() {
		return nbPacket ++;
	}
	
	public TableViewer getTableViewer() {
		return viewer;
	}
	public boolean getRevealed() {
		return revealed;
	}
	public void clearInput() {
		input = new ArrayList<FullPacket>();
		viewer.setInput(input);
		viewer.getTable().clearAll();
		
		
		
		
	}
	
	
}