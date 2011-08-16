package org.jgroups.tools.analyser.handlers;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.jgroups.tools.analyser.model.FullPacket;
import org.jgroups.tools.analyser.service.IPcapService;
import org.jgroups.tools.analyser.service.PacketSniffer;
import org.jgroups.tools.analyser.service.Refresher;
import org.jgroups.tools.analyser.service.SourceProvider;
import org.jgroups.tools.analyser.views.PacketTableView;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;

public class ProcessPacketHandler extends Thread implements IRunnableWithProgress {

	private LinkedBlockingQueue<PcapPacket> q;
	private PacketTableView pv;
	private long lastTimeStamp ;
	private int nbPacketBeforeRefreshLimit = 250;
	static public int nbPacketProcessed;
	private int maxTimeBeforeRefresh = 1000;
	public int timeToRefresh;
	private FullPacket packet;
	public boolean runningStatus = true;
	private long maxQueueSize = 0;
	private SourceProvider provider;
	private boolean monitorLimit;
	private IProgressMonitor monitor;

	public ProcessPacketHandler(PacketTableView pv, LinkedBlockingQueue<PcapPacket> q, SourceProvider commandStateService)  {
		this.q = q;
		this.pv = pv;
		this.provider = commandStateService;

	}

	public void run() {
		run(null);
	}
	
	public void run(final IProgressMonitor monitor ) {
		this.monitor = monitor;
		long newTimeStamp = new Date().getTime();
		boolean lastRefresh = false;
		long nbPacket = q.size();
		int i = 0;
		if(monitor != null) {
			monitor.beginTask("Loading pcap file", 100);
		}

		while(runningStatus) {

			if(! q.isEmpty() ) {
				if(monitor != null) {
					monitor.subTask("loading ...");
				}
				PcapPacket p = q.poll();
				if(monitor != null && (q.size() % (nbPacket/100)) == 0) {
					i++;
					if(monitor != null) {
						PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
							public void run() {
								monitor.worked(1);
							}
						});
					}
				}
				if((monitor !=  null && monitor.isCanceled()) ) {
					runningStatus = false;
					provider.toogleLoad();
					provider.tooglePlay();
					if(monitor != null) {
						closeLoadingProcess();
					}
				}
				if(p != null) {
					packet = new FullPacket(p);
					lastTimeStamp = newTimeStamp;
					if (packet.isComplete() && packet.isJGroupsPacket()) {
						lastRefresh = false;
						packet.setNumber(++ nbPacketProcessed );
						pv.getInput().add(packet);
							
						if(monitor == null) {
							if( ((nbPacketProcessed % nbPacketBeforeRefreshLimit) == 0) || (newTimeStamp - lastTimeStamp) > maxTimeBeforeRefresh || q.isEmpty()) {
								long start = new Date().getTime();
								PlatformUI.getWorkbench().getDisplay().syncExec(new Refresher(pv, pv.getRevealed(), packet));
							}
						}
					}
				}
			}
			if(monitor != null && q.isEmpty()) {
				runningStatus = false;
				provider.toogleLoad();
				provider.tooglePlay();
				PlatformUI.getWorkbench().getDisplay().syncExec(new Refresher(pv, false, packet));
			}
			if(q.isEmpty() && ! lastRefresh && ! runningStatus) {
				lastRefresh = true;
				if(monitor != null) {
					closeLoadingProcess();				
				}
				runningStatus = false;

			}

		}
	}
	private void closeLoadingProcess() {
		monitor.done();
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

	}

}
