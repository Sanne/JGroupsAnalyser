package org.jgroups.tools.analyser.service;

import java.util.concurrent.LinkedBlockingQueue;

import org.jgroups.tools.analyser.handlers.ProcessPacketHandler;
import org.jnetpcap.Pcap;

public class PcapService implements IPcapService {

	private PacketSniffer packetSniffer;
	private LinkedBlockingQueue q;
	private ProcessPacketHandler pph;
	
	public PcapService() {
	}

	public PacketSniffer getPacketSniffer() {
		return packetSniffer;
	}
	
	public void setPcapHandler(PacketSniffer pcap) {
		this.packetSniffer = pcap;
	}

	public Pcap getPcapHandler() {
		return packetSniffer.getPcap();
	}

	public void setQueue(LinkedBlockingQueue q) {
		this.q = q;
		
	}

	public LinkedBlockingQueue getQueue() {
		return q;
	}

	@Override
	public ProcessPacketHandler getProcessPacketHandler() {
		return pph;
	}

	@Override
	public void setProcessPacketHandler(ProcessPacketHandler pph) {
		this.pph = pph;
		
	}

}
