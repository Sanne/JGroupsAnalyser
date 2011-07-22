package org.jgroups.tools.analyser.model;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jgroups.Header;
import org.jgroups.Message;
import org.jgroups.protocols.COMPRESS;
import org.jgroups.protocols.FragHeader;
import org.jgroups.protocols.PingData;
import org.jgroups.protocols.PingHeader;
import org.jgroups.tools.analyser.util.ParseMessages;
import org.jgroups.tools.analyser.util.Util;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Udp;

public class FullPacket {

	public static String[] columns = new String[]{"Nb", "TimeStamp", "From", "To", "Sum", "Size", "Message", "Payload"}; 
	public static int[] bounds = new int[]{30, 100, 130, 130,45, 40, 300, 1000};
	public static  long global_size = 0;
	private static HashMap<String, ArrayList<JPacket>> fragments = new HashMap<String, ArrayList<JPacket>>();

	private Logger logger = Logger.getLogger(FullPacket.class);
	private PcapPacket packet;
	private Ethernet eth = new Ethernet();
	private Ip4 ip4 = new Ip4();
	private Udp udp = new Udp();
	private Calendar timestamp = new GregorianCalendar();
	private String ipSrc;
	private String portSrc;
	private String ipDest;
	private String portDest;
	private String description;
	private String dateTime;
	private ArrayList<Message> msgs;
	private String jgroupsVersion;
	private int size = 0;
	private long local_global_size = 0;
	private boolean isComplete = true;
	private long packetNumber;
	private byte[] buf = null;
	private String payload;
	private long id;
	
	static public void resetFragments() {
		fragments = new HashMap<String, ArrayList<JPacket>>();
	}
	
	public FullPacket(PcapPacket packet) {
		this.packet = packet;
		if(packet != null) {
			init();
		}
	}

	public String getGlobalSize() {
		return "" + local_global_size;
	}

	private void addGlobaleSize(int size) {
		global_size += size;
		local_global_size = global_size;
	}

	public boolean isFragmented() {

		if(ip4.isInitialized() && (ip4.flags() & ip4.FLAG_MORE_FRAGMENTS) == ip4.FLAG_MORE_FRAGMENTS) { 
			return true;
		} else {
			return false;
		}
	}


	private void init() {

		packet.hasHeader(ip4);
		packet.hasHeader(udp);
		JPacket defragPacket = null;

		if(ip4.isInitialized()) {
			id = ip4.id();
			String key = "" + ip4.id() + ip4.sourceToInt() +    // key id to identify IP fragments
			ip4.destinationToInt() + ip4.type();
			Iterator it = fragments.keySet().iterator();

			if(isFragmented()) {
				ArrayList<JPacket> f =  fragments.get(key);
				if(f == null) {
					f = new ArrayList<JPacket>();
					fragments.put(key, f);
				}
				f.add(packet);
				isComplete = false;
			} else {
				if(fragments.get(key) != null) {
					fragments.get(key).add(packet);
					buf = Util.hasAllFragments(fragments.get(key));
					if(buf != null) {
						defragPacket = fragments.get(key).get(0); // save first datagramm to extract udp header infos
						fragments.remove(key);
					} else {
						isComplete = false;
					}
				}

				if(udp != null && udp.getPayloadLength() > 0) {
					buf = udp.getPayload();
				}
			}
			if(isComplete && isJGroupsPacket()) {
				msgs = decodeJgroups();
				size = buf.length;

				if(msgs != null) {
					timestamp.setTimeInMillis(packet.getCaptureHeader().timestampInMillis());

					long micro = packet.getCaptureHeader().timestampInMicros() - (packet.getCaptureHeader().timestampInMillis() *1000);
					dateTime =  String.format("%02d:%02d:%02d.%03d.%03d" , timestamp.get(GregorianCalendar.HOUR_OF_DAY),
							timestamp.get(GregorianCalendar.MINUTE), timestamp.get(GregorianCalendar.SECOND),
							timestamp.get(GregorianCalendar.MILLISECOND), micro);

					if(ip4 != null && ip4.source() != null) {
						ipSrc = Util.getIpAddress(ip4.source());
					} else {
						ipSrc = "No source";
					}

					if(ip4 != null && ip4.destination() != null) {
						ipDest = Util.getIpAddress(ip4.destination());
					} else {
						ipDest = "no destination";
					}
					if(defragPacket != null) {
						Udp udpFirstPacket = new Udp();
						if(defragPacket.hasHeader(udpFirstPacket)) {
							portSrc = "" + udpFirstPacket.source();
							portDest = "" + udpFirstPacket.destination();
						}
					} else {
						portSrc = "" + udp.source(); 
						portDest = "" + udp.destination();
					}
					for(Message m : msgs) {
						addGlobaleSize(size);
					}
				}
				payload = decodePayload();
			}

		}
		packet = null;
	}

	public long getId() {
		return id;
	}
	
	private ArrayList<Message> decodeJgroups() {
		if(isJGroupsPacket()) {

			DataInputStream in = new DataInputStream(new ByteArrayInputStream(buf));
			ArrayList<Message> msgs = null;
			try {
				msgs = new ParseMessages(in).parse();
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			return msgs;
		} else {
			return null;
		}

	}


	public boolean isJGroupsPacket() {
		if(buf != null && buf.length > 0) {
			short version = (short) ((buf[0] << 8) + buf[1]);
			return version == 0x1280 || version == 0x12C0 || version == 0x1301 ;  // support JGroups version 2.10 2.11, 2.12
		} else {
			return false;
		}
	}

	public String getJGroupsVersion() {
		return jgroupsVersion;
	}

	public String getDateTime() {
		return dateTime;
	}

	public String getSrcIp() {
		return ipSrc;
	}
	public String getDestIp() {
		return ipDest;
	}
	public String getSrcPort() {
		return portSrc;
	}
	public String getDestPort() {
		return portDest;
	}

	public String getFrom() {
		return ipSrc + ":" + portSrc;
	}
	public String getTo() {
		return ipDest + ":" + portDest;
	}

	public String getDescription() {
		StringBuffer desc = new StringBuffer();
		if(msgs != null) {
			for(Message m : msgs) {
				Map<Short, Header> map = m.getHeaders();
				Iterator it = map.keySet().iterator();
				boolean crlf = false;
				while(it.hasNext()) {
					Short key = (Short) it.next();
					Header h = map.get(key);
					if(! crlf) {
						crlf = ! crlf;
					} else {
						desc.append("\n");
					}

					desc.append(h);
					if(h instanceof PingHeader) {
						PingHeader pingHeader = (PingHeader)h;
						PingData data = pingHeader.arg;
						if(data.getView() != null) {
							desc.append("\n -> PING_DATA : (" + data.getView().size()+ " members in view) " );
						} else {
							desc.append("\n -> PING_DATA : view is null");
						}

					}
				}

			}
		}
		return desc.toString();
	}

	public PcapPacket getPcapPacket() {
		return packet;
	}
	public ArrayList<Message> getMessage() {
		return msgs;
	}

	public String getPayload() {
		return payload;
	}
	
	public String decodePayload() {
		StringBuffer result = new StringBuffer();
		ArrayList<Payload> payloads = new ArrayList<Payload>();
		
		boolean isJGroupsFragmented = false;
		int nb = 0;

		for(Message m : msgs) {
			Map headers = m.getHeaders();
			Iterator it = headers.keySet().iterator();
			boolean hasFragHeader = false;
			boolean hasCompressHeader = false;
			
			while(it.hasNext()) {
				Object h = headers.get(it.next());
				if(h instanceof FragHeader) {
					hasFragHeader = true;
				}
				if(h instanceof COMPRESS.CompressHeader) {
					hasCompressHeader = true;
				}
			}

			if(hasFragHeader) {
				payloads.add(new Payload(null, false, "Cannot decode JGroups FRAG2 data"));
			} else {
				if(hasCompressHeader) {
					byte[] to_uncompress = Arrays.copyOfRange(m.getRawBuffer(), m.getOffset(), m.getLength());
					Payload p = new Payload(to_uncompress, true, null);
					payloads.add(p);
				} else {
					Payload p = new Payload(m.getRawBuffer(), false, null);
					payloads.add(p);
				}
			}
			
			for(Payload p : payloads) {
				result.append(p + "\n");
			}
			if(result.length() != 0) {
				return result.toString();
			} else {
				return "no payload";
			}
		}
		return "no messages";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FullPacket) {
			FullPacket fp = (FullPacket) obj;
			return getFrom().equals(fp.getFrom()) && getDateTime().equals(fp.getDateTime());
		} else {
			return super.equals(obj);
		}
	}

	public String getSize() {
		return "" + size;
	}

	private byte[] defragmentPacket(int id) {

		ArrayList<JPacket> packets = fragments.get(new Integer(id));
		ArrayList<ByteBuffer> buffers = new ArrayList<ByteBuffer>();

		for(JPacket p : packets) {
			Udp udp = new Udp();
			if(p.hasHeader(udp)) {
				ByteBuffer buffer = ByteBuffer.allocate(udp.getPayload().length);
				buffer.put(udp.getPayload());
				buffers.add(buffer);
			} else {
				Ip4 ip = new Ip4();
				if(p.hasHeader(ip)) {
					ByteBuffer buffer = ByteBuffer.allocate(ip.getPayloadLength());
					buffer.put(ip.getPayload());
					buffers.add(buffer);
				}
			}
		}
		int size = 0;
		for(ByteBuffer b : buffers) {
			size += b.array().length;
		}
		byte[] result = new byte[size];
		int idx = 0;
		for(ByteBuffer b : buffers) {
			System.arraycopy(b.array(), 0, result, idx, b.array().length);
			idx += b.array().length;
		}

		return result;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setNumber(long packetNumber) {
		this.packetNumber = packetNumber; 
	}

	public long getPacketNumber() {
		return packetNumber;
	}
}

