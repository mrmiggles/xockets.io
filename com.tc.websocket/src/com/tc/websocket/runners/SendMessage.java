package com.tc.websocket.runners;

import com.google.inject.Inject;
import com.tc.utils.JSONUtils;
import com.tc.websocket.Const;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.SocketMessage;

public class SendMessage implements Runnable {
	
	@Inject
	IDominoWebSocketServer server;
	
	private SocketMessage msg;
	
	public SendMessage(SocketMessage msg){
		this.msg = msg;
	}
	

	@Override
	public void run() {
		if(Const.BROADCAST.equalsIgnoreCase(msg.getTo())){
			this.server.broadcast(msg);
		}else{
			this.server.send(msg.getTo(), JSONUtils.toJson(msg));
		}
		
	}

}
