/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.websocket.Config;
import com.tc.websocket.SSLFactory;

/**
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

	@Inject
	private IGuicer guicer;
	
	public WebSocketServerInitializer() {

	}


	
	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		if(Config.getInstance().isEncrypted()){
			SSLEngine sslEngine = new SSLFactory().createSSLContext().createSSLEngine();
			sslEngine.setUseClientMode(false);
			pipeline.addLast("ssl", new SslHandler(sslEngine));
		}

		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(65536));
		
		
		if(Config.getInstance().isCompressionEnabled()){
			pipeline.addLast(new WebSocketServerCompressionHandler());
		}
		
		pipeline.addLast(guicer.inject(new WebSocketServerHandler()));
	}
}
