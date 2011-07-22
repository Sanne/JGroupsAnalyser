package org.jgroups.tools.analyser.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.ui.PlatformUI;
import org.jgroups.tools.analyser.model.FullPacket;
import org.jgroups.tools.analyser.service.Refresher;
import org.jgroups.tools.analyser.views.PacketTableView;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.util.JNetPcapFormatter;


public class ProcessPacketHandler extends Thread{

	private LinkedBlockingQueue<PcapPacket> q;
	private PacketTableView pv;
	private long lastTimeStamp ;
	private int nbPacketBeforeRefreshLimit = 25;
	static public int nbPacketProcessed;
	private int maxTimeBeforeRefresh = 1000;
	public int timeToRefresh;
	private FullPacket packet;
	public boolean runningStatus = true;
	private long maxQueueSize = 0;
	
	public ProcessPacketHandler(PacketTableView pv, LinkedBlockingQueue<PcapPacket> q, int timeToRefresh)  {
		this.q = q;
		this.pv = pv;
		this.timeToRefresh = timeToRefresh;
		setName("ProcessPacketHandler");
	}
	
	
	public void run() {
		
		long newTimeStamp = new Date().getTime();
		boolean lastRefresh = false;
		while(runningStatus) {
			
			if(! q.isEmpty() ) {
				if(q.size() > maxQueueSize) {
					maxQueueSize = q.size();
				}
				PcapPacket p = q.poll();
				if(p != null) {

					packet = new FullPacket(p);

					lastTimeStamp = newTimeStamp;
					if (packet.isComplete() && packet.isJGroupsPacket()) {
						lastRefresh = false;
						packet.setNumber(++ nbPacketProcessed );

						pv.getInput().add(packet);
						
						if( ((nbPacketProcessed % nbPacketBeforeRefreshLimit) == 0) || (newTimeStamp - lastTimeStamp) > maxTimeBeforeRefresh || q.isEmpty()) {
							long start = new Date().getTime();
							PlatformUI.getWorkbench().getDisplay().syncExec(new Refresher(pv, pv.getRevealed(), packet));
						}
					}
				}
			}
			if(q.isEmpty() && ! lastRefresh) {
				lastRefresh = true;
				PlatformUI.getWorkbench().getDisplay().syncExec(new Refresher(pv, false, packet));
				
				
			}

		}
	}
	
	
}
