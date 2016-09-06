/*
 * 
 */
package com.tc.websocket.runners;

import java.util.Collection;

import com.google.inject.Inject;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;


// TODO: Auto-generated Javadoc
/**
 * The Class UserQueueProcessor.
 */
public class UserQueueProcessor implements Runnable{

	/** The server. */
	@Inject
	private IDominoWebSocketServer server;
	

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Collection<IUser> users = server.getUsers();
		for(IUser user : users){
			user.processQueue();
		}
	}

}
