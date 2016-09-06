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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.Const;


// TODO: Auto-generated Javadoc
/**
 * The Class ProxyBackendHandler.
 */
public class ProxyBackendHandler extends ChannelInboundHandlerAdapter {
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(ProxyBackendHandler.class.getName());

	/** The inbound channel. */
	private final Channel inboundChannel;
	
	/** The websocket. */
	private boolean websocket;

	/**
	 * Instantiates a new proxy backend handler.
	 *
	 * @param inboundChannel the inbound channel
	 */
	public ProxyBackendHandler(Channel inboundChannel) {
		this.inboundChannel = inboundChannel;

	}



	/**
	 * Checks if is websocket.
	 *
	 * @return true, if is websocket
	 */
	public boolean isWebsocket() {
		return websocket;
	}



	/**
	 * Sets the websocket.
	 *
	 * @param websocket the new websocket
	 */
	public void setWebsocket(boolean websocket) {
		this.websocket = websocket;
	}



	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.read();
	}
	

	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
		ByteBuf buf = (ByteBuf) msg;
		String data = new String(ByteBufUtil.getBytes(buf));
		ByteBuf bufData = buf;
		
		
		if(Config.getInstance().isEncrypted() && data.contains(StringCache.HTTP)){
			data = data.replace(StringCache.HTTP, StringCache.HTTPS);
			bufData = Unpooled.wrappedBuffer(data.getBytes());
		}
		
		ProxyFrontendHandler.writeToFile("backend", ByteBufUtil.getBytes(bufData));
		
		
		inboundChannel.writeAndFlush(bufData).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					ctx.channel().read();
					
				} else {
					future.channel().close();
				}
			}
		});

	}

	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		if(!this.isWebsocket()){
			ProxyFrontendHandler.closeOnFlush(inboundChannel);
		}
	}

	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if(cause.getMessage().startsWith(Const.ESTABLISHED_CONN_ERR)){
			LOG.log(Level.FINE, null, cause);
		}else{
			LOG.log(Level.SEVERE, null, cause);
		}
		
		ProxyFrontendHandler.closeOnFlush(ctx.channel());
	}
}
