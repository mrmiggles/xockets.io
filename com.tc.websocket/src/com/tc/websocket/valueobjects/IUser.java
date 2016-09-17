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


package com.tc.websocket.valueobjects;


import java.util.Collection;
import java.util.Date;

import com.tc.websocket.server.ContextWrapper;
import com.tc.websocket.server.RoutingPath;


// TODO: Auto-generated Javadoc
/**
 * The Interface IUser.
 */
public interface IUser extends IUri {
	
	/**
	 * Sets the doc id.
	 *
	 * @param docId the new doc id
	 */
	public abstract void setDocId(String docId);
	
	/**
	 * Gets the doc id.
	 *
	 * @return the doc id
	 */
	public abstract String getDocId();
	
	/**
	 * Gets the session id.
	 *
	 * @return the session id
	 */
	public abstract String getSessionId();

	/**
	 * Sets the session id.
	 *
	 * @param sessionId the new session id
	 */
	public abstract void setSessionId(String sessionId);

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public abstract String getUserId();

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public abstract void setUserId(String userId);

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public abstract Date getDate();

	/**
	 * Sets the date.
	 *
	 * @param date the new date
	 */
	public abstract void setDate(Date date);

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public abstract String toString();

	/**
	 * Equals.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
	public abstract boolean equals(Object o);

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public abstract int hashCode();
	
	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public abstract void setStatus(String status);
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus();
	
	/**
	 * Checks if is going offline.
	 *
	 * @return true, if is going offline
	 */
	public boolean isGoingOffline();
	
	/**
	 * Sets the going offline.
	 *
	 * @param goingOffline the new going offline
	 */
	public void setGoingOffline(boolean goingOffline);
	
	/**
	 * Checks if is open.
	 *
	 * @return true, if is open
	 */
	public boolean isOpen();
	
	/**
	 * Checks if is on server.
	 *
	 * @return true, if is on server
	 */
	public boolean isOnServer();
	
	/**
	 * Checks if is anonymous.
	 *
	 * @return true, if is anonymous
	 */
	public boolean isAnonymous();
	
	/**
	 * Can receive.
	 *
	 * @return true, if successful
	 */
	public boolean canReceive();
	
	/**
	 * Gets the conn.
	 *
	 * @return the conn
	 */
	public ContextWrapper getConn();

	/**
	 * Sets the conn.
	 *
	 * @param conn the new conn
	 */
	public void setConn(ContextWrapper conn);
	
	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost();
	
	/**
	 * Sets the host.
	 *
	 * @param host the new host
	 */
	public void setHost(String host);
	
	/**
	 * Gets the last ping.
	 *
	 * @return the last ping
	 */
	public abstract Date getLastPing();
	
	/**
	 * Sets the last ping.
	 *
	 * @param date the new last ping
	 */
	public void setLastPing(Date date);

	/**
	 * Gets the user path.
	 *
	 * @return the user path
	 */
	public String getUserPath();

	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public boolean isValid();
	
	/**
	 * Copy.
	 *
	 * @return the i user
	 */
	public IUser copy();
	
	/**
	 * Send.
	 *
	 * @param message the message
	 */
	public void send(String message);
	
	/**
	 * Process queue.
	 */
	public void processQueue();
	
	/**
	 * Find connection.
	 *
	 * @param path the path
	 * @return the collection
	 */
	public Collection<ContextWrapper> findConnection(RoutingPath path);
	
	public ContextWrapper findConnection(String uri);
	
	/**
	 * Gets the connections.
	 *
	 * @return the connections
	 */
	public Collection<ContextWrapper> getConnections();
	
	/**
	 * Close.
	 */
	public void close();
	
	/**
	 * Clear.
	 */
	public void clear();
	
	/**
	 * Count.
	 *
	 * @return the int
	 */
	public int count();
	
	
	

}