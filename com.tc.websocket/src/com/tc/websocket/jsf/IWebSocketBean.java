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

package com.tc.websocket.jsf;

import java.util.List;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import lotus.domino.NotesException;

import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.SocketMessage;

public interface IWebSocketBean {
	
	public void init(IDominoWebSocketServer server);

	public abstract void registerCurrentUser() throws NotesException;

	public abstract void removeCurrentUser();

	public abstract List<String> getUsers();

	public abstract List<SelectItem> getOnlineUsers() throws NotesException;

	public abstract String getWebSocketUrl();

	public abstract void sendMessage(String from, String to, String text);

	public abstract void sendMessage(SocketMessage msg);
	
	public void setRequest(HttpServletRequest req);

	public boolean containsScript(String source);
	
	public String getCustomWebSocketUrl(HttpServletRequest req, String sessionId, String sourceUri);

	
	//URI listeners
	public void addListeners(final String uri, final String[] sources);
	public void addUriListener(String uri, String source, String runAsUser, String runAsPassword);
	public void addUriListener(String uri, String source);
	public boolean containsUriListener(String uri);
	public void removeUriListener(String source);
	
	
	//observers
	public void addObservers(String[] resources);
	public void addEventObserver(final String function, final String source);
	public boolean containsObserver(final String function, final String source);
	public void removeObserver(final String function, final String source);
	

}