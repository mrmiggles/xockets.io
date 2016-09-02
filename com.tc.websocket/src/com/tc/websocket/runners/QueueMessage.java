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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;

import com.tc.utils.JSONUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.valueobjects.SocketMessage;
import com.tc.xpage.profiler.Stopwatch;

public class QueueMessage extends NotesOperation {

	private static Logger LOG = Logger.getLogger(QueueMessage.class.getName());


	private SocketMessage msg;



	@Override
	@Stopwatch
	public void run() {

		if(TaskRunner.getInstance().isClosing()){
			return;
		}

		//if the message has already been persisted don't process it again.
		if(!msg.isPersisted()){

			Session session = this.openSession();
			try {
				//mark object as persisted so we don't keep persisting.
				msg.setPersisted(true);
				
				Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
				Document doc = db.createDocument();
				doc.replaceItemValue("Form", "fmSocketMessage");
				doc.replaceItemValue("text", msg.getText());
				doc.replaceItemValue("to", msg.getTo());
				doc.replaceItemValue("from", msg.getFrom());
				doc.replaceItemValue("event", StringCache.EMPTY);
				doc.replaceItemValue("durable", String.valueOf(msg.isDurable()));
				doc.replaceItemValue("persisted", String.valueOf(msg.isPersisted()));

				
				this.attach(doc, msg);

				db.recycle();
				doc.recycle();

			} catch (NotesException e) {
				LOG.log(Level.SEVERE,null, e);

			}finally{
				this.closeSession(session);
			}
		}

	}

	private void attach(Document doc, SocketMessage msg){
		File file = JSONUtils.write(msg);
		try{
			RichTextItem rtitem = doc.createRichTextItem("json");
			EmbeddedObject eo = rtitem.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, file.getAbsolutePath(), Const.ATTACH_NAME);
			doc.save();

			//cleanup.
			eo.recycle();
			rtitem.recycle();
		}catch(Exception e){
			LOG.log(Level.SEVERE,null,e);

		}finally{
			if(file!=null && file.exists()){
				file.delete();
			}
		}
	}

	public void setMsg(SocketMessage msg) {
		this.msg = msg;
	}

}
