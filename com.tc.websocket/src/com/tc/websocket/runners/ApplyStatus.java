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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import com.google.inject.Inject;
import com.tc.guice.domino.module.ServerInfo;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;
import com.tc.xpage.profiler.Stopwatch;

public class ApplyStatus extends NotesOperation {

	private static Logger logger = Logger.getLogger(ApplyStatus.class.getName());

	@Inject
	IDominoWebSocketServer server;

	private IUser user;

	private boolean removeUser;


	public ApplyStatus(IUser user){
		this.user = user;
	}

	public ApplyStatus setRemoveUser(boolean removeUser){
		this.removeUser=removeUser;
		return this;
	}


	@Override
	@Stopwatch
	public void run() {

		if(TaskRunner.getInstance().isClosing()){
			return;
		}

		if(ServerInfo.getInstance().isCurrentServer(user.getHost())){

			if(user.isGoingOffline()){
				this.offline();

			}else {
				this.online();

			}
		}

	}


	@Stopwatch
	private void online(){
		Session session = null;
		try {

			if(user!=null && !StrUtils.isEmpty(this.getUserId())){
				session = this.openSession();

				this.user.setStatus(Const.STATUS_ONLINE);

				logger.log(Level.FINE,this.getUserId());

				Document doc = this.getUserDoc(session, true);

				doc.replaceItemValue(Const.FIELD_FORM, Const.FIELD_VALUE_USER);

				Item userId = doc.replaceItemValue(Const.FIELD_USERID, this.getUserId());
				userId.setAuthors(true);

				doc.replaceItemValue(Const.FIELD_SESSIONID, this.getSessionId());
				doc.replaceItemValue(Const.FIELD_STATUS, this.getStatus());
				doc.replaceItemValue(Const.FIELD_HOST, user.getHost());
				doc.replaceItemValue(Const.FIELD_URI, user.getUri());
				doc.save();

				user.setDocId(doc.getUniversalID());

				if(!user.isAnonymous()){
					deleteAnonymousDoc(session);
				}

				doc.recycle();
			}



		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}finally{
			this.closeSession(session);
		}
	}




	@Stopwatch
	private void offline(){
		Session session = null;

		try {
			if(user!=null && !StrUtils.isEmpty(this.getSessionId())){

				session = this.openSession();

				Document doc = this.getUserDoc(session, false);

				if(doc==null) return;

				this.user.setStatus(Const.STATUS_OFFLINE);

				assert(user.getConn().isClosed()) : "user.getConn() should be closed prior to setting offline!";

				this.user.setConn(null); //make sure we null this out, make available for gc.

				doc.replaceItemValue(Const.FIELD_FORM, Const.FIELD_VALUE_USER);
				doc.replaceItemValue(Const.FIELD_SESSIONID, this.getSessionId());
				doc.replaceItemValue(Const.FIELD_STATUS, this.getStatus());
				doc.replaceItemValue(Const.FIELD_HOST, user.getHost());
				doc.save();

				user.setDocId(doc.getUniversalID());

				if(removeUser){
					server.removeUser(user);
				}


				//make sure if user transitioned from anonymous that doc is cleaned up
				this.deleteAnonymousDoc(session);


				doc.recycle();
			}

		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);

		}finally{
			this.closeSession(session);
		}
	}


	private Document getByUNID(Database db, String unid){
		Document doc = null;

		try {
			doc = db.getDocumentByUNID(unid);
		} catch (NotesException e) {
			logger.log(Level.FINE, e.text);
		}

		return doc;
	}

	private Document getUserDoc(Session session, boolean create) throws NotesException{
		Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
		Document doc = null;

		if(!StrUtils.isEmpty(user.getDocId())){
			doc = this.getByUNID(db, user.getDocId());
		}

		if(doc == null){
			View users = db.getView(Const.VIEW_USERS);
			View sessions = db.getView(Const.VIEW_SESSIONS);
			doc = users.getDocumentByKey(user.getUserId().trim(),true);
			if(doc == null){
				doc = sessions.getDocumentByKey(user.getSessionId(),true);
			}
			//if we get here... create the doc.
			if(doc==null && create){
				doc = db.createDocument();
			}
			//cleanup
			users.recycle();
			sessions.recycle();


		}
		return doc;
	}


	//if the user moved from anonymous to person to anonymous to person
	private void deleteAnonymousDoc(Session session) throws NotesException{
		Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
		View view = db.getView(Const.VIEW_USERS);
		Vector<String> keys = new Vector<String>(3);
		keys.add(this.getSessionId().trim());
		keys.add(this.getSessionId().trim());


		DocumentCollection col = view.getAllDocumentsByKey(keys,true);
		if(col!=null && col.getCount() > 0){
			col.stampAll(Const.FIELD_FORM, Const.FIELD_VALUE_DELETE);
			col.recycle();
		}
	}



	public String getUserId() {
		return user.getUserId();
	}



	public String getSessionId() {
		return this.user.getSessionId();
	}




	public String getStatus() {
		return this.user.getStatus();
	}




}