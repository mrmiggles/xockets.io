package com.tc.websocket.queue;

import java.util.Collection;

import com.google.inject.Inject;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;

public class ProcessUserQueues implements Runnable{

	@Inject
	private IDominoWebSocketServer server;
	
	@Override
	public void run() {
		Collection<IUser> users = server.getUsersOnThisServer();
		for(IUser user : users){
			user.processQueue();
		}
	}

}
