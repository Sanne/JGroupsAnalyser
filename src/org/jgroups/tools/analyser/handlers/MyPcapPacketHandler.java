package org.jgroups.tools.analyser.handlers;

import java.util.concurrent.LinkedBlockingQueue;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class MyPcapPacketHandler implements PcapPacketHandler<String> {

	
	LinkedBlockingQueue<PcapPacket> q;
	
	public MyPcapPacketHandler(LinkedBlockingQueue<PcapPacket> q) {
		this.q = q;
	}

	public void nextPacket(PcapPacket p, String user) {
		q.add(p);
	}

}
