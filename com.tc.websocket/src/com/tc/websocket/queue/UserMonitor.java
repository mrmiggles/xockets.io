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

package com.tc.websocket.queue;

import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import com.google.inject.Inject;
import com.tc.guice.domino.module.ServerInfo;
import com.tc.guice.domino.module.SessionFactory;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.factories.IUserFactory;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;

public class UserMonitor extends AbstractQueueProcessor implements Runnable {
	
	@Inject
	IDominoWebSocketServer server;
	
	@Inject
	IUserFactory userFactory;
	
	private static final Logger logger = Logger.getLogger(UserMonitor.class.getName());

	@Override
	public void run() {
		if(TaskRunner.getInstance().isClosing()){
			return;
		}
		
		Session session = SessionFactory.openSession(Config.getInstance().getUsername(), Config.getInstance().getPassword());
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
			logger.log(Level.SEVERE,null,e);

		}finally{
			SessionFactory.closeSession(session);
		}
	}

}
