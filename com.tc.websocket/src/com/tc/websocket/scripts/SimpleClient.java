package com.tc.websocket.scripts;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.JSONUtils;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.SocketMessage;

public class SimpleClient {
	
	@Inject
	private IDominoWebSocketServer server;
	
	@Inject
	private IGuicer guicer;
	
	private Script script;
	
	
	public SimpleClient(Script script){
		this.script = script;
	}
	
	
	public SocketMessage createMessage() {
		return guicer.createObject(SocketMessage.class);
	}
	
	public void sendMsg(SocketMessage msg){
		msg.setFrom(this.script.getSource());
		this.send(msg);;
	}
	
	public void send(SocketMessage socketMessage){
		socketMessage.setFrom(this.script.getSource());
		server.onMessage(socketMessage.getTo(),JSONUtils.toJson(socketMessage));
	}
	
	public void send(String to, String text){
		SocketMessage msg = this.createMessage();
		this.send(msg.to(to).text(text).from(this.script.getSource()));
	}
	
	public void send(String json){
		SocketMessage msg = JSONUtils.toObject(json, SocketMessage.class);
		this.send(msg.from(this.script.getSource()));
	}

}
