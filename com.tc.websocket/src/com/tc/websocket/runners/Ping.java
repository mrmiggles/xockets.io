/*
 * 
 */
package com.tc.websocket.runners;


// TODO: Auto-generated Javadoc
/**
 * The Class Ping.
 */
public class Ping extends AbstractQueueProcessor implements Runnable{
	



	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		super.server.pingUsers();
	}
	

}
