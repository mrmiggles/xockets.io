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

package com.tc.websocket.factories;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import org.apache.commons.io.IOUtils;

import com.tc.utils.JSONUtils;
import com.tc.websocket.Const;
import com.tc.websocket.valueobjects.SocketMessage;
import com.tc.xpage.profiler.Profiled;

public class SocketMessageFactory implements ISocketMessageFactory {
	private static final Logger logger = Logger.getLogger(SocketMessageFactory.class.getName());
	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.factories.ISocketMessageFactory#buildMessages(lotus.domino.ViewEntryCollection)
	 */
	@Override
	@Profiled
	public List<SocketMessage> buildMessages(ViewEntryCollection col){
		List<SocketMessage> list = new ArrayList<SocketMessage>();
		
		try{
		ViewEntry entry = col.getFirstEntry();
		ViewEntry temp = null;
		while(entry!=null){
			
			if(entry.isDocument()){
				Document doc = entry.getDocument();
				if(doc.isValid()){
					//add the message to the list.
					list.add(this.buildMessage(doc));
				}
			}
			
			temp = col.getNextEntry(entry);
			entry.recycle();
			entry = temp;
		}
		
		
		}catch(NotesException n){
			logger.log(Level.SEVERE,null,n);
			
		}
		
		return list;
	}
	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.factories.ISocketMessageFactory#buildMessage(lotus.domino.Document)
	 */
	@Override
	@Profiled
	public SocketMessage buildMessage(Document doc){
		SocketMessage msg = null;
		try{
			RichTextItem rtitem = (RichTextItem) doc.getFirstItem(Const.FIELD_JSON);
			String json = null;

			if(doc.hasEmbedded()){
				json = this.scanForAttachedJson(rtitem);
			}else{
				json = rtitem.getUnformattedText();
			}

			msg = JSONUtils.toObject(json, SocketMessage.class);

			if(msg==null){
				return new SocketMessage();//return empty invalid message.
			}


			msg.setJson(json);//so we don't have to re-serialize...

		}catch(Exception e){
			logger.log(Level.SEVERE,null, e);
			try{
				doc.replaceItemValue("sentFlag", -1);
				doc.replaceItemValue("error", e.getMessage());
				doc.save();
			}catch(NotesException n){
				logger.log(Level.SEVERE,null, n);
			}
		}
		return msg;
	}



	private String scanForAttachedJson(RichTextItem rtitem)throws NotesException{
		String json = null;
		@SuppressWarnings("unchecked")
		Vector<EmbeddedObject> objects = rtitem.getEmbeddedObjects();
		for(EmbeddedObject eo: objects){
			if(eo.getName().toLowerCase().endsWith(".json")){
				InputStream in = eo.getInputStream();
				try {
					json = IOUtils.toString(in,"UTF-8");
					int start = json.indexOf('{');
					int end = json.lastIndexOf('}');
					json=json.substring(start,end) + "}";
				} catch (IOException e) {
					logger.log(Level.SEVERE,null,e);
				}finally{
					IOUtils.closeQuietly(in);
					eo.recycle();
					eo = null;
				}
			}
		}
		return json;
	}

}
