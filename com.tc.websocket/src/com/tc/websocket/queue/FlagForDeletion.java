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
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.tc.guice.domino.module.SessionFactory;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.xpage.profiler.Profiled;

public class FlagForDeletion implements Runnable {
	
	private static final Logger logger = Logger.getLogger(FlagForDeletion.class.getName());


	private static final String SRCH="Form=\"fmSocketMessage\" & sentFlag=1";

	@Override
	@Profiled
	public void run() {
		
		if(TaskRunner.getInstance().isClosing()){
			return;
		
		}else if(!Config.getInstance().isBroadcastServer()){
			return;
		}
		

		Session session = SessionFactory.openSession(Config.getInstance().getUsername(), Config.getInstance().getPassword());
		try {	
			Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
			DocumentCollection col = db.search(SRCH);
			col.stampAll("Form", "delete");
			
			//cleanup
			col.recycle();
			db.recycle();
			
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null,e);

		}finally{
			SessionFactory.closeSession(session);
		}

	}



	






}
