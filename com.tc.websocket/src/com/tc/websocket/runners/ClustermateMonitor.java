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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import com.tc.guice.domino.module.ServerInfo;
import com.tc.utils.DateUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.xpage.profiler.Stopwatch;


// TODO: Auto-generated Javadoc
/**
 * The Class ClustermateMonitor.
 */
public class ClustermateMonitor extends AbstractQueueProcessor implements Runnable {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(ClustermateMonitor.class.getName());


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	@Stopwatch
	public void run() {
		
		if(TaskRunner.getInstance().isClosing()){
			return;
		}
		
		Session session = this.openSession();
		try {
			Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
			View view = db.getView(Const.VIEW_SERVER_STATUS);

			//first stamp the document with latest time stamp
			Document doc = view.getDocumentByKey(ServerInfo.getInstance().getServerName(),true);
			if(doc==null){
				doc = db.createDocument();
			}

			//update the server status doc.
			doc.replaceItemValue(Const.FIELD_HOST, ServerInfo.getInstance().getServerName());
			doc.replaceItemValue("updateDtm", db.getParent().createDateTime(new Date()));
			doc.replaceItemValue(Const.FIELD_STATUS, Const.STATUS_ONLINE);
			doc.replaceItemValue("Form", "fmServerStatus");
			doc.save();
			doc.recycle();

			//now check the server we're monitoring make sure its up.
			doc = view.getDocumentByKey(Config.getInstance().getClustermateMonitor(), true);
			if(doc!=null && Const.STATUS_ONLINE.equals(doc.getItemValueString(Const.FIELD_STATUS))){

				//calculate elapsedSeconds minus the thread run interval.
				long elapsedSeconds = DateUtils.getTimeDiffSec(doc.getLastModified().toJavaDate(), new Date()) - Const.CLUSTERMATE_MONITOR_INTERVAL;
				if(elapsedSeconds >= Config.getInstance().getClustermateExpiration()){
					this.setClustermateUsersOffline(db, Config.getInstance().getClustermateMonitor());
				}
			}

		} catch (NotesException e) {
			LOG.log(Level.SEVERE,null,e);

		}finally{
			this.closeSession(session);
		}

	}


	/**
	 * Sets the clustermate users offline.
	 *
	 * @param db the db
	 * @param clustermate the clustermate
	 */
	@Stopwatch
	private void setClustermateUsersOffline(Database db, String clustermate) {

		try {
			View view = db.getView(Const.VIEW_USERS);
			view.setAutoUpdate(false);
			Document doc = view.getFirstDocument();
			Document temp = null;
			while(doc!=null){

				if(doc.isValid()){
					String host = doc.getItemValueString(Const.FIELD_HOST);

					//if hosts are the same
					if(host!=null && host.equals(clustermate)){
						doc.replaceItemValue(Const.FIELD_STATUS, Const.STATUS_OFFLINE);
						doc.save();
					}
				}

				temp = view.getNextDocument(doc);
				doc.recycle();
				doc = temp;
			}
			view.setAutoUpdate(true);

		} catch (NotesException e) {
			LOG.log(Level.SEVERE,null,e);
		}

	}


}
