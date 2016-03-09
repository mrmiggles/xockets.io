package com.tc.websocket.guice;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.ISSLFactory;
import com.tc.websocket.SSLFactory;
import com.tc.websocket.server.WebSocketServerInitializer;
import com.tc.websocket.server.handler.WebSocketValidationHandler;
import com.tc.websocket.server.pipeline.IPipelineBuilder;
import com.tc.websocket.server.pipeline.RedirectPipelineBuilder;
import com.tc.websocket.server.pipeline.WebSocketPipelineBuilder;

public class NettyModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(WebSocketServerInitializer.class).in(Singleton.class);
		bind(WebSocketValidationHandler.class).in(Singleton.class);
		bind(IPipelineBuilder.class).annotatedWith(Names.named(Const.GUICE_WEBSOCKET_PIPELINE)).to(WebSocketPipelineBuilder.class);
		bind(IPipelineBuilder.class).annotatedWith(Names.named(Const.GUICE_REDIRECT_PIPELINE)).to(RedirectPipelineBuilder.class);
		bind(ISSLFactory.class).to(SSLFactory.class).in(Singleton.class);
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
