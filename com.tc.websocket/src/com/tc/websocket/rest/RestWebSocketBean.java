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

package com.tc.websocket.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import lotus.domino.NotesException;

import com.google.inject.Inject;
import com.tc.utils.XSPUtils;
import com.tc.websocket.jsf.AbstractWebSocketBean;
import com.tc.websocket.runners.ApplyStatus;
import com.tc.websocket.runners.TaskRunner;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;


// TODO: Auto-generated Javadoc
/**
 * The Class RestWebSocketBean.
 */
public class RestWebSocketBean extends AbstractWebSocketBean {
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(RestWebSocketBean.class.getName());

	/** The req. */
	private HttpServletRequest req;


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#init(com.tc.websocket.server.IDominoWebSocketServer)
	 */
	@Override
	@Inject
	public void init(IDominoWebSocketServer server){
		this.server = server;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#registerCurrentUser()
	 */
	@Override
	public void registerCurrentUser() throws NotesException {
		super.registerCurrentUser(req);
	}

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	private String getUserName() {
		String username = null;
		try {
			username =XSPUtils.session().getEffectiveUserName();
		} catch (NotesException e) {
			LOG.log(Level.SEVERE, null, e);
		}
		return username;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#removeCurrentUser()
	 */
	@Override
	public void removeCurrentUser() {
		
		IUser user = server.resolveUser(this.getUserName());
		if(user!=null){
			user.setGoingOffline(true);
			ApplyStatus status = new ApplyStatus(user);
			status.setRemoveUser(true);
			TaskRunner.getInstance().add(status);
		}
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#getWebSocketUrl()
	 */
	@Override
	public String getWebSocketUrl() {
		return super.getWebSocketUrl(req);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#setRequest(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void setRequest(HttpServletRequest req) {
		this.req = req;
	}


}
