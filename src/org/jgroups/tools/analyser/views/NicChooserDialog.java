package org.jgroups.tools.analyser.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.jgroups.tools.analyser.service.PacketSniffer;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;


public class NicChooserDialog extends TitleAreaDialog {

	private Button ok;
	private Shell shell;
	private List<PcapIf> alldevs = new ArrayList<PcapIf>();
	private StringBuilder errbuf = new StringBuilder();  
	private Logger log = Logger.getLogger(PacketSniffer.class);
	private PcapIf device;
	private Integer nic;
	
	public NicChooserDialog(Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		setTitle("Nic chooser");
		setMessage("Choose the NIC to dump ';'", IMessageProvider.INFORMATION);
		return control;	
	}
	
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);
		Group group1 = new Group(parent, SWT.SHADOW_IN);
		
		group1.setText("What network interface to sniff ?");
		group1.setLayout(new RowLayout(SWT.VERTICAL));
		
		int r = Pcap.findAllDevs(alldevs, errbuf);  
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
			log.error("Can't read list of devices, error is %s" + errbuf.toString());  
		} else {
			for(int i = 0; i < alldevs.size(); i ++) {
				Button b = new Button(group1, SWT.RADIO);
				b.setText(alldevs.get(i).getDescription() == null ? alldevs.get(i).getName() : alldevs.get(i).getDescription());
				b.setData(i);
				b.addSelectionListener(new SelectionListener() {
					
					public void widgetSelected(SelectionEvent e) {
						nic = (Integer) ((Button)e.getSource()).getData();
					}
					
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			}
		}
		
		return parent;
	};

	protected void createButtonsForButtonBar(Composite parent) {
		((GridLayout) parent.getLayout()).numColumns++;

		ok = new Button(parent, SWT.PUSH);
		ok.setText("Sniff");
		ok.setFont(JFaceResources.getDialogFont());
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
	}
	public PcapIf getNicChoosen() {
		return alldevs.get(nic);
	}
	
}
