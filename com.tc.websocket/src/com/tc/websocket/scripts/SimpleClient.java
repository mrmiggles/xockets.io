/*
 * 
 */
package com.tc.websocket.scripts;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.JSONUtils;
import com.tc.utils.StrUtils;
import com.tc.websocket.runners.SendMessage;
import com.tc.websocket.runners.TaskRunner;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.server.IMessageSender;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;


// TODO: Auto-generated Javadoc
/**
 * The Class SimpleClient.
 */
public class SimpleClient implements IMessageSender {
	
	/** The server. */
	@Inject
	private IDominoWebSocketServer server;
	
	/** The guicer. */
	@Inject
	private IGuicer guicer;
	
	/** The script. */
	private Script script;
	
	
	/**
	 * Instantiates a new simple client.
	 *
	 * @param script the script
	 */
	public SimpleClient(Script script){
		this.script = script;
	}
	
	
	/**
	 * Creates the message.
	 *
	 * @return the socket message
	 */
	public SocketMessage createMessage() {
		return guicer.createObject(SocketMessage.class);
	}
	
	
	
	/**
	 * Send.
	 *
	 * @param socketMessage the socket message
	 */
	public void sendMessage(SocketMessage msg){
		if(StrUtils.isEmpty(msg.getFrom())) msg.setFrom(this.script.getSource());
		server.onMessage(msg.getTo(),JSONUtils.toJson(msg));
	}
	
	/**
	 * Send.
	 *
	 * @param to the to
	 * @param text the text
	 */
	public void sendMessage(String to, String text){
		SocketMessage msg = this.createMessage();
		this.sendMessage(msg.to(to).text(text).from(this.script.getSource()));
	}
	
	/**
	 * Send.
	 *
	 * @param json the json
	 */
	public void sendMessage(String json){
		SocketMessage msg = JSONUtils.toObject(json, SocketMessage.class);
		this.sendMessage(msg.from(this.script.getSource()));
	}
	
	
	public IUser getUser(String userId){
		return server.resolveUser(userId);
	}


	@Override
	public void sendMessageWithDelay(SocketMessage msg, int seconds) {
		TaskRunner.getInstance().add(new SendMessage(msg), seconds);
	}

	
	
}
