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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSessionEvent;

import lotus.domino.NotesException;

import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.application.events.SessionListener;
import com.tc.utils.XSPUtils;
import com.tc.websocket.Const;




// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving socketSession events.
 * The class that is interested in processing a socketSession
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSocketSessionListener<code> method. When
 * the socketSession event occurs, that object's appropriate
 * method is invoked.
 *
 * @see SocketSessionEvent
 */
public class SocketSessionListener implements SessionListener {
	
	/** The log. */
	private static Logger LOG = Logger.getLogger(SocketSessionListener.class.getName());



	/* (non-Javadoc)
	 * @see com.ibm.xsp.application.events.SessionListener#sessionCreated(com.ibm.xsp.application.ApplicationEx, javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(ApplicationEx app, HttpSessionEvent event) {
		LOG.log(Level.INFO,"***sessionCreated***");
		IWebSocketBean userMgr = (IWebSocketBean) XSPUtils.getBean(Const.WEBSOCKET_BEAN);
		try {
			userMgr.registerCurrentUser();
		} catch (NotesException e) {
			LOG.log(Level.SEVERE,null, e);
		}
	}



	/* (non-Javadoc)
	 * @see com.ibm.xsp.application.events.SessionListener#sessionDestroyed(com.ibm.xsp.application.ApplicationEx, javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(ApplicationEx app, HttpSessionEvent event) {
		LOG.log(Level.INFO,"***sessionDestroyed***");	
		if(XSPUtils.context()!=null){
			IWebSocketBean userMgr = (IWebSocketBean) XSPUtils.getBean(Const.WEBSOCKET_BEAN);
			userMgr.removeCurrentUser();
		}
	}
}