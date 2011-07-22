package org.jgroups.tools.analyser.util;

import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jgroups.tools.analyser.Activator;
import org.jnetpcap.nio.JMemory.Type;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.PeeringException;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Udp;

public class Util {

	public static Logger logger = Logger.getLogger(Util.class);

	private static final class ComparatorImplementation implements Comparator {
		public int compare(final Object o1, final Object o2) {
			int result = 0;
			final JPacket j1 = (JPacket) o1;
			final JPacket j2 = (JPacket) o2;
			final Ip4 j1ip = new Ip4();
			final Ip4 j2ip = new Ip4();
			if(j1.hasHeader(j1ip) && j2.hasHeader(j2ip)) {
				if(j1ip.offset() > j2ip.offset()) {
					result = 1;
				}
				if(j1ip.offset() < j2ip.offset()) {
					result = -1;
				} else {
					result = 0;
				}
			}
			return result;
		}
	}

	public static String getIpAddress(byte[] rawBytes) {
		int i = 4;
		String ipAddress = "";
		for (byte raw : rawBytes)
		{
			ipAddress += (raw & 0xFF);
			if (--i > 0) {
				ipAddress += ".";
			}
		}
		return ipAddress;
	}
	
	public static String getPayloadAsString(byte[] payload) {
		StringBuilder sb = new StringBuilder();
		for(byte b : payload) {
			if(isStringChar((char)b)) {
				sb.append((char)b);
			} else {
				sb.append(".");
			}
		}
		return sb.toString();
	}
	protected static boolean isStringChar(char ch) {
	    if (ch >= 'a' && ch <= 'z')
	      return true;
	    if (ch >= 'A' && ch <= 'Z')
	      return true;
	    if (ch >= '0' && ch <= '9')
	      return true;
	    switch (ch) {
	    case '/':
	    case '-':
	    case ':':
	    case '.':
	    case ',':
	    case '_':
	    case '$':
	    case '%':
	    case '\'':
	    case '(':
	    case ')':
	    case '[':
	    case ']':
	    case '<':
	    case '>':
	      return true;
	    }
	    return false;
	  }

	public static boolean isPrintableChar( char c ) {
		Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
		return (!Character.isISOControl(c)) &&	c != KeyEvent.CHAR_UNDEFINED && block != null &&
		block != Character.UnicodeBlock.SPECIALS;
	}
	
	public static boolean isPrintableChar( byte c ) {
		return isPrintableChar((char)(c & 0xFF));
	}

	public static boolean isMulticastAddress(String address) {
		boolean result = false;
		int prefix = Integer.parseInt(address.split("\\.")[0]);
		if(prefix >= 224 && prefix <= 240) {
			result = true;
		}
		return result;
		
	}


	public static byte[] hasAllFragments(ArrayList<JPacket> list) {
		byte[] result = null;
		if(list.size() <= 1 ) {
			return null;
		}
		Collections.sort(list, new ComparatorImplementation());
		boolean foundLastFragment = false;
		boolean missingFragment =  false;
		long lastOffset = 0;
		ArrayList<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
				
		for(int i = 0; i < list.size(); i ++) {
			JPacket jp = list.get(i);
			Ip4 ip4 = new Ip4();
			if(jp.hasHeader(ip4)) {
				if( (ip4.flags() & ip4.FLAG_MORE_FRAGMENTS) == 0) {
					foundLastFragment = true;
				}
//				System.out.println("offset : " + ip4.offset()*8 + ", lastoffset : " + lastOffset);
				if((ip4.offset() *8) != lastOffset) {
					missingFragment = true;
				} else {
					lastOffset = (ip4.offset()*8) + ip4.length() - 20; //Ip header = 20
				}
			}
			Udp udp = new Udp();
			if(jp.hasHeader(udp)) {
				ByteBuffer buffer = ByteBuffer.allocate(udp.getPayload().length);
				buffer.put(udp.getPayload());
				buffers.add(buffer);
				
			} else {
				ByteBuffer buffer = ByteBuffer.allocate(ip4.getPayloadLength());
				buffer.put(ip4.getPayload());
				buffers.add(buffer);
			}
			
		}
		if( ! foundLastFragment || missingFragment) {
			return null;
		} else {
			int size = 0;
			for(ByteBuffer b : buffers) {
				size += b.array().length;
			}
			result = new byte[size];
			int idx = 0;
			for(ByteBuffer b : buffers) {
				System.arraycopy(b.array(), 0, result, idx, b.array().length);
				idx += b.array().length;
			}
			return result;
		}


	}
}
