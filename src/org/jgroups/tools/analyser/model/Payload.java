package org.jgroups.tools.analyser.model;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.xbean.classloader.JarFileClassLoader;
import org.jgroups.tools.analyser.Activator;
import org.jgroups.tools.analyser.util.Util;
import org.jgroups.util.ExposedByteArrayInputStream;

public class Payload {

	public static String treatment = Activator.getDefault().getPreferenceStore().getString("PAYLOAD");
	public static String jarPath = Activator.getDefault().getPreferenceStore().getString("JAR_PATH");
	public static JarFileClassLoader classLoader = null;
	
	public static void setClassLoader() {
		File dependencyDirectory = new File(jarPath);
		if(new File(jarPath).exists()) {
			File[] files = dependencyDirectory.listFiles();
			ArrayList<URL> urls = new ArrayList<URL>();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().endsWith(".jar")) {
					try {
						System.out.println("add " + files[i].toURI(). toURL());
						urls.add(files[i].toURI(). toURL());
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
			classLoader = new JarFileClassLoader("Payload classes" , urls.toArray(new URL[urls.size()]), Activator.class.getClassLoader());
		}
	}
	
	private byte[] payload;
	private StringBuffer result = new StringBuffer();
	private String type;
	private boolean isCompressed = false;
	private String errorMsg = null;
	
	public Payload(byte[] payload, boolean isCompressed, String errorMsg) {
		this.payload = payload;
		this.isCompressed = isCompressed;
		this.errorMsg = errorMsg;
		
	}
	
	public String getReadablePayload() {
		
		String result = "No payload";
		
		if(treatment.equals("nodisplay")) {
			return "Hidden. Change preferences to display payload";
		}
		if(payload != null) {
			if(treatment.equals("binaryToString")) {
				result = Util.getPayloadAsString(payload);
			}
			if(treatment.equals("uncompress")) {
				result = Util.getPayloadAsString(uncompress());
			}
			if(treatment.equals("unserialize")) {
				result = unSerialize(uncompress());
			} 
		} 
		if(errorMsg != null) {
			return errorMsg;
		} else {
			return result;
		}
		
	}
	
	private byte[] uncompress() {
		byte[] result = new byte[payload.length * 10];
		byte[] uncompressedPayload = null;
		
		if(! isCompressed) {
			return payload;
		}
		
		Inflater decompresser = new Inflater();
		decompresser.setInput(payload);
		int r = 0;
		try {
			r = decompresser.inflate(result);
			uncompressedPayload = Arrays.copyOfRange(result, 0,r);
			
		} catch (DataFormatException e) {
			errorMsg = e.getMessage();
		}
		
		return result;
	}
	
	private String unSerialize(byte[] objectBinary) {
		String result = null;
		if(classLoader == null) {
			setClassLoader();
		}
		try {
			ExposedByteArrayInputStream ex = new ExposedByteArrayInputStream(objectBinary, 1, objectBinary.length -1);

			ObjectInputStream in = new ObjectInputStream(ex) {
				protected Class<?> resolveClass(ObjectStreamClass desc)	throws IOException, ClassNotFoundException {
					return classLoader.loadClass(desc.getName());
				}
			};
			Object o = in.readObject();
			result = o.toString();

		} catch (Exception e) {
			errorMsg = e.getMessage();
		} catch(NoClassDefFoundError n) {
			errorMsg = "class not found : " + n.getMessage() + ", you should update the preferences JAR path";
		}
		return result; 
		
	}
	
	public String toString() {
		if( errorMsg != null && ! errorMsg.isEmpty()) {
			return errorMsg;
		} else {
			return getReadablePayload();
		}
	
	
	}
}
