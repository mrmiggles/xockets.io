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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.tc.guice.domino.module.ServerInfo;
import com.tc.utils.JSONUtils;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.server.ContextWrapper;
import com.tc.websocket.server.RoutingPath;
import com.tc.xpage.profiler.Stopwatch;


public class User implements IUser {
	private String docId;
	private String sessionId;
	private String userId;
	private Date date=new Date();
	private Date lastPing=new Date();
	private String string;
	private String status;
	private Collection<ContextWrapper> connections = Collections.synchronizedCollection(new ArrayList<ContextWrapper>());
	

	private boolean goingOffline;
	private ContextWrapper conn;
	private String host; //used for clustered environments.
	
	//private static final Logger logger = Logger.getLogger(User.class.getName());


	@Override
	public boolean isValid(){
		return sessionId!=null && userId!=null;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}


	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	@Override
	public String getUserId() {
		return userId;
	}


	@Override
	public void setUserId(String userid) {
		this.userId = userid;
	}



	@Override
	public Date getDate() {
		return date;
	}


	@Override
	public void setDate(Date date) {
		this.date = date;
	}


	@Override
	public String toString(){
		if(string==null){
			string = this.getSessionId() + "." + this.getUserId() + "." + this.getDate().getTime();
		}
		return string;
	}


	@Override
	public boolean equals(Object o){
		boolean b = false;
		if(o instanceof User){
			b=this.toString().equals(o.toString());
		}
		return b;
	}

	@Override
	public int hashCode(){
		return this.getSessionId().hashCode();
	}

	@Override
	public synchronized String getStatus() {
		return status;
	}


	@Override
	public synchronized void setStatus(String status) {
		this.status = status;
	}


	@Override
	public synchronized boolean isGoingOffline() {
		return goingOffline;
	}


	@Override
	public synchronized void setGoingOffline(boolean goingOffline) {
		this.goingOffline = goingOffline;
	}


	@Override
	public synchronized ContextWrapper getConn() {
		return conn;
	}


	@Override
	public synchronized void setConn(ContextWrapper conn) {
		this.conn = conn;
		this.addConn(conn);
	}


	@Override
	public String getHost() {
		return host;
	}


	@Override
	public void setHost(String host) {
		this.host = host;
	}


	@Override
	public Date getLastPing() {
		return lastPing;
	}


	@Override
	public void setLastPing(Date lastPing) {
		this.lastPing = lastPing;
	}

	@Override
	public boolean isOpen(){
		return this.count() > 0;
	}

	@Override
	public boolean isOnServer(){
		return ServerInfo.getInstance().isCurrentServer(this.getHost());
	}
	
	@Override
	public boolean isAnonymous(){
		return this.getSessionId().equals(this.getUserId()) || this.getUserId().equals(StringCache.ANONYMOUS);
	}



	/*
	 * (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getUri()
	 * this must stay dynamic in case the uri changes in the browser / client.
	 */
	@Override
	public String getUri() {
		return this.parseUri(this.getConn());
	}
	
	private String parseUri(ContextWrapper wrapper){
		String uri = StringCache.FORWARD_SLASH;
		if(wrapper!=null && wrapper.isOpen()){
			uri = StrUtils.middle(wrapper.getResourceDescriptor(), Const.WEBSOCKET_URI, this.getSessionId());
			if(uri.endsWith(StringCache.FORWARD_SLASH)){
				uri = uri.substring(0, uri.length() -1);
			}
		}
		return uri;
	}

	@Override
	public String getUserPath(){
		String userPath =  this.getUri() + StringCache.FORWARD_SLASH + this.getUserId();
		return userPath;

	}

	
	@Override
	public IUser copy(){
		IUser user = new User();
		user.setUserId(userId);
		user.setSessionId(sessionId);
		return user;
	}

	@Override
	public void send(String message) {
		SocketMessage msg = JSONUtils.toObject(message, SocketMessageLite.class);
		RoutingPath path = new RoutingPath(msg.getTo());	
		Collection<ContextWrapper> results = this.findConnection(path);
		Iterator<ContextWrapper> iter = results.iterator();
		while(iter.hasNext()){
			iter.next().send(message);
		}
	}

	
	public Collection<ContextWrapper> findConnection(RoutingPath path){
		List<ContextWrapper> results = new ArrayList<ContextWrapper>();
		
		if(path.getUri().equals(this.getUserId()) || path.getUri().equalsIgnoreCase(Const.BROADCAST)){
			return this.getConnections();
		}
		
		//we get here... find the uri match
		for(ContextWrapper w : this.getConnections()){
			String uri = this.parseUri(w);
			if(path.isWild() && uri.contains(path.getUri())){
				results.add(w);
			}else if(uri.equals(path.getUri())){
				results.add(w);
			}
		}
		
		return results;
	}

	@Override
	@Stopwatch(time=10)
	public void processQueue() {
		for(ContextWrapper w : this.getConnections()){
			w.processQueue();
		}
	}

	@Override
	public boolean canReceive() {
		boolean b = this.getConn()!=null
				&& this.getStatus().equals(Const.STATUS_ONLINE)
				&& this.isOnServer()
				&& !this.getConn().isClosed() && !this.isGoingOffline();

		return b;
	}
	
	@Override
	public String getDocId() {
		return docId;
	}

	@Override
	public void setDocId(String docId) {
		this.docId = docId;
	}

	public Collection<ContextWrapper> getConnections() {
		return connections;
	}

	public void setConnections(Collection<ContextWrapper> connections) {
		this.connections = connections;
	}
	
	private void addConn(ContextWrapper wrapper){
		connections.add(wrapper);
	}

	@Override
	public void close() {
		for(ContextWrapper w : this.getConnections()){
			if(w!=null && w.isOpen()){
				w.close();
			}
		}
		this.getConnections().clear();
	}

	@Override
	public List<String> getUris() {
		List<String> vec = new ArrayList<String>();
		for(ContextWrapper wrapper : this.getConnections()){
			vec.add(this.parseUri(wrapper));
		}
		return vec;
	}

	@Override
	public void clear() {
		List<ContextWrapper> list = new ArrayList<ContextWrapper>();
		
		for(ContextWrapper wrapper : this.getConnections()){
			if(wrapper.isOpen()){
				list.add(wrapper);
			}
		}
		this.getConnections().clear();
		this.getConnections().addAll(list);
	}

	@Override
	public int count() {
		int cntr = 0;
		for(ContextWrapper w : this.getConnections()){
			if(w!=null && w.isOpen()){
				cntr ++;
			}
		}
		return cntr;
	}

	@Override
	public boolean containsUri(String uri) {
		return this.getUris().contains(uri);
	}
	
	@Override
	public boolean startsWith(String uri) {
		for(String str : this.getUris()){
			if(str.startsWith(uri)){
				return true;
			}
		}
		return false;
	}
	
	
	
}
