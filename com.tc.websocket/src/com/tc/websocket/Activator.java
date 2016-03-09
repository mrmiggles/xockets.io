/*
 * © Copyright Tek Counsel LLC 2016
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */


package com.tc.websocket;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


// TODO: Auto-generated Javadoc
/**
 * The Class Activator.
 */
public class Activator extends Plugin {

	/** The plugin id. */
	public static Bundle bundle;

	/** The version. */
	public static String VERSION;
	
	
	
	@SuppressWarnings("unused")
	private ServiceRegistration commandLineReg;



	@Override
	public void start(final BundleContext context) {
		
		
		bundle=context.getBundle(); 
		commandLineReg=context.registerService(CommandProvider.class.getName(), new CommandLine(), null);
		StartAndStop.getInstance().start();
	}

	@Override
	public void stop(final BundleContext context) {
		StartAndStop.getInstance().stop();
	}


}
