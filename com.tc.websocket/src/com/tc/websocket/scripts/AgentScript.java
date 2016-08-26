package com.tc.websocket.scripts;

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

public class AgentScript extends Script {
	Logger logger = Logger.getLogger(AgentScript.class.getName());

	@Override
	public void run() {
		
		if(Const.ON_MESSAGE.equalsIgnoreCase(this.getFunction())){
			this.onMessage();
			
		}else if(Const.ON_OPEN.equalsIgnoreCase(this.getFunction())){
			this.onOpenOrClose(Const.ON_OPEN);
			
		}else if(Const.ON_CLOSE.equalsIgnoreCase(this.getFunction())){
			this.onOpenOrClose(Const.ON_CLOSE);
		}
		
		else if(Const.ON_ERROR.equalsIgnoreCase(this.getFunction())){
			this.onError();
		}

	}
	
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
			doc.replaceItemValue("function", Const.ON_ERROR);
			
			RichTextItem item = doc.createRichTextItem("Body");
			item.appendText(JSONUtils.toJson(user));
			doc.replaceItemValue("Form", user.getClass().getName());
			

			Agent agent = db.getAgent(StrUtils.rightBack(this.getSource(), "/"));
			agent.runWithDocumentContext(doc);
			

		}catch(NotesException n){
			logger.log(Level.SEVERE, null, n);
		}finally{
			this.closeSession(session);
		}
		
	}
	
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
			doc.replaceItemValue("function", fun);
			
			RichTextItem item = doc.createRichTextItem("Body");
			item.appendText(JSONUtils.toJson(user));
			doc.replaceItemValue("Form", user.getClass().getName());

			Agent agent = db.getAgent(StrUtils.rightBack(this.getSource(), "/"));
			agent.runWithDocumentContext(doc);
			

		}catch(NotesException n){
			logger.log(Level.SEVERE, null, n);
		}finally{
			this.closeSession(session);
		}
		
	}
	
	private void onMessage(){
		Session session = null;
		try{
			session = this.openSession();
			Database db = session.getDatabase(StringCache.EMPTY, dbPath());
			
			Document doc = db.createDocument();
			
			SocketMessage msg = (SocketMessage) this.args[0];
			
			doc.replaceItemValue("function", Const.ON_MESSAGE);
			doc.replaceItemValue("from", msg.getFrom());
			doc.replaceItemValue("to", msg.getTo());
			doc.replaceItemValue("text", msg.getText());
			doc.replaceItemValue("event", Const.ON_MESSAGE);
			doc.replaceItemValue("targets", ColUtils.toVector(msg.getTargets()));
			
			RichTextItem item = doc.createRichTextItem("Body");
			item.appendText(msg.toJson());
			doc.replaceItemValue("Form", SocketMessage.class.getName());

			Agent agent = db.getAgent(StrUtils.rightBack(this.getSource(), "/"));
			agent.runWithDocumentContext(doc);
			
		

		}catch(NotesException n){
			logger.log(Level.SEVERE, null, n);
		}finally{
			this.closeSession(session);
		}
	}
	
	
	public synchronized String extractScript() {
		return StringCache.EMPTY;
	}

	@Override
	public Script copy(Object ...args) {
		AgentScript copy  = new AgentScript();
		copy.setArgs(args);
		copy.setFunction(this.getFunction());
		copy.setScript(this.getScript());
		copy.setSource(this.getSource());
		copy.setCreds(user, password);
		return copy;
	}

	@Override
	public boolean recompile(boolean reload) {
		return true;
	}



}
