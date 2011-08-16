package org.jgroups.tools.analyser.handlers;

import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;
import org.jgroups.tools.analyser.model.FullPacket;
import org.jgroups.tools.analyser.service.IPcapService;
import org.jgroups.tools.analyser.service.PacketSniffer;
import org.jgroups.tools.analyser.service.SourceProvider;
import org.jnetpcap.Pcap;

public class StopHandler implements IHandler {


	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IPcapService p = (IPcapService) PlatformUI.getWorkbench().getService(IPcapService.class);
		Pcap pcap = p.getPcapHandler();
		PacketSniffer packetSniffer = p.getPacketSniffer();
		ProcessPacketHandler pph = p.getProcessPacketHandler();
		pph.runningStatus = false;
		ProcessPacketHandler.nbPacketProcessed = 0;
		LinkedBlockingQueue q = p.getQueue();
		pcap.breakloop();
		pcap.close();
		packetSniffer.interrupt();
		q.clear();
		FullPacket.global_size = 0;
		FullPacket.resetFragments();
		
		
		ISourceProviderService sourceProviderService = (ISourceProviderService) HandlerUtil.getActiveWorkbenchWindow(event).getService(
				ISourceProviderService.class);
		SourceProvider commandStateService = (SourceProvider) sourceProviderService.getSourceProvider(SourceProvider.PLAY_STATE);
		commandStateService.tooglePlay();	
		commandStateService.toogleStop();
		commandStateService.toogleLoad();
		
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
		
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub
		
	}

}
