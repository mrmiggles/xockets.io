package com.tc.websocket.queue;

public class Ping extends AbstractQueueProcessor implements Runnable{
	


	@Override
	public void run() {
		super.server.pingUsers();
	}
	

}
