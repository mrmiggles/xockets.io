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


// TODO: Auto-generated Javadoc
/**
 * The Interface IDominoWebSocketServer.
 */
public interface IDominoWebSocketServer extends Runnable {

	/**
	 * Sets the filter.
	 *
	 * @param filter the new filter
	 */
	public abstract void setFilter(IWebsocketFilter filter);
	
	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	public abstract IWebsocketFilter getFilter();

	/**
	 * Adds the user.
	 *
	 * @param user the user
	 */
	public abstract void addUser(IUser user);

	/**
	 * Removes the user.
	 *
	 * @param key the key
	 */
	public abstract void removeUser(String key);
	
	/**
	 * Removes the user.
	 *
	 * @param user the user
	 */
	public abstract void removeUser(IUser user);

	/**
	 * Gets the users.
	 *
	 * @return the users
	 */
	public abstract Collection<IUser> getUsers();
	
	/**
	 * Gets the users on this server.
	 *
	 * @return the users on this server
	 */
	public abstract Collection<IUser> getUsersOnThisServer();
	
	/**
	 * Gets the users by uri.
	 *
	 * @param uri the uri
	 * @return the users by uri
	 */
	public Collection<IUser> getUsersByUri(String uri);

	/**
	 * On open.
	 *
	 * @param channel the channel
	 * @param handshake the handshake
	 */
	public abstract void onOpen(ContextWrapper channel, FullHttpRequest handshake);

	/**
	 * On close.
	 *
	 * @param conn the conn
	 */
	public abstract void onClose(ContextWrapper conn);
	
	/**
	 * Close with delay.
	 *
	 * @param conn the conn
	 * @param delay the delay
	 */
	public abstract void closeWithDelay(ContextWrapper conn, int delay);

	/**
	 * On message.
	 *
	 * @param to the to
	 * @param json the json
	 * @return true, if successful
	 */
	public abstract boolean onMessage(String to, String json);
	
	/**
	 * On message.
	 *
	 * @param conn the conn
	 * @param message the message
	 */
	public abstract void onMessage(ContextWrapper conn, String message);

	/**
	 * On error.
	 *
	 * @param conn the conn
	 * @param ex the ex
	 */
	public abstract void onError(ContextWrapper conn, Exception ex);

	/**
	 * Resolve user.
	 *
	 * @param conn the conn
	 * @return the i user
	 */
	public abstract IUser resolveUser(ContextWrapper conn);
	
	/**
	 * Resolve user.
	 *
	 * @param key the key
	 * @return the i user
	 */
	public abstract IUser resolveUser(String key);//key could be sessionId, or userId.
	
	/**
	 * Contains user.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean containsUser(String key);
	
	/**
	 * Ping users.
	 */
	public abstract void pingUsers();

	/**
	 * Broadcast.
	 *
	 * @param msg the msg
	 */
	public abstract void broadcast(SocketMessage msg);
	
	/**
	 * Queue message.
	 *
	 * @param msg the msg
	 */
	public abstract void queueMessage(SocketMessage msg);
	
	/**
	 * Start.
	 */
	public abstract void start();
	
	/**
	 * Stop.
	 */
	public abstract void stop();
	
	/**
	 * Checks if is valid size.
	 *
	 * @param data the data
	 * @return true, if is valid size
	 */
	public boolean isValidSize(String data);
	
	/**
	 * Checks if is on.
	 *
	 * @return true, if is on
	 */
	public boolean isOn();
	
	/**
	 * Sets the on.
	 *
	 * @param on the new on
	 */
	public void setOn(boolean on);
	
	/**
	 * Removes the all users.
	 */
	public void removeAllUsers();
	
	/**
	 * Gets the web socket count.
	 *
	 * @return the web socket count
	 */
	public int getWebSocketCount();
	
	/**
	 * Gets the web socket and observer count.
	 *
	 * @return the web socket and observer count
	 */
	public int getWebSocketAndObserverCount();
	
	/**
	 * Decrement count.
	 *
	 * @return the int
	 */
	public int decrementCount();
	
	/**
	 * Gets the all scripts.
	 *
	 * @return the all scripts
	 */
	public Collection<Script> getAllScripts();
	
	/**
	 * Reload scripts.
	 */
	public void reloadScripts();
	
	
	/**
	 * Contains observer.
	 *
	 * @param script the script
	 * @return true, if successful
	 */
	//for all events
	public boolean containsObserver(Script script);
	
	/**
	 * Adds the event observer.
	 *
	 * @param script the script
	 */
	public void addEventObserver(Script script);
	
	/**
	 * Removes the event observer.
	 *
	 * @param script the script
	 */
	public void removeEventObserver(Script script);
	
	/**
	 * Notify event observers.
	 *
	 * @param event the event
	 * @param args the args
	 */
	public void notifyEventObservers(String event, Object ...args);
	
	/**
	 * Gets the event observers.
	 *
	 * @return the event observers
	 */
	public Collection<Script> getEventObservers();
	
	
	
	/**
	 * Gets the uri listeners.
	 *
	 * @return the uri listeners
	 */
	//for uri onMessage events.
	public Collection<Script> getUriListeners();
	
	/**
	 * Adds the uri listener.
	 *
	 * @param script the script
	 */
	public void addUriListener(Script script);
	
	/**
	 * Find uri listener.
	 *
	 * @param source the source
	 * @return the script
	 */
	public Script findUriListener(String source);
	
	/**
	 * Removes the uri listener.
	 *
	 * @param script the script
	 */
	public void removeUriListener(Script script);
	
	
	/**
	 * Adds the intervaled.
	 *
	 * @param script the script
	 */
	//for scheduled onInterval scripts
	public void addIntervaled(Script script);
	
	/**
	 * Removes the intervaled.
	 *
	 * @param script the script
	 */
	public void removeIntervaled(Script script);
	
	/**
	 * Gets the intervaled.
	 *
	 * @return the intervaled
	 */
	public Collection<Script> getIntervaled(); 
	
}