package com.tc.websocket.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.tc.websocket.queue.BroadcastQueueProcessor;
import com.tc.websocket.queue.ClustermateMonitor;
import com.tc.websocket.queue.EventQueueProcessor;
import com.tc.websocket.queue.QueueMessage;
import com.tc.websocket.queue.QueueProcessor;
import com.tc.websocket.queue.StampAllUsers;
import com.tc.websocket.queue.UserCleanup;
import com.tc.websocket.queue.UserMonitor;

public class RunnablesModule extends AbstractModule {

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
