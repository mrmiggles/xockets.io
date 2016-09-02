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

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.xpage.profiler.Stopwatch;

public class QueueProcessor extends AbstractQueueProcessor implements Runnable {
	private static final Logger LOG = Logger.getLogger(QueueProcessor.class.getName());

	@Override
	@Stopwatch
	public void run() {

		if(TaskRunner.getInstance().isClosing()){
			return;
		}
		
		if(server.getWebSocketAndObserverCount() == 0)return;

		Session session = this.openSession();
		try {
			Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
			View view = db.getView(Const.VIEW_MSG_QUEUE);
			if(view.getAllEntries().getCount() > 0){
				view.setAutoUpdate(false);
				Document doc = view.getFirstDocument();
				Document temp = null;
				while(doc!=null){
					if(doc.isValid() && !doc.hasItem(StringCache.FIELD_CONFLICT)){
						this.processDoc(doc);

					}else if(doc.isValid() && doc.hasItem(StringCache.FIELD_CONFLICT)){
						this.processConflict(doc);
					}
					temp = view.getNextDocument(doc);
					doc.recycle();
					doc = temp;	
				}

				view.setAutoUpdate(true);
			}
		} catch (NotesException e) {
			LOG.log(Level.SEVERE,null,e);

		}finally{
			this.closeSession(session);
		}
	}




}
