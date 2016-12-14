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

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.ibm.xsp.application.ApplicationEx;
import com.tc.utils.XSPUtils;
import com.tc.websocket.server.IDominoWebSocketServer;

import lotus.domino.NotesException;


// TODO: Auto-generated Javadoc
/**
 * The Class WebSocketBean.
 */
public class WebSocketBean extends AbstractWebSocketBean implements Serializable, IWebSocketBean{

	/** The log. */
	private static Logger LOG = Logger.getLogger(WebSocketBean.class.getName());
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2680334746972588793L;
	
	/** The is on. */
	private final AtomicBoolean isOn = new AtomicBoolean(false);


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#init(com.tc.websocket.server.IDominoWebSocketServer)
	 */
	@Override
	@Inject
	public void init(IDominoWebSocketServer server){
		if(isOn.compareAndSet(false, true)) {
			this.server = server;
			ApplicationEx appEx = (ApplicationEx) XSPUtils.app();
			
			//add the listeners
			appEx.addApplicationListener(new AppListener());
			appEx.addSessionListener(new SocketSessionListener());
			

			//catch the very first user.
			try {
				this.registerCurrentUser();
			} catch (NotesException e) {
				LOG.log(Level.SEVERE,null,e);
			}
		}
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#registerCurrentUser()
	 */
	@Override
	public void registerCurrentUser() throws NotesException {
		super.registerCurrentUser(XSPUtils.getRequest());
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#removeCurrentUser()
	 */
	@Override
	public void removeCurrentUser() {
		super.removeCurrentUser(XSPUtils.getRequest());
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#getWebSocketUrl()
	 */
	@Override
	public String getWebSocketUrl() {
		return super.getWebSocketUrl(XSPUtils.getRequest());
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#setRequest(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void setRequest(HttpServletRequest req) {
		throw new UnsupportedOperationException("Unsupported");
	}








}
