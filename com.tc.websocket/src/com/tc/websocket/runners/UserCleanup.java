package com.tc.websocket.runners;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;


/*
 * cleanup connections / sessions that end abruptly
 */

public class UserCleanup implements Runnable {

	@Inject
	IDominoWebSocketServer server;

	@Inject
	IGuicer guicer;
	
	@Override
	public void run() {	
		Batch batch = new Batch();
		for(IUser user : server.getUsers()){
			
			//clear out any old connections
			user.clear();
			
			//now check to see if the user needs to get dropped.
			if(!user.isOpen() && !user.isGoingOffline() && user.isOnServer()){
				user.setGoingOffline(true);
				batch.addRunner(guicer.inject(new ApplyStatus(user)));
				server.decrementCount();
			}
		}
		//now execute all the status updates together.
		TaskRunner.getInstance().add(batch);

	}

}
