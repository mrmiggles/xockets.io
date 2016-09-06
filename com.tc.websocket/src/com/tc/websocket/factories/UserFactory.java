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


package com.tc.websocket.factories;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Document;
import lotus.domino.NotesException;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.guice.domino.module.ServerInfo;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.valueobjects.IUser;


// TODO: Auto-generated Javadoc
/**
 * A factory for creating User objects.
 */
public class UserFactory implements IUserFactory {
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(UserFactory.class.getName());
	
	/** The guicer. */
	@Inject
	IGuicer guicer;


	/* (non-Javadoc)
	 * @see com.tc.websocket.factories.IUserFactory#createUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IUser createUser(String sessionId, String userId, String status){
		IUser user = guicer.createObject(IUser.class);
		user.setDate(new Date());
		user.setSessionId(sessionId);
		
		if(StringCache.ANONYMOUS.equals(userId)){
			user.setUserId(sessionId);
		}else{
			user.setUserId(userId);
		}
		
		
		user.setHost(ServerInfo.getInstance().getServerName());
		
		if(Const.STATUS_ONLINE.equals(status)){
			user.setGoingOffline(false);
			user.setStatus(Const.STATUS_ONLINE);
			
		}else{
			user.setGoingOffline(true);
			user.setStatus(Const.STATUS_OFFLINE);
			
		}
		
		LOG.log(Level.INFO, user.getUserId() + " created ");
		
		return user;
	}
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.factories.IUserFactory#createUser(lotus.domino.Document)
	 */
	//this method is used to create a user from a different server in a cluster
	@Override
	public IUser createUser(Document doc) {
		IUser user = guicer.createObject(IUser.class);

		try {
			
			user.setConn(null); // conn should be null, user is on a different server in the cluster.
			user.setDate(doc.getCreated().toJavaDate());
			user.setGoingOffline(false);
			user.setHost(doc.getItemValueString(Const.FIELD_HOST));
			user.setSessionId(doc.getItemValueString(Const.FIELD_SESSIONID));
			user.setStatus(doc.getItemValueString(Const.FIELD_STATUS));
			user.setUserId(doc.getItemValueString(Const.FIELD_USERID));
			
		} catch (NotesException e) {
			LOG.log(Level.SEVERE,null, e);
		}
		
		return user;
	}

}
