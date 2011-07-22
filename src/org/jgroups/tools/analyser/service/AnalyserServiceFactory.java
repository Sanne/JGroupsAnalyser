package org.jgroups.tools.analyser.service;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

public class AnalyserServiceFactory extends AbstractServiceFactory {

	public AnalyserServiceFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object create(Class serviceInterface, IServiceLocator parentLocator, IServiceLocator locator) {
		return (IPcapService)new PcapService();
	}

}
