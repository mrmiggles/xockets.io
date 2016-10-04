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

import lotus.domino.Document;
import lotus.domino.NotesException;

import com.google.inject.Inject;
import com.tc.guice.domino.module.ServerInfo;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.factories.ISocketMessageFactory;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;


// TODO: Auto-generated Javadoc
/**
 * The Class AbstractQueueProcessor.
 */
public abstract class AbstractQueueProcessor extends NotesOperation {

	/** The server. */
	@Inject
	protected IDominoWebSocketServer server;

	/** The msg factory. */
	@Inject
	private ISocketMessageFactory msgFactory;

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(AbstractQueueProcessor.class.getName());


	/**
	 * Process doc.
	 *
	 * @param doc the doc
	 */
	//@Stopwatch(time=50)
	protected void processDoc(Document doc){
		try{
			ServerInfo info = ServerInfo.getInstance();
			String to = doc.getItemValueString(Const.FIELD_TO);

			IUser user = server.resolveUser(to);

			//only process if we're on the correct host, and that the user has an open connection.
			if((to.startsWith(StringCache.FORWARD_SLASH) 
					|| info.isCurrentServer(to) 
					|| (user!=null && user.isOpen() 
					&& user.isOnServer()))){

				//validate the fields
				SocketMessage msg = msgFactory.buildMessage(doc);

				//validate the data was bound correctly.
				if(msg.isValid()){
					boolean b = server.onMessage(to, msg.toJson());
					if(b){
						doc.replaceItemValue(Const.FIELD_SENTFLAG, Const.FIELD_SENTFLAG_VALUE_SENT);
						doc.save();
					}
				}else{
					doc.replaceItemValue(Const.FIELD_SENTFLAG, Const.FIELD_SENTFLAG_VALUE_ERROR);
					doc.replaceItemValue(Const.FIELD_ERROR, "Invalid message.  Please check field data.");
					doc.save();
				}
			}

		}catch(Exception e){
			LOG.log(Level.SEVERE,null, e);
		}
	}


	/**
	 * Process conflict.
	 *
	 * @param doc the doc
	 * @throws NotesException the notes exception
	 */
	//latest one in wins.
	protected void processConflict(Document doc) throws NotesException{

		Document parent = doc.getParentDatabase().getDocumentByUNID(doc.getParentDocumentUNID());
		Document winner = null;

		if(parent.getCreated().toJavaDate().before(doc.getCreated().toJavaDate())){
			winner = doc;
			this.flagForDeletion(parent);
		}else{
			winner = parent;
			this.flagForDeletion(doc);
		}


		winner.removeItem(StringCache.FIELD_CONFLICT);
		winner.removeItem(StringCache.FIELD_REF);
		winner.save();

	}

	/**
	 * Flag for deletion.
	 *
	 * @param doc the doc
	 * @throws NotesException the notes exception
	 */
	private void flagForDeletion(Document doc) throws NotesException{
		doc.removeItem(StringCache.FIELD_REF);
		doc.removeItem(StringCache.FIELD_CONFLICT);
		doc.replaceItemValue(StringCache.FIELD_FORM, Const.FIELD_VALUE_DELETE);
		doc.save();
	}


}
