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
package com.tc.websocket.server.handler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.server.ContextWrapper;
import com.tc.websocket.server.IDominoWebSocketServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;



// TODO: Auto-generated Javadoc
/**
 * Handles handshakes and messages.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(WebSocketServerHandler.class.getName());

	/** The handshaker. */
	private WebSocketServerHandshaker handshaker;

	/** The domino server. */
	private IDominoWebSocketServer dominoServer;
	
	private StringBuilder textBuffer = new StringBuilder();

	/** The guicer. */
	@Inject
	IGuicer guicer;


	/**
	 * Gets the domino server.
	 *
	 * @return the domino server
	 */
	public IDominoWebSocketServer getDominoServer() {
		return dominoServer;
	}

	/**
	 * Sets the domino server.
	 *
	 * @param dominoServer the new domino server
	 */
	@Inject
	public void setDominoServer(IDominoWebSocketServer dominoServer) {
		this.dominoServer = dominoServer;

	}




	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof FullHttpRequest) {
			handleHandShake(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	/**
	 * Handle hand shake.
	 *
	 * @param ctx the ctx
	 * @param req the req
	 */
	private void handleHandShake(ChannelHandlerContext ctx, FullHttpRequest req) {
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true, (int) Config.getInstance().getMaxSize());
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
			dominoServer.onOpen(this.newWrapper(ctx), req);
		}
	}

	/**
	 * New wrapper.
	 *
	 * @param ctx the ctx
	 * @return the context wrapper
	 */
	private ContextWrapper newWrapper(ChannelHandlerContext ctx){
		ContextWrapper wrapper = guicer.createObject(ContextWrapper.class);
		return wrapper.init(ctx);
	}

	/**
	 * Handle web socket frame.
	 *
	 * @param ctx the ctx
	 * @param frame the frame
	 */
	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

		try{

			// Check for closing frame
			if (frame instanceof CloseWebSocketFrame) {
				dominoServer.onClose(this.newWrapper(ctx));
				handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
				return;

			}
			if (frame instanceof PingWebSocketFrame) {
				ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
				return;
			}


			if(frame instanceof PongWebSocketFrame){
				return;//do nothing.
				
			}


			if(frame instanceof TextWebSocketFrame){
				String message = ((TextWebSocketFrame) frame).text();
				textBuffer.append(message);
			}else if(frame instanceof ContinuationWebSocketFrame){
				textBuffer.append(((ContinuationWebSocketFrame) frame).text());
			}
			
	
			if(frame.isFinalFragment()){
				dominoServer.onMessage(this.newWrapper(ctx), textBuffer.toString());
				textBuffer = new StringBuilder();
			}
			

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void writeToTempFile(String fragment, boolean isFinal) throws IOException{
		File file = File.createTempFile((isFinal ? "final" : "frag"),".json");
		FileUtils.write(file,fragment);
	}


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

		if(Config.getInstance().isTestMode()){
			LOG.log(Level.SEVERE,this.newWrapper(ctx).getResourceDescriptor() + " closed abruptly.");
		}else{
			LOG.log(Level.SEVERE,null,cause);
		}

		this.dominoServer.closeWithDelay(this.newWrapper(ctx), 0);
		//close it just in case
		if(ctx.channel().isOpen()){
			ctx.channel().close();
		}
	}

	/**
	 * Gets the web socket location.
	 *
	 * @param req the req
	 * @return the web socket location
	 */
	private static String getWebSocketLocation(FullHttpRequest req) {
		String location =  req.headers().get(HttpHeaderNames.HOST) + Const.WEBSOCKET_URI;
		if (Config.getInstance().isEncrypted()) {
			return "wss://" + location;
		} else {
			return "ws://" + location;
		}
	}


	/**
	 * Prints the.
	 *
	 * @param o the o
	 */
	public static void print(Object o){
		System.out.println(o);
	}

}
