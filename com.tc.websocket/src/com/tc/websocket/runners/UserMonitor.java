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

package com.tc.websocket.runners;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.tc.guice.domino.module.ServerInfo;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.factories.IUserFactory;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;


// TODO: Auto-generated Javadoc
/**
 * The Class UserMonitor.
 */
public class UserMonitor extends AbstractQueueProcessor implements Runnable {
	
	/** The server. */
	@Inject
	IDominoWebSocketServer server;
	
	/** The user factory. */
	@Inject
	IUserFactory userFactory;
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(UserMonitor.class.getName());


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if(TaskRunner.getInstance().isClosing()){
			return;
		}
		
		Session session = super.openSession();
		try {
			Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
			View view = db.getView(Const.VIEW_USERS);
			view.setAutoUpdate(false);
			Document doc = view.getFirstDocument();

			Document temp = null;
			while(doc!=null){
				//can't seem to stop this from happening in clustered environment (hack for now)	
				if(doc.isValid() && !doc.hasItem(StringCache.FIELD_CONFLICT)){ 
					IUser user = userFactory.createUser(doc);
					if(Const.STATUS_ONLINE.equals(user.getStatus()) && !ServerInfo.getInstance().isCurrentServer(user.getHost())){
						server.addUser(user);
						
						
					}else if(Const.STATUS_OFFLINE.equals(user.getStatus()) && !ServerInfo.getInstance().isCurrentServer(user.getHost())){
						server.removeUser(user);
						
					}
				}
				
				//cleanup the conflicts. (hack... need to revisit)
				else if( doc.isValid() && doc.hasItem(StringCache.FIELD_CONFLICT)){
					this.processConflict(doc);
				}
				
				
				temp = view.getNextDocument(doc);
				doc.recycle();
				doc = temp;
			}

			view.setAutoUpdate(true);
		} catch (NotesException e) {
			LOG.log(Level.SEVERE,null,e);

		}finally{
			super.closeSession(session);
		}
	}

}
