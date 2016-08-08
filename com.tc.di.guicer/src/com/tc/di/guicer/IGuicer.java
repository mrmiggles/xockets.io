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

import com.google.inject.Injector;

@SuppressWarnings("rawtypes")
public interface IGuicer {

	public abstract Injector getInjector();

	public <T> T createObject(Class cls);
	
	public <T> T build(Class cls);
	
	public abstract <T> T createObject(Class cls, String name);

	public abstract <T> T createObject( Class cls, String name, boolean inject);

	public  <T> T inject(T t);

	public abstract String getPluginId();

}