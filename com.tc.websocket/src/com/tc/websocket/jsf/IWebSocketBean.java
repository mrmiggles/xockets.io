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


// TODO: Auto-generated Javadoc
/**
 * The Interface IWebSocketBean.
 */
public interface IWebSocketBean {
	
	/**
	 * Inits the.
	 *
	 * @param server the server
	 */
	public void init(IDominoWebSocketServer server);

	/**
	 * Register current user.
	 *
	 * @throws NotesException the notes exception
	 */
	public abstract void registerCurrentUser() throws NotesException;

	/**
	 * Removes the current user.
	 */
	public abstract void removeCurrentUser();

	/**
	 * Gets the users.
	 *
	 * @return the users
	 */
	public abstract List<String> getUsers();

	/**
	 * Gets the online users.
	 *
	 * @return the online users
	 * @throws NotesException the notes exception
	 */
	public abstract List<SelectItem> getOnlineUsers() throws NotesException;

	/**
	 * Gets the web socket url.
	 *
	 * @return the web socket url
	 */
	public abstract String getWebSocketUrl();

	/**
	 * Send message.
	 *
	 * @param from the from
	 * @param to the to
	 * @param text the text
	 */
	public abstract void sendMessage(String from, String to, String text);

	/**
	 * Send message.
	 *
	 * @param msg the msg
	 */
	public abstract void sendMessage(SocketMessage msg);
	
	/**
	 * Sets the request.
	 *
	 * @param req the new request
	 */
	public void setRequest(HttpServletRequest req);

	/**
	 * Contains script.
	 *
	 * @param source the source
	 * @return true, if successful
	 */
	public boolean containsScript(String source);
	
	/**
	 * Gets the custom web socket url.
	 *
	 * @param req the req
	 * @param sessionId the session id
	 * @param sourceUri the source uri
	 * @return the custom web socket url
	 */
	public String getCustomWebSocketUrl(HttpServletRequest req, String sessionId, String sourceUri);

	
	/**
	 * Adds the uri listeners.
	 * Convenience method to add multiple URI listeners to the same URI
	 * Example:
	 <pre>
	 	//listeners only fire during onMessage
		websocketBean.addUriListeners("/chat.nsf*",
		[
		"/chat.nsf/listener.agent",
		"/chat.nsf/listener.py",
		"/chat.nsf/listener.rb",
		"/chat.nsf/listener.gvy",
		"/chat.nsf/listener.ssjs",
		"/chat.nsf/listener.bsh"
		]);
	</pre>
	 
	 * @param uri the uri
	 * @param sources the sources
	 */
	//URI listeners
	public void addUriListeners(final String uri, final String[] sources);
	
	/**
	 * Adds the uri listener.
	 * @param uri the uri
	 * @param source the source
	 * @param runAsUser the run as user
	 * @param runAsPassword the run as password
	 */
	public void addUriListener(String uri, String source, String runAsUser, String runAsPassword);
	
	/**
	 * Adds the uri listener.
	 *
	 * @param uri the uri
	 * @param source the source
	 */
	public void addUriListener(String uri, String source);
	
	/**
	 * Adds the uri listener.
	 * When using this method the listener only listens for direct
	 * invocations of itself
	 * Example:
	<pre>
		websocketBean.addUriListener("/chat.nsf/observer.ssjs");
	 </pre>
	 * will only fire when the SocketMessage is sent directly to the script's path.
	 * @param source the source
	 */
	public void addUriListener(String source);
	
	/**
	 * Contains uri listener.
	 *
	 * @param uri the uri
	 * @return true, if successful
	 */
	public boolean containsUriListener(String uri);
	
	/**
	 * Removes the uri listener.
	 *
	 * @param source the source
	 */
	public void removeUriListener(String source);

	
	
	/**
	 * Adds the observers.
	 * Convenience method to add multiple observers in one call
	 * Example:
		<pre>
			//assumes each script below will support all events (onMessage, onOpen, onClose, onError)
			websocketBean.addObservers([
			"/chat.nsf/observer.agent"
			,"/chat.nsf/observer.ssjs"
			,"/chat.nsf/observer.rb"
			,"/chat.nsf/observer.gvy"
			,"/chat.nsf/observer.py"
			,"/chat.nsf/observer.bsh"]);
		
			}
			</pre>
	 * @param sources the sources
	 */
	//observers
	public void addObservers(String[] sources);
	
	
	
	/**
	 * Adds the event observer.
	 * Adds a single source as an observer for all events.
	 * @param source the source
	 */
	public void addEventObserver(String source);
	
	
	/**
	 * Adds the event observer to listen for a specific function / event (e.g. onMessage, onClose)
	 * Example using websocketBean managed bean:
	 <pre>
	 		websocketBean.addEventObserver("onMessage","/chat.nsf/observer.ssjs");
	 </pre>
	 
	 * @param function the function
	 * @param source the source
	 */
	public void addEventObserver(final String function, final String source);
	
	/**
	 * Contains observer.
	 *
	 * @param function the function
	 * @param source the source
	 * @return true, if successful
	 */
	public boolean containsObserver(final String function, final String source);
	
	/**
	 * Removes the observer.
	 *
	 * @param function the function
	 * @param source the source
	 */
	public void removeObserver(final String function, final String source);
	
	
	
	/**
	 * Adds the intervaled scripts.
	 * Convenience method to register multiple intervaled scripts
	 * Example using managed bean in XPages
	 * Each script referenced below runs every 30 seconds
	 <pre>
		 websocketBean.addIntervaledScripts(30, [
		"/chat.nsf/interval.agent",
		"/chat.nsf/interval.py",
		"/chat.nsf/interval.rb",
		"/chat.nsf/interval.gvy",
		"/chat.nsf/interval.ssjs",
		"/chat.nsf/interval.bsh"
		]); 
	</pre>
	 * @param interval the interval
	 * @param sources the sources
	 */
	//intervaled
	public void addIntervaledScripts(int interval, final String[] sources);
	
	/**
	 * Adds the intervaled script and registers user and password to run under.
	 * 
	 * @param interval the interval
	 * @param source the source
	 * @param runAsUser the run as user
	 * @param runAsPassword the run as password
	 */
	public void addIntervaled(int interval, String source, String runAsUser, String runAsPassword);
	
	/**
	 * Adds the intervaled.
	 * Example:
	 	<pre>
	 	websocketBean.addIntervaled(5, "/chat.nsf/RunEveryFiveSeconds.ssjs);
	 </pre>
	 * @param interval the interval
	 * @param source the source
	 */
	public void addIntervaled(int interval, String source);
	
	/**
	 * Removes the intervaled.
	 *
	 * @param source the source
	 */
	public void removeIntervaled(String source);
	
}