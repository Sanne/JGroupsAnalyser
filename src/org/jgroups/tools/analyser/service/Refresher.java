package org.jgroups.tools.analyser.service;

import java.util.ArrayList;

import org.jgroups.tools.analyser.model.FullPacket;
import org.jgroups.tools.analyser.views.PacketTableView;

public class Refresher extends Thread {

	private boolean reveal = false;
	private PacketTableView pv;
	private FullPacket fp;
	
	public void setReveal(boolean reveal) { this.reveal = reveal; }

	public Refresher(PacketTableView pv, boolean reveal, FullPacket packet) {
		this.pv = pv;
		this.reveal = reveal;
		this.fp = packet;
	}

	public void run() {
		pv.getTableViewer().refresh();
		if (reveal && (ArrayList) pv.getTableViewer().getInput() != null) {
			pv.getTableViewer().reveal(fp);
		}
	}
}
