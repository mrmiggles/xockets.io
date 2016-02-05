package com.tc.guice.domino.module;

import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.ibm.domino.xsp.module.nsf.SessionCloner;

public class Recycler {
	
	private static final Logger logger = Logger.getLogger(Recycler.class.getName());
	
	public static void recycle(Document doc){
		try {
			if(doc==null) return;
			doc.recycle();
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}
	}
	
	public static void recycle(Session session){
		try {
			if(session==null) return;
			session.recycle();
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}
	}
	
	public static void recycle(View view){
		try {
			if(view==null) return;
			view.recycle();
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}
	}
	
	
	public static void recycle(ViewEntry entry){
		try {
			if(entry==null) return;
			entry.recycle();
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}
	}
	
	
	public static void recycle(DocumentCollection col){
		try {
			if(col==null) return;
			col.recycle();
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}
	}
	
	
	public static void recycle(ViewEntryCollection col){
		try {
			if(col==null) return;
			col.recycle();
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null, e);
		}
	}
	
	
	
	public static void recycle(SessionCloner cloner){
		try {
			cloner.recycle();
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null,e);
		}
	}
	
}
