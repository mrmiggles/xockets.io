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


package com.tc.websocket.server;


import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Collection;

import com.tc.websocket.filter.IWebsocketFilter;
import com.tc.websocket.scripts.Script;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;

public interface IDominoWebSocketServer extends Runnable {

	public abstract void setFilter(IWebsocketFilter filter);
	
	public abstract IWebsocketFilter getFilter();

	public abstract void addUser(IUser user);

	public abstract void removeUser(String key);
	
	public abstract void removeUser(IUser user);

	public abstract Collection<IUser> getUsers();
	
	public abstract Collection<IUser> getUsersOnThisServer();
	
	public Collection<IUser> getUsersByUri(String uri);

	public abstract void onOpen(ContextWrapper channel, FullHttpRequest handshake);

	public abstract void onClose(ContextWrapper conn);
	
	public abstract void closeWithDelay(ContextWrapper conn, int delay);

	public abstract boolean onMessage(String to, String json);
	
	public abstract void onMessage(ContextWrapper conn, String message);

	public abstract void onError(ContextWrapper conn, Exception ex);

	public abstract IUser resolveUser(ContextWrapper conn);
	
	public abstract IUser resolveUser(String key);//key could be sessionId, or userId.
	
	public boolean containsUser(String key);
	
	public abstract void pingUsers();

	public abstract void broadcast(SocketMessage msg);
	
	public abstract void queueMessage(SocketMessage msg);
	
	public abstract void start();
	
	public abstract void stop();
	
	public boolean isValidSize(String data);
	
	public boolean isOn();
	
	public void setOn(boolean on);
	
	public void removeAllUsers();
	
	public int getWebSocketCount();
	
	public int getWebSocketAndObserverCount();
	
	public int decrementCount();
	
	public Collection<Script> getAllScripts();
	
	public void reloadScripts();
	
	
	//for all events
	public boolean containsObserver(Script script);
	public void addEventObserver(Script script);
	public void removeEventObserver(Script script);
	public void notifyEventObservers(String event, Object ...args);
	public Collection<Script> getEventObservers();
	
	
	
	//for uri onMessage events.
	public Collection<Script> getUriListeners();
	public void addUriListener(Script script);
	public Script findUriListener(String source);
	public void removeUriListener(Script script);
	
}