package org.jgroups.tools.analyser.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.jgroups.tools.analyser.handlers.ProcessPacketHandler;
import org.jgroups.tools.analyser.model.FullPacket;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.packet.PcapPacketHandler;

public class PacketSniffer extends Thread {

	private List<PcapIf> alldevs = new ArrayList<PcapIf>();
	private StringBuilder errbuf = new StringBuilder();  
	private Logger log = Logger.getLogger(PacketSniffer.class);
	private PcapIf device;
	private String pcapFile;
	private int snaplen = 64 * 1024; // no trucation
	private int flags = Pcap.MODE_PROMISCUOUS;
	private int timeout = 1; 
	private Pcap pcap;
	PcapPacketHandler<String> jpacketHandler = null;
	private long nbPackets;
	private int loop;
	
	public PacketSniffer(PcapIf device) {
		this.device = device;
		initNic();
		loop = -1;
	}

	public PacketSniffer(String pcapFile) {
		this.pcapFile = pcapFile;
		initPcapFile();
		loop = 1;
	}

	private void initPcapFile() {
		StringBuilder sb = new StringBuilder();
		pcap = Pcap.openOffline(pcapFile, sb);
	}
	
	private void initNic() {
		pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
	}

	public void setPacketHandler(PcapPacketHandler jpacketHandler) {
		this.jpacketHandler = jpacketHandler;
	}

	public void run() {
		if(jpacketHandler != null) {
			if(loop == -1) {
				pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, "JGroupsAnalyser");
				
			}
			if(loop == 1) {
				int r = 0;
				while((r = pcap.dispatch(loop, jpacketHandler, "JGroupsAnalyser")) != 0) {
					nbPackets ++;
				} 
				System.out.println("erreur pcap return : " + r);
			}
		}
		System.out.println("ending load of " + nbPackets);
	}
	
	public Pcap getPcap() {
		return pcap;
	}
	
}
