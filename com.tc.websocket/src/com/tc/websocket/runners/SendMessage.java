/*
 * 
 */
package com.tc.websocket.runners;

import com.google.inject.Inject;
import com.tc.utils.JSONUtils;
import com.tc.websocket.Const;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.SocketMessage;


// TODO: Auto-generated Javadoc
/**
 * The Class SendMessage.
 */
public class SendMessage implements Runnable {
	
	/** The server. */
	@Inject
	IDominoWebSocketServer server;
	
	/** The msg. */
	private SocketMessage msg;
	
	/**
	 * Instantiates a new send message.
	 *
	 * @param msg the msg
	 */
	public SendMessage(SocketMessage msg){
		this.msg = msg;
	}
	


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if(Const.BROADCAST.equalsIgnoreCase(msg.getTo())){
			this.server.broadcast(msg);
			
		}else{
			this.server.onMessage(msg.getTo(), JSONUtils.toJson(msg));
			
		}
		
	}

}
