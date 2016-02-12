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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.tc.websocket.Config;
import com.tc.websocket.Const;


/**
 * Handles handshakes and messages
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

	private static final Logger logger = Logger.getLogger(WebSocketServerHandler.class.getName());

	private WebSocketServerHandshaker handshaker;

	private RequestValidator validator;

	private IDominoWebSocketServer dominoServer;


	public IDominoWebSocketServer getDominoServer() {
		return dominoServer;
	}

	@Inject
	public void setDominoServer(IDominoWebSocketServer dominoServer, RequestValidator validator) {
		this.dominoServer = dominoServer;
		this.validator = validator;
	}


	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof FullHttpRequest) {
			if(validator.isValidRequest(ctx, (FullHttpRequest) msg)){
				handleHandShake(ctx, (FullHttpRequest) msg);
			}
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
		
	}


	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		//print ("channelReadComplete");
		ctx.flush();
	}

	private void handleHandShake(ChannelHandlerContext ctx, FullHttpRequest req) {
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true, (int) Config.getInstance().getMaxSize());
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
			dominoServer.onOpen(new ContextWrapper(ctx), req);
		}

	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			dominoServer.onClose(new ContextWrapper(ctx));
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
					.getName()));
		}

		String message = ((TextWebSocketFrame) frame).text();
		dominoServer.onMessage(new ContextWrapper(ctx), message);
		
	
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

		if(Config.getInstance().isTestMode()){
			logger.log(Level.SEVERE,new ContextWrapper(ctx).getResourceDescriptor() + " closed abruptly.");
		}else{
			logger.log(Level.SEVERE,null,cause);
		}

		this.dominoServer.closeWithDelay(new ContextWrapper(ctx), 0);
		//close it just in case
		if(ctx.channel().isOpen()){
			ctx.channel().close();
		}
	}

	private static String getWebSocketLocation(FullHttpRequest req) {
		String location =  req.headers().get(HttpHeaderNames.HOST) + Const.WEBSOCKET_URI;
		if (Config.getInstance().isEncrypted()) {
			return "wss://" + location;
		} else {
			return "ws://" + location;
		}
	}


	public static void print(Object o){
		System.out.println(o);
	}

}
