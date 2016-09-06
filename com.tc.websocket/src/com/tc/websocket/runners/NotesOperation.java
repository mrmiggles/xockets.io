/*
 * 
 */
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


// TODO: Auto-generated Javadoc
/**
 * The Class NotesOperation.
 */
public abstract class NotesOperation implements Runnable {
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(NotesOperation.class.getName());
	
	
	/**
	 * Open session.
	 *
	 * @return the session
	 */
	public Session openSession(){
		return SessionFactory.openSessionDefaultToTrusted(Config.getInstance().getUsername(), Config.getInstance().getPassword());
	}
	
	/**
	 * Close session.
	 *
	 * @param session the session
	 */
	public void closeSession(Session session){
		SessionFactory.closeSession(session);
	}
	
	/**
	 * Close session.
	 *
	 * @param db the db
	 */
	public void closeSession(Database db) {
		SessionFactory.closeSession(db);
	}
	
	/**
	 * Close session.
	 *
	 * @param doc the doc
	 */
	public void closeSession(Document doc){
		SessionFactory.closeSession(doc);
	}
	
	/**
	 * Stamp documents.
	 *
	 * @param search the search
	 * @param field the field
	 * @param value the value
	 */
	public void stampDocuments(String search, String field, Object value){
		Session session = this.openSession();
		try {	
			Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
			DocumentCollection col = db.search(search);
			if(col!=null && col.getCount() > 0){
				col.stampAll(field, value);
				col.recycle();
			}
			
			//cleanup
			db.recycle();
			
		} catch (NotesException e) {
			if(!e.text.contains("No documents were categorized")){
				LOG.log(Level.SEVERE,null,e);
			}
		}finally{
			closeSession(session);
		}
	}
	
	/**
	 * Removes the documents.
	 *
	 * @param search the search
	 */
	public void removeDocuments(String search){
		Session session = openSession();
		try {	
			Database db = session.getDatabase(StringCache.EMPTY, Const.WEBSOCKET_PATH);
			DocumentCollection col = db.search(search);
			if(col!=null && col.getCount() > 0){
				col.removeAll(true);
				col.recycle();
			}
			
			//cleanup
			db.recycle();
			
		} catch (NotesException e) {
			LOG.log(Level.SEVERE,null,e);

		}finally{
			closeSession(session);
		}
	}

}
