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
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.tc.guice.domino.module.ServerInfo;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.server.ContextWrapper;


public class User implements IUser {
	private String docId;
	private String sessionId;
	private String userId;
	private Date date=new Date();
	private Date lastPing=new Date();
	private String string;
	private String status;


	private boolean goingOffline;
	private ContextWrapper conn;
	private String host; //used for clustered environments.
	private Queue<String> messages = new ConcurrentLinkedQueue<String>(); //used in case user is not writeable.



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
		return this.getConn()!=null && this.getConn().isOpen();
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
		String uri = StringCache.FORWARD_SLASH;
		if(this.isOpen()){
			uri = StrUtils.middle(this.getConn().getResourceDescriptor(), Const.WEBSOCKET_URI, this.getSessionId());
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
		this.messages.add(message);
		this.processQueue();
	}

	@Override
	public void processQueue() {
		while(this.getConn().channel().isWritable() && !this.messages.isEmpty()){
			String msg = messages.poll();
			if(msg!=null){
				this.getConn().send(msg);
			}
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
}
