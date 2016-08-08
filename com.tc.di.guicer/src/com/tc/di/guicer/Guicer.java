/*
 * © Copyright Tek Counsel LLC 2013
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


package com.tc.di.guicer;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;

@SuppressWarnings({"rawtypes","unchecked"})
public class Guicer implements IGuicer {
	
	private static final Logger logger = Logger.getLogger(Guicer.class.getName());
	
	private static final Map<String, Guicer> GUICERS=new ConcurrentHashMap<String,Guicer>();

	private Injector injector;
	private String pluginId;



	//singleton.
	protected Guicer(){

	}




	public static Guicer createGuicer(Bundle bundle, List<Module> modules){

		String pluginId = buildPluginId(bundle);
		Guicer guicer = GUICERS.get(pluginId);

		try{
			if(guicer==null){
				synchronized(GUICERS){
					guicer = GUICERS.get(pluginId);
					if(guicer==null){ //if we're still null in the synch block create the guicer.
						logger.log(Level.FINE,"creating guicer for " + pluginId);
						Injector injector = Guice.createInjector(modules);
						guicer =new Guicer();
						guicer.setInjector(injector);
						GUICERS.put(pluginId, guicer);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return guicer;
	}



	public static void removeGuicer(Bundle bundle){
		GUICERS.remove(buildPluginId(bundle));
	}


	private static String buildPluginId(Bundle bundle){
		String pluginId = bundle.getSymbolicName() + "." + getVersion(bundle);
		return pluginId;
	}


	private static String getVersion(Bundle bundle){
		Dictionary dict = bundle.getHeaders();
		return String.valueOf(dict.get(Constants.BUNDLE_VERSION));
	}

	public static IGuicer getInstance(Bundle bundle){
		IGuicer guicer = GUICERS.get(buildPluginId(bundle));
		return guicer;
	}


	protected void setInjector(Injector injector){
		this.injector=injector;
	}


	/* (non-Javadoc)
	 * @see com.tc.di.guicer.IGuicer#getInjector()
	 */
	@Override
	public Injector getInjector(){
		return this.injector;
	}

	/* (non-Javadoc)
	 * @see com.tc.di.guicer.IGuicer#createObject(java.lang.Class)
	 */

	@Override
	public <T> T createObject(Class cls){
		T t = (T) this.getInjector().getInstance(cls);
		this.getInjector().injectMembers(t);
		return t;
	}
	
	/* (non-Javadoc)
	 * @see com.tc.di.guicer.IGuicer#createObject(java.lang.Class, java.lang.String)
	 */

	@Override
	public <T> T createObject(Class cls, String name){
		Key key = Key.get(cls, Names.named(name));
		T t = (T) this.getInjector().getInstance(key);
		this.getInjector().injectMembers(t);
		return t;
	}


	/* (non-Javadoc)
	 * @see com.tc.di.guicer.IGuicer#createObject(java.lang.Class, java.lang.String, boolean)
	 */

	@Override
	public <T> T createObject(Class cls, String name, boolean inject){
		
		Key key = Key.get(cls, Names.named(name));
		T t= (T) this.getInjector().getInstance(key);

		if(inject){
			this.getInjector().injectMembers(t);
		}

		return t;
	}

	/* (non-Javadoc)
	 * @see com.tc.di.guicer.IGuicer#inject(java.lang.Object)
	 */
	@Override
	public  <T> T inject(T t){
		this.injector.injectMembers(t);
		return t;
	}

	/* (non-Javadoc)
	 * @see com.tc.di.guicer.IGuicer#getPluginId()
	 */
	@Override
	public String getPluginId() {
		return pluginId;
	}




	@Override
	public <T> T build(Class cls) {
		return this.createObject(cls);
	}







}

