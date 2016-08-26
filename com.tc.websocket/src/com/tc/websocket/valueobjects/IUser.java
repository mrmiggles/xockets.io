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

public interface IUser extends IUri {
	
	public abstract void setDocId(String docId);
	
	public abstract String getDocId();
	
	public abstract String getSessionId();

	public abstract void setSessionId(String sessionId);

	public abstract String getUserId();

	public abstract void setUserId(String userId);

	public abstract Date getDate();

	public abstract void setDate(Date date);

	@Override
	public abstract String toString();

	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();
	
	public abstract void setStatus(String status);
	
	public String getStatus();
	
	public boolean isGoingOffline();
	
	public void setGoingOffline(boolean goingOffline);
	
	public boolean isOpen();
	
	public boolean isOnServer();
	
	public boolean isAnonymous();
	
	public boolean canReceive();
	
	public ContextWrapper getConn();

	public void setConn(ContextWrapper conn);
	
	public String getHost();
	
	public void setHost(String host);
	
	public abstract Date getLastPing();
	
	public void setLastPing(Date date);

	public String getUserPath();

	public boolean isValid();
	
	public IUser copy();
	
	public void send(String message);
	
	public void processQueue();
	
	public Collection<ContextWrapper> findConnection(RoutingPath path);
	
	public Collection<ContextWrapper> getConnections();
	
	public void close();
	
	public void clear();
	
	public int count();
	
	
	

}