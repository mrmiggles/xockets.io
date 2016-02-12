package com.tc.websocket.guice;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.server.RequestValidator;
import com.tc.websocket.server.WebSocketServerInitializer;

public class NettyModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(WebSocketServerInitializer.class).in(Singleton.class);
		bind(RequestValidator.class).in(Singleton.class);
	}
	
	
	@Provides
	@Named(Const.GUICE_EVENTLOOP_BOSS)
	@Singleton
	public EventLoopGroup provideBoss(){
		EventLoopGroup loopy = null;
		if(Config.getInstance().isNativeTransport()){
			loopy = new EpollEventLoopGroup(1);
		}else{
			loopy = new NioEventLoopGroup(1);
		}
		return loopy;
	}
	
	@Provides
	@Named(Const.GUICE_EVENTLOOP_WORKER)
	@Singleton
	public EventLoopGroup provideWorker(){
		EventLoopGroup loopy = null;
		if(Config.getInstance().isNativeTransport()){
			loopy = new EpollEventLoopGroup(Config.getInstance().getEventLoopThreads());
		}else{
			loopy = new NioEventLoopGroup(Config.getInstance().getEventLoopThreads());
		}
		return loopy;
	}

}
