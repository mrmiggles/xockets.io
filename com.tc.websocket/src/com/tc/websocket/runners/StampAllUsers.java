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

import com.tc.guice.domino.module.ServerInfo;
import com.tc.guice.domino.module.SessionFactory;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.xpage.profiler.Stopwatch;


// TODO: Auto-generated Javadoc
/**
 * The Class StampAllUsers.
 */
public class StampAllUsers extends NotesOperation implements Runnable {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(StampAllUsers.class.getName());

	/** The status. */
	private String status;





	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	@Stopwatch
	public void run() {

		if(TaskRunner.getInstance().isClosing()){
			return;
		}


		Session session =  this.openSession();
		try {	
			Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
			View view = db.getView(Const.VIEW_USERS);
			Document doc = view.getFirstDocument();
			Document temp = null;
			while(doc!=null){
				String host = doc.getItemValueString(Const.FIELD_HOST);
				if(ServerInfo.getInstance().isCurrentServer(host)){
					String currentStatus= doc.getItemValueString(Const.FIELD_STATUS);
					//only update if we need to.
					if(!this.getStatus().equals(currentStatus)){
						doc.replaceItemValue(Const.FIELD_STATUS, this.getStatus());
						doc.save();
					}
				}

				temp = view.getNextDocument(doc);
				doc.recycle();
				doc = temp;
			}

		} catch (NotesException e) {
			LOG.log(Level.SEVERE,null,e);

		}finally{
			SessionFactory.closeSession(session);
		}

	}




	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}




	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
