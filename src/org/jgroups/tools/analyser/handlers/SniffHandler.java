package org.jgroups.tools.analyser.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.State;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;
import org.jgroups.tools.analyser.service.IPcapService;
import org.jgroups.tools.analyser.service.PacketSniffer;
import org.jgroups.tools.analyser.service.SourceProvider;
import org.jgroups.tools.analyser.views.NicChooserDialog;
import org.jgroups.tools.analyser.views.PacketTableView;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;


public class SniffHandler extends AbstractHandler {

	public boolean started = false;
	
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public Object execute(ExecutionEvent event) throws ExecutionException {

		final PacketTableView pv = (PacketTableView)HandlerUtil.getActivePart(event).getSite().getPage().findView("JGroupsAnalyser.PacketTableView");
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
	    NicChooserDialog nicDialog = new NicChooserDialog(window.getShell());
	    nicDialog.open();

//		Shell shell = null;
//		try {
//			shell = HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell();
//		} catch (ExecutionException e) {
//			MessageDialog.openInformation(shell, "Preferences Error", e.getMessage());
//		}
		
		IPcapService p = (IPcapService) PlatformUI.getWorkbench().getService(IPcapService.class);
	    PcapIf nic = nicDialog.getNicChoosen();
	    LinkedBlockingQueue<PcapPacket> q = new LinkedBlockingQueue<PcapPacket>();
	    IWorkbench wb = PlatformUI.getWorkbench();
	    IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
	    
	    ISourceProviderService sourceProviderService = (ISourceProviderService) win.getService(ISourceProviderService.class);
	    SourceProvider commandStateService = (SourceProvider) sourceProviderService.getSourceProvider(SourceProvider.PLAY_STATE);
	    commandStateService.tooglePlay();
	    commandStateService.toogleStop();
	    commandStateService.toogleLoad();
	    
		PacketSniffer packetSniffer = new PacketSniffer(nic);
		p.setPcapHandler(packetSniffer);
		p.setQueue(q);
		
		MyPcapPacketHandler handler = new MyPcapPacketHandler(q);

		ProcessPacketHandler pph =  new ProcessPacketHandler(pv,q, commandStateService);
		p.setProcessPacketHandler(pph);
		packetSniffer.setPacketHandler(handler);
		packetSniffer.start();
		pph.start();

		return null;
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
