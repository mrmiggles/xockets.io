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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import com.tc.guice.domino.module.ServerInfo;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.xpage.profiler.Stopwatch;

public class BroadcastQueueProcessor extends AbstractQueueProcessor implements Runnable {
	private static final Logger logger = Logger.getLogger(BroadcastQueueProcessor.class.getName());
	private List<String> clusterMates = new ArrayList<String>();

	@Override
	@Stopwatch
	public void run() {

		if(TaskRunner.getInstance().isClosing()){
			return;
		}
		
		//exit if nobody on.
		if(server.getWebSocketCount() == 0) return;

		Session session = super.openSession();
		try {

			if(ServerInfo.getInstance().isCurrentServer(Config.getInstance().getBroadcastServer())){
				Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
				View view = db.getView(Const.VIEW_BROADCAST_QUEUE);
				view.setAutoUpdate(false);
				Document doc = view.getFirstDocument();
				Document temp = null;
				while(doc!=null){
					if(doc.isValid() && !doc.hasItem(StringCache.FIELD_CONFLICT)){
						this.buildDirectMessages(doc);
						doc.replaceItemValue("sentFlag", 1);
						doc.save();
					}
					temp = view.getNextDocument(doc);
					doc.recycle();
					doc = temp;
				}
				
				
				view.setAutoUpdate(true);
			}
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null,e);

		}finally{
			super.closeSession(session);
		}
	}


	private List<String> getClusterMates(Session s) throws NotesException{
		if(clusterMates.isEmpty()){
			synchronized(clusterMates){
				if(clusterMates.isEmpty()){

					if(!Config.getInstance().isClustered()){
						clusterMates.add(ServerInfo.getInstance().getServerName());
						return clusterMates;
					}

					System.out.println("loading clustermates");
					/*
					 * all below should get recycled after session is recycled.
					 */
					Database db = s.getDatabase(StringCache.EMPTY, "names.nsf");
					View view = db.getView("($Servers)");
					Document docServer = view.getDocumentByKey(ServerInfo.getInstance().getServerName(), true);
					String clusterName = docServer.getItemValueString("ClusterName");
					DocumentCollection col = db.getView("($Clusters)").getAllDocumentsByKey(clusterName,true);
					Document doc = col.getFirstDocument();
					while(doc!=null){
						clusterMates.add(doc.getItemValueString("ServerName"));
						doc = col.getNextDocument(doc);
					}
				}
			}
		}
		return clusterMates;
	}



	@Stopwatch
	private void buildDirectMessages(Document doc) {
		Document directMessage=null;
		try{
			for(String server : this.getClusterMates(doc.getParentDatabase().getParent())){
				directMessage = doc.copyToDatabase(doc.getParentDatabase());
				directMessage.replaceItemValue("to", server);
				directMessage.save();
				directMessage.recycle();
			}
		}catch(Exception e){
			logger.log(Level.SEVERE,null, e);

			try {
				doc.replaceItemValue("error", e.getLocalizedMessage());
				doc.replaceItemValue("sentFlag", -1);
				doc.save();

				directMessage.replaceItemValue("error", e.getLocalizedMessage());
				directMessage.replaceItemValue("sentFlag", -1);
				directMessage.save();

			} catch (NotesException e1) {
				logger.log(Level.SEVERE,null, e);
			}


		}


	}



}
