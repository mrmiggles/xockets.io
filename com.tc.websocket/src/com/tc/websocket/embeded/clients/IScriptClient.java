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


package com.tc.websocket.embeded.clients;

import java.util.Collection;

import com.tc.scriptrunner.runners.IScriptRunner;
import com.tc.websocket.valueobjects.IUser;

public interface IScriptClient {

	//event constants.
	public static final String ON_OPEN = "onOpen";
	public static final String ON_MESSAGE = "onMessage";
	public static final String ON_CLOSE = "onClose";
	public static final String ON_ERROR = "onError";

	public abstract void setUser(IUser user);

	public abstract IUser getUser();

	public abstract void setScriptRunner(IScriptRunner runner);

	public abstract void addToScope(String varName, Object o);

	public abstract void removeFromScope(String varName);

	public abstract void addScript(Script script);

	public abstract void removeScriptByEvent(String event);
	
	public abstract void removeScriptByPath(String path);
	
	public abstract boolean hasError();

	public abstract void reloadScripts();
	
	public abstract Collection<Script> getScripts();
	
	public abstract void runAsUser(String sessionUsername, String sessionPassword);

}