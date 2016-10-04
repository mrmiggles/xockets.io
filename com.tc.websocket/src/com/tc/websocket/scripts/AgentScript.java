/*
 * 
 */
package com.tc.websocket.scripts;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Agent;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;

import com.tc.utils.ColUtils;
import com.tc.utils.JSONUtils;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;


// TODO: Auto-generated Javadoc
/**
 * The Class AgentScript.
 */
public class AgentScript extends Script {
	
	/** The log. */
	Logger LOG = Logger.getLogger(AgentScript.class.getName());
	
	/** The Constant FUNCTION. */
	private static final String FUNCTION="function";
	
	/** The Constant EVENT. */
	private static final String EVENT="event";


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		if(!this.shouldRun()){return;}


		if(Const.ON_MESSAGE.equalsIgnoreCase(this.getFunction())){
			this.onMessage();

		}else if(Const.ON_OPEN.equalsIgnoreCase(this.getFunction())){
			this.onOpenOrClose(Const.ON_OPEN);

		}else if(Const.ON_CLOSE.equalsIgnoreCase(this.getFunction())){
			this.onOpenOrClose(Const.ON_CLOSE);
		}

		else if(Const.ON_ERROR.equalsIgnoreCase(this.getFunction())){
			this.onError();

		}else if(Const.ON_INTERVAL.equalsIgnoreCase(this.getFunction())){
			this.onInterval();
		}


		this.setLastRun(new Date());

	}

	/**
	 * On error.
	 */
	private void onError(){
		Session session = null;
		try{
			session = this.openSession();
			Database db = session.getDatabase(StringCache.EMPTY, dbPath());

			Document doc = db.createDocument();

			IUser user = (IUser) this.args[0];

			Exception e = (Exception) args[0];

			e.printStackTrace();

			doc.replaceItemValue("error", e.getMessage() );
			doc.replaceItemValue(FUNCTION, Const.ON_ERROR);

			RichTextItem item = doc.createRichTextItem("Body");
			item.appendText(JSONUtils.toJson(user));
			doc.replaceItemValue("Form", user.getClass().getName());


			Agent agent = db.getAgent(StrUtils.rightBack(this.getSource(), "/"));
			agent.runWithDocumentContext(doc);


		}catch(NotesException n){
			
			LOG.log(Level.SEVERE, null, n);
		}finally{
			this.closeSession(session);
		}

	}

	/**
	 * On open or close.
	 *
	 * @param fun the fun
	 */
	private void onOpenOrClose(String fun){
		Session session = null;
		try{
			session = this.openSession();
			Database db = session.getDatabase(StringCache.EMPTY, dbPath());

			Document doc = db.createDocument();

			IUser user = (IUser) this.args[0];

			doc.replaceItemValue("userId",user.getUserId());
			doc.replaceItemValue("sessionId",user.getSessionId());
			doc.replaceItemValue("host",user.getHost());
			doc.replaceItemValue("status",user.getStatus());
			doc.replaceItemValue("uris",ColUtils.toVector(user.getUris()));
			doc.replaceItemValue(FUNCTION, fun);

			RichTextItem item = doc.createRichTextItem("Body");
			item.appendText(JSONUtils.toJson(user));
			doc.replaceItemValue("Form", user.getClass().getName());

			Agent agent = db.getAgent(StrUtils.rightBack(this.getSource(), "/"));
			agent.runWithDocumentContext(doc);


		}catch(NotesException n){
			
			LOG.log(Level.SEVERE, null, n);
		}finally{
			this.closeSession(session);
		}

	}

	/**
	 * On message.
	 */
	private void onMessage(){
		Session session = null;
		try{
			session = this.openSession();
			Database db = session.getDatabase(StringCache.EMPTY, dbPath());

			Document doc = db.createDocument();

			SocketMessage msg = (SocketMessage) this.args[0];

			doc.replaceItemValue(FUNCTION, Const.ON_MESSAGE);
			doc.replaceItemValue("from", msg.getFrom());
			doc.replaceItemValue("to", msg.getTo());
			doc.replaceItemValue("text", msg.getText());
			doc.replaceItemValue(EVENT, Const.ON_MESSAGE);
			doc.replaceItemValue("targets", ColUtils.toVector(msg.getTargets()));

			RichTextItem item = doc.createRichTextItem("Body");
			item.appendText(msg.toJson());
			doc.replaceItemValue("Form", SocketMessage.class.getName());

			Agent agent = db.getAgent(StrUtils.rightBack(this.getSource(), "/"));
			agent.runWithDocumentContext(doc);



		}catch(NotesException n){
			
			LOG.log(Level.SEVERE, null, n);
		}finally{
			this.closeSession(session);
		}
	}


	/**
	 * On interval.
	 */
	private void onInterval(){
		Session session = null;
		try{
			session = this.openSession();
			Database db = session.getDatabase(StringCache.EMPTY, dbPath());
			Document doc = db.createDocument();
			doc.replaceItemValue(FUNCTION, Const.ON_MESSAGE);
			doc.replaceItemValue(EVENT, Const.ON_INTERVAL);
			Agent agent = db.getAgent(StrUtils.rightBack(this.getSource(), "/"));
			agent.runWithDocumentContext(doc);
		}catch(NotesException n){
			
			LOG.log(Level.SEVERE, null, n);
		}finally{
			this.closeSession(session);
		}
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#extractScript()
	 */
	public synchronized String extractScript() {
		return StringCache.EMPTY;
	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#copy(java.lang.Object[])
	 */
	@Override
	public Script copy(Object ...args) {
		AgentScript copy  = new AgentScript();
		copy.setArgs(args);
		copy.setFunction(this.getFunction());
		copy.setScript(this.getScript());
		copy.setSource(this.getSource());
		copy.setLastRun(this.getLastRun());
		copy.setCreds(user, password);
		return copy;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#recompile(boolean)
	 */
	@Override
	public boolean recompile(boolean reload) {
		return true;
	}



}
