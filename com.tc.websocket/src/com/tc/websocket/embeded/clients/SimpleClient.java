package com.tc.websocket.embeded.clients;

import com.google.inject.Inject;
import com.tc.utils.JSONUtils;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.SocketMessage;

public class SimpleClient {
	
	@Inject
	private IDominoWebSocketServer server;
	
	private JavaScript script;
	
	
	public SimpleClient(JavaScript script){
		this.script = script;
	}
	
	public JavaScript getScript() {
		return script;
	}

	public void setScript(JavaScript script) {
		this.script = script;
	}
	


	public SocketMessage createMessage() {
		SocketMessage msg = new SocketMessage();
		msg.setFrom(script.getSource());
		return msg;
	}
	
	public void send(SocketMessage socketMessage){
		socketMessage.setFrom(script.getSource());
		server.onMessage(socketMessage.getTo(),JSONUtils.toJson(socketMessage));
	}
	
	public void send(String to, String text){
		SocketMessage msg = this.createMessage();
		this.send(msg.to(to).text(text));
	}
	
	public void send(String json){
		SocketMessage msg = JSONUtils.toObject(json, SocketMessage.class);
		this.send(msg);
	}

}
