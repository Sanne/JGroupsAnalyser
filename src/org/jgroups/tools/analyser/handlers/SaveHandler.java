package org.jgroups.tools.analyser.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jgroups.tools.analyser.model.FullPacket;
import org.jgroups.tools.analyser.views.PacketTableView;


public class SaveHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {	
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		FileDialog dialog = new FileDialog(window.getShell(), SWT.SAVE);
		dialog.setFilterExtensions(new String[] {"*.csv", "*.*"});
		dialog.setFilterNames(new String[] {"CSV Files", "All Files"});
		String fileSelected = dialog.open();
		if(fileSelected == null) {
			return null;
		}

		IViewPart v = window.getActivePage().findView(PacketTableView.ID);
		ArrayList packets = ((PacketTableView)(v)).getInput();
		writePackets(packets, fileSelected);

		return null;
	}

	private void writePackets(ArrayList<FullPacket> packets, String fileSelected) {
		boolean csv = false;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(fileSelected));
			BufferedWriter bw = new BufferedWriter(new PrintWriter(fos));
			for(FullPacket p : packets) {
				bw.write(p.getDateTime() + ";" + p.getFrom() + ";" + p.getTo() + ";" + p.getGlobalSize()+ ";" + p.getSize() + ";" + p.getDescription().replace('\n', '#') + ";" + p.getPayload() + "\n");
			}
			bw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
