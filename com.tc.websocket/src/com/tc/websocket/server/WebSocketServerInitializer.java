/*
 * Original Work: Copyright 2012 The Netty Project
 * 
 * Modified Work: Copyright 2016 Tek Counsel LLC
 *
 * Both licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.tc.websocket.server;

import javax.net.ssl.SSLEngine;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tc.di.guicer.IGuicer;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.IConfig;
import com.tc.websocket.ISSLFactory;
import com.tc.websocket.server.handler.ProxyFrontendHandler;
import com.tc.websocket.server.pipeline.IPipelineBuilder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;


// TODO: Auto-generated Javadoc
/**
 * The Class WebSocketServerInitializer.
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

	/** The guicer. */
	@Inject
	private IGuicer guicer;
	
	/** The factory. */
	@Inject
	ISSLFactory factory;
	
	/** The websocket builder. */
	@Inject
	@Named(Const.GUICE_WEBSOCKET_PIPELINE)
	private IPipelineBuilder websocketBuilder;
	
	/** The redirect builder. */
	@Inject
	@Named(Const.GUICE_REDIRECT_PIPELINE)
	private IPipelineBuilder redirectBuilder;	

	/**
	 * Instantiates a new web socket server initializer.
	 */
	public WebSocketServerInitializer() {

	}


	
	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
	 */
	@Override
	public void initChannel(SocketChannel ch) throws Exception {

		IConfig cfg = Config.getInstance();
		
		//if we need to check for ByteBuf leaks.
		if(cfg.isLeakDetector()){
			ResourceLeakDetector.setLevel(Level.ADVANCED);
		}
		
		
		ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(1024));

		ChannelPipeline pipeline = ch.pipeline();

		int incomingPort = ch.localAddress().getPort();
		
		
		//if users are coming in on a different port than the proxy port, we need to redirect them.
		if(cfg.isProxy() && cfg.getPort() != incomingPort){
			redirectBuilder.apply(pipeline);
	        return;
		}
		
		
		if (cfg.isEncrypted()) {
			SslContext sslContext = factory.createSslContext(Config.getInstance());
			SSLEngine engine = sslContext.newEngine(ch.alloc());
			engine.setUseClientMode(false);
			engine.setNeedClientAuth(cfg.isCertAuth());
			ch.pipeline().addFirst("ssl",new SslHandler(engine));
		}

		if(cfg.isProxy()){
			pipeline.channel().config().setAutoRead(false);
			pipeline.addLast(guicer.inject(new ProxyFrontendHandler(cfg.getProxyBackendHost(),cfg.getProxyBackendPort())));
			
		}else{
			websocketBuilder.apply(pipeline);
		}

	}

}
