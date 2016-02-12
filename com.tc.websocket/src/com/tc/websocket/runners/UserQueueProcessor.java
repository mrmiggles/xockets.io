package com.tc.websocket.runners;

import java.util.Collection;

import com.google.inject.Inject;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;

public class UserQueueProcessor implements Runnable{

	@Inject
	private IDominoWebSocketServer server;
	
	@Override
	public void run() {
		Collection<IUser> users = server.getUsers();
		for(IUser user : users){
			user.processQueue();
		}
	}

}
