package org.jgroups.tools.analyser.service;

import java.util.concurrent.LinkedBlockingQueue;

import org.jgroups.tools.analyser.handlers.ProcessPacketHandler;
import org.jnetpcap.Pcap;

public interface IPcapService {

	public void setPcapHandler(PacketSniffer pcap);
	public Pcap getPcapHandler();
	public void setQueue(LinkedBlockingQueue q);
	public LinkedBlockingQueue getQueue();
	public PacketSniffer getPacketSniffer();
	public ProcessPacketHandler getProcessPacketHandler();
	public void setProcessPacketHandler(ProcessPacketHandler pph);
	
}
