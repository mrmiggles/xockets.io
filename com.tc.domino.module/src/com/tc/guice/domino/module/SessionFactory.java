package com.tc.guice.domino.module;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.domino.xsp.module.nsf.NSFComponentModule;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.domino.xsp.module.nsf.SessionCloner;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;
import lotus.domino.local.DocumentCollection;


public class SessionFactory {



	private static final Logger logger = Logger.getLogger(SessionFactory.class.getName());

	public static Session openSession(){
		return openSession(null,"");
	}
	
	
	public static void main(String[] args) throws NotesException{
		System.setProperty("java.library.path","C:/Domino");
		Session s = SessionFactory.openSession("admin admin/marksdev","password");
		System.out.println(s.getCommonUserName());
	}
	
	
	public static Session openTrusted(){
		Session s = null;
		try {
			NotesThread.sinitThread();
			s = NotesFactory.createTrustedSession();
			logger.log(Level.FINE,"opened trusted session for " + s.getEffectiveUserName());
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null,e);
		}
		return s;
	}
	
	public static Session openSession(String username, String password){
		Session s = null;
		try {
			NotesThread.sinitThread();
			s = NotesFactory.createSession((String) null, username, password);
			logger.log(Level.FINE,"opened session for " + s.getUserName());
		} catch (NotesException e) {
			logger.log(Level.SEVERE,null,e);
		}
		return s;
	}


	public static void closeSession(Session session){
		logger.log(Level.FINE,"closing session");
		Recycler.recycle(session);
		NotesThread.stermThread();
	}
	
	public static void closeSession(Database db){
		try {
			closeSession(db.getParent());
		} catch (NotesException e) {
			logger.log(Level.SEVERE, null, e);
		}
	}
	
	public static void closeSession(Document doc){
		try {
			closeSession(doc.getParentDatabase());
		} catch (NotesException e) {
			logger.log(Level.SEVERE, null, e);
		}
	}
	
	public static void closeSession(DocumentCollection col){
		try {
			closeSession(col.getParent());
		} catch (NotesException e) {
			logger.log(Level.SEVERE, null, e);
		}
	}

	public static Session openSessionCloner(){
		Session session = null;

		try{
			
			SessionCloner sessionCloner=SessionCloner.getSessionCloner();
			NSFComponentModule module= NotesContext.getCurrent().getModule();
			NotesContext context = new NotesContext( module );
			NotesContext.initThread( context );
			session = sessionCloner.getSession();
            
		}catch(NotesException n){
			logger.log(Level.SEVERE,null,n);

		}
		return session;

	}







}
