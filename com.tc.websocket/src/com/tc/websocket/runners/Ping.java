package com.tc.websocket.runners;

public class Ping extends AbstractQueueProcessor implements Runnable{
	


	@Override
	public void run() {
		super.server.pingUsers();
	}
	

}
