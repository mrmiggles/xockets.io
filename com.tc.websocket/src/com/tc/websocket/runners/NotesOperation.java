package com.tc.websocket.runners;

import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.tc.guice.domino.module.SessionFactory;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.Const;

public abstract class NotesOperation implements Runnable {
	private static final Logger logger = Logger.getLogger(NotesOperation.class.getName());
	
	
	public Session openSession(){
		return SessionFactory.openSession(Config.getInstance().getUsername(), Config.getInstance().getPassword());
	}
	
	public void closeSession(Session session){
		SessionFactory.closeSession(session);
	}
	
	public void closeSession(Database db) {
		SessionFactory.closeSession(db);
	}
	
	public void closeSession(Document doc){
		SessionFactory.closeSession(doc);
	}
	
	public void stampDocuments(String search, String field, Object value){
		Session session = this.openSession();
		try {	
			Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
			DocumentCollection col = db.search(search);
			if(col!=null && col.getCount() > 0){
				col.stampAll(field, value);
			}
			
			//cleanup
			col.recycle();
			db.recycle();
			
		} catch (NotesException e) {
			if(!e.text.contains("No documents were categorized")){
				logger.log(Level.SEVERE,null,e);
			}
		}finally{
			closeSession(session);
		}
	}
	
	public void removeDocuments(String search){
		Session session = openSession();
		try {	
			Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
			DocumentCollection col = db.search(search);
			if(col!=null && col.getCount() > 0){
				col.removeAll(true);
			}
			
			//cleanup
			col.recycle();
			db.recycle();
			
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null,e);

		}finally{
			closeSession(session);
		}
	}

}
