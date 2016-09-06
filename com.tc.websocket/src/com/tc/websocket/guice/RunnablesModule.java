/*
 * 
 */
package com.tc.websocket.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.tc.websocket.runners.BroadcastQueueProcessor;
import com.tc.websocket.runners.ClustermateMonitor;
import com.tc.websocket.runners.EventQueueProcessor;
import com.tc.websocket.runners.QueueMessage;
import com.tc.websocket.runners.QueueProcessor;
import com.tc.websocket.runners.StampAllUsers;
import com.tc.websocket.runners.UserCleanup;
import com.tc.websocket.runners.UserMonitor;


// TODO: Auto-generated Javadoc
/**
 * The Class RunnablesModule.
 */
public class RunnablesModule extends AbstractModule {


	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		//queue/monitors.
		bind(BroadcastQueueProcessor.class).in(Singleton.class);
		bind(ClustermateMonitor.class).in(Singleton.class);
		bind(QueueProcessor.class).in(Singleton.class);
		bind(UserMonitor.class).in(Singleton.class);
		bind(UserCleanup.class).in(Singleton.class);

		//other runnables
		bind(EventQueueProcessor.class);
		bind(QueueMessage.class);
		bind(StampAllUsers.class);
	}

}
