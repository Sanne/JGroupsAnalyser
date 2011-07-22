package org.jgroups.tools.analyser.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class SourceProvider extends AbstractSourceProvider {

	public final static String PLAY_STATE = "JGroupsAnalyser.play";
	public final static String STOP_STATE = "JGroupsAnalyser.stop";
	public final static String LOAD_STATE = "JGroupsAnalyser.load";
	public final static String ENABLED = "ENABLED";
	public final static String DISABLED = "DISABLED";
	private boolean playenabled = true;
	private boolean loadenabled = true;
	private boolean stopenabled = false;
	
	public SourceProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map getCurrentState() {
		Map map = new HashMap(1);
		String playvalue = playenabled ? ENABLED : DISABLED;
		String loadvalue = playenabled ? ENABLED : DISABLED;
		String stopvalue = playenabled ? DISABLED : ENABLED;
		map.put(PLAY_STATE, playvalue);
		map.put(STOP_STATE, stopvalue);
		map.put(LOAD_STATE, loadvalue);
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { PLAY_STATE, STOP_STATE };
	}

	public void tooglePlayEnabled() {
		playenabled = true;
		stopenabled = false;
		toggleEnabled();
	}

	public void toogleLoadEnabled() {
		loadenabled = true;
		stopenabled = false;
		toggleEnabled();
	}

	
	
	public void toogleStopEnabled() {
		playenabled = false;
		loadenabled = false;
		stopenabled = true;
		toggleEnabled();
	}

	private void toggleEnabled() {
		fireSourceChanged(ISources.WORKBENCH, PLAY_STATE, playenabled ? ENABLED : DISABLED);
		fireSourceChanged(ISources.WORKBENCH, LOAD_STATE, loadenabled ? ENABLED : DISABLED);
		fireSourceChanged(ISources.WORKBENCH, STOP_STATE, stopenabled ? ENABLED : DISABLED);
		
	}
}
