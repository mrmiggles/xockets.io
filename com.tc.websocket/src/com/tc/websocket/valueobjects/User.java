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
import java.util.concurrent.atomic.AtomicBoolean;

import com.tc.guice.domino.module.ServerInfo;
import com.tc.utils.JSONUtils;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.server.ContextWrapper;
import com.tc.websocket.server.RoutingPath;
import com.tc.xpage.profiler.Stopwatch;



// TODO: Auto-generated Javadoc
/**
 * The Class User.
 */
public class User implements IUser {
	
	/** The doc id. */
	private String docId;
	
	/** The session id. */
	private String sessionId;
	
	/** The user id. */
	private String userId;
	
	/** The date. */
	private Date date=new Date();
	
	/** The last ping. */
	private Date lastPing=new Date();
	
	/** The string. */
	private String string;
	
	/** The status. */
	private String status;
	
	/** The connections. */
	private Collection<ContextWrapper> connections = Collections.synchronizedCollection(new ArrayList<ContextWrapper>());
	

	/** The going offline. */
	private AtomicBoolean goingOffline= new AtomicBoolean(false);
	
	/** The conn. */
	private ContextWrapper conn;
	
	/** The host. */
	private String host; //used for clustered environments.
	
	//private static final Logger LOG = Logger.getLogger(User.class.getName());


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#isValid()
	 */
	@Override
	public boolean isValid(){
		return sessionId!=null && userId!=null;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getSessionId()
	 */
	@Override
	public String getSessionId() {
		return sessionId;
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#setSessionId(java.lang.String)
	 */
	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getUserId()
	 */
	@Override
	public String getUserId() {
		return userId;
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#setUserId(java.lang.String)
	 */
	@Override
	public void setUserId(String userid) {
		this.userId = userid;
	}




	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getDate()
	 */
	@Override
	public Date getDate() {
		return date;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#setDate(java.util.Date)
	 */
	@Override
	public void setDate(Date date) {
		this.date = date;
	}


	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		if(string==null){
			string = this.getSessionId() + "." + this.getUserId() + "." + this.getDate().getTime();
		}
		return string;
	}


	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		boolean b = false;
		if(o instanceof User){
			b=this.toString().equals(o.toString());
		}
		return b;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		return this.getSessionId().hashCode();
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getStatus()
	 */
	@Override
	public synchronized String getStatus() {
		return status;
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#setStatus(java.lang.String)
	 */
	@Override
	public synchronized void setStatus(String status) {
		this.status = status;
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#isGoingOffline()
	 */
	@Override
	public boolean isGoingOffline() {
		return goingOffline.get();
	}


	
	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#setGoingOffline(boolean)
	 */
	@Override
	public void setGoingOffline(boolean b) {
		this.goingOffline.getAndSet(b);
		
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getConn()
	 */
	@Override
	public synchronized ContextWrapper getConn() {
		return conn;
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#setConn(com.tc.websocket.server.ContextWrapper)
	 */
	@Override
	public synchronized void setConn(ContextWrapper conn) {
		this.conn = conn;
		this.addConn(conn);
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getHost()
	 */
	@Override
	public String getHost() {
		return host;
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#setHost(java.lang.String)
	 */
	@Override
	public void setHost(String host) {
		this.host = host;
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getLastPing()
	 */
	@Override
	public Date getLastPing() {
		return lastPing;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#setLastPing(java.util.Date)
	 */
	@Override
	public void setLastPing(Date lastPing) {
		this.lastPing = lastPing;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#isOpen()
	 */
	@Override
	public boolean isOpen(){
		return this.count() > 0;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#isOnServer()
	 */
	@Override
	public boolean isOnServer(){
		return ServerInfo.getInstance().isCurrentServer(this.getHost());
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#isAnonymous()
	 */
	@Override
	public boolean isAnonymous(){
		return this.getSessionId().equals(this.getUserId()) || this.getUserId().equals(StringCache.ANONYMOUS);
	}




	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUri#getUri()
	 */
	@Override
	public String getUri() {
		return this.parseUri(this.getConn());
	}
	
	/**
	 * Parses the uri.
	 *
	 * @param wrapper the wrapper
	 * @return the string
	 */
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

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getUserPath()
	 */
	@Override
	public String getUserPath(){
		String userPath =  this.getUri() + StringCache.FORWARD_SLASH + this.getUserId();
		return userPath;

	}

	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#copy()
	 */
	@Override
	public IUser copy(){
		IUser user = new User();
		user.setUserId(userId);
		user.setSessionId(sessionId);
		return user;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#send(java.lang.String)
	 */
	@Override
	public void send(String message) {
		SocketMessage msg = JSONUtils.toObject(message, SocketMessageLite.class);
		RoutingPath path = new RoutingPath(msg.getTo());	
		Collection<ContextWrapper> results = this.findConnection(path);
		Iterator<ContextWrapper> iter = results.iterator();
		while(iter.hasNext()){
			if(msg.isOnlyText()){
				iter.next().send(msg.getText());
				
			}else if(msg.isOnlyData()){
				iter.next().send(JSONUtils.toJson(msg.getData()));
				
			}else{
				iter.next().send(message);
				
			}
		}
	}

	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#findConnection(com.tc.websocket.server.RoutingPath)
	 */
	public Collection<ContextWrapper> findConnection(RoutingPath path){
		
		
		if(path.getUri().equals(this.getUserId()) || path.getUri().equalsIgnoreCase(Const.BROADCAST)){
			return this.getConnections();
		}
		
		List<ContextWrapper> results = new ArrayList<ContextWrapper>();
		
		//we get here... find the uri match
		for(ContextWrapper w : this.getConnections()){
			String uri = this.parseUri(w);
			String directUri = uri + StringCache.FORWARD_SLASH + this.getUserId();
			if(path.isWild() && uri.contains(path.getUri())){
				results.add(w);
			}else if(uri.equals(path.getUri()) ||  directUri.equals(path.getUri())){
				results.add(w);
			}
		}
		
		return results;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#processQueue()
	 */
	@Override
	@Stopwatch(time=10)
	public void processQueue() {
		for(ContextWrapper w : this.getConnections()){
			w.processQueue();
		}
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#canReceive()
	 */
	@Override
	public boolean canReceive() {
		boolean b = this.getConn()!=null
				&& this.getStatus().equals(Const.STATUS_ONLINE)
				&& this.isOnServer()
				&& this.isOpen();// && !this.isGoingOffline();

		return b;
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getDocId()
	 */
	@Override
	public String getDocId() {
		return docId;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#setDocId(java.lang.String)
	 */
	@Override
	public void setDocId(String docId) {
		this.docId = docId;
	}

	
	 
	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#getConnections()
	 */
	public Collection<ContextWrapper> getConnections() {
		return connections;
	}

	/**
	 * Sets the connections.
	 *
	 * @param connections the new connections
	 */
	public void setConnections(Collection<ContextWrapper> connections) {
		this.connections = connections;
	}
	
	/**
	 * Adds the conn.
	 *
	 * @param wrapper the wrapper
	 */
	private void addConn(ContextWrapper wrapper){
		connections.add(wrapper);
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#close()
	 */
	@Override
	public void close() {
		for(ContextWrapper w : this.getConnections()){
			if(w!=null && w.isOpen()){
				w.close();
			}
		}
		this.getConnections().clear();
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUri#getUris()
	 */
	@Override
	public List<String> getUris() {
		List<String> vec = new ArrayList<String>();
		for(ContextWrapper wrapper : this.getConnections()){
			String uri = this.parseUri(wrapper);
			if(!vec.contains(uri)){
				vec.add(uri);
			}
		}
		
		
		//add the URI paths including userId
		List<String> list = new ArrayList<String>();
		for(String str : vec){
			if(str.equals(StringCache.FORWARD_SLASH)){
				str = str + this.getUserId();
			}else{
				str = str + StringCache.FORWARD_SLASH + this.getUserId();
			}
			if(!list.contains(str)){
				list.add(str);
			}
		}
		
		vec.addAll(list);
		
		return vec;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#clear()
	 */
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


	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUser#count()
	 */
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

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUri#containsUri(java.lang.String)
	 */
	@Override
	public boolean containsUri(String uri) {
		return this.getUris().contains(uri);
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IUri#startsWith(java.lang.String)
	 */
	@Override
	public boolean startsWith(String uri) {
		for(String str : this.getUris()){
			if(str.startsWith(uri)){
				return true;
			}
		}
		return false;
	}



	@Override
	public ContextWrapper findConnection(String uri) {
		ContextWrapper wrapper = null;
		Collection<ContextWrapper> col = this.findConnection(new RoutingPath(uri));
		if(col!=null && !col.isEmpty()){
			wrapper = col.iterator().next(); // just return the first;
		}
		return wrapper;
	}
	
	
	
}
