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

import java.util.List;

import org.osgi.framework.Bundle;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;

@SuppressWarnings("rawtypes")
public class TestGuicer implements IGuicer {
	//private static final Map<String, TestGuicer> GUICERS=new ConcurrentHashMap<String,TestGuicer>();

	private static TestGuicer guicer;
	private Injector injector;
	private String pluginId;



	//singleton.
	private TestGuicer(){

	}


	public static TestGuicer createGuicer(Bundle bundle,List<Module> modules){

		try{
			if(guicer==null){
				Injector injector = Guice.createInjector(modules);
				guicer =new TestGuicer();
				guicer.setInjector(injector);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return guicer;
	}



	public static void removeGuicer(Bundle bundle){
		
	}


	public static TestGuicer getInstance(Bundle bundle){
		return guicer;
	}


	protected void setInjector(Injector injector){
		this.injector=injector;
	}


	@Override
	public Injector getInjector(){
		return this.injector;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object createObject(Class cls){
		Object o = this.getInjector().getInstance(cls);
		this.getInjector().injectMembers(o);
		return o;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object createObject(Class cls, String name){
		Key key = Key.get(cls, Names.named(name));
		Object o= this.getInjector().getInstance(key);
		this.getInjector().injectMembers(o);
		return o;
	}


	@Override
	@SuppressWarnings("unchecked")
	public Object createObject(Class cls, String name, boolean inject){
		Key key = Key.get(cls, Names.named(name));
		Object o= this.getInjector().getInstance(key);

		if(inject){
			this.getInjector().injectMembers(o);
		}

		return o;
	}

	@Override
	public  <T> T inject(T t){
		this.injector.injectMembers(t);
		return t;
	}

	@Override
	public String getPluginId() {
		return pluginId;
	}


}

