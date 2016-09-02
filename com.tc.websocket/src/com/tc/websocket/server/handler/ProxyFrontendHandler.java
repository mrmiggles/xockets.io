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

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.StringCache;
import com.tc.websocket.Const;
import com.tc.websocket.server.pipeline.IPipelineBuilder;

public class ProxyFrontendHandler extends ChannelInboundHandlerAdapter {

	private static final Logger LOG = Logger.getLogger(ProxyFrontendHandler.class.getName());	

	private final String remoteHost;
	private final int remotePort;

	private volatile Channel outboundChannel;

	private ProxyBackendHandler handler;
	
	@Inject
	@Named(Const.GUICE_WEBSOCKET_PIPELINE)
	private IPipelineBuilder builder;

	@Inject
	IGuicer guicer;

	public ProxyFrontendHandler(String remoteHost, int remotePort) {
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		final Channel inboundChannel = ctx.channel();

		this.handler = new ProxyBackendHandler(inboundChannel);

		// Start the connection attempt.
		Bootstrap b = new Bootstrap();
		b.group(inboundChannel.eventLoop())
		.channel(ctx.channel().getClass())
		.handler(this.handler)
		.option(ChannelOption.AUTO_READ, false);

		ChannelFuture f = b.connect(remoteHost, remotePort);
		outboundChannel = f.channel();

		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					// connection complete start to read first data
					inboundChannel.read();

				} else {
					// Close the connection if the connection attempt has failed.
					inboundChannel.close();
				}
			}
		});

	}


	private AtomicBoolean proxy = new AtomicBoolean(true);

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg){

		ByteBuf buf = (ByteBuf) msg;
		String data = new String(ByteBufUtil.getBytes(buf));
		
		writeToFile("frontend", ByteBufUtil.getBytes(buf));
		
		if(data.contains(Const.UPGRADE_WEBSOCKET) || data.contains(Const.GET_WEBSOCKET)){
			proxy.set(false);

		}else if(data.contains(StringCache.HTTP_1_1)){
			proxy.set(true);
		}

		if(proxy.get()==false){
			writeToFile("frontend." + ctx.channel().id(), ByteBufUtil.getBytes(buf));
		}

		if(proxy.get()){
			ctx.channel().config().setAutoRead(false);
			if (outboundChannel.isActive()) {
				outboundChannel.writeAndFlush(buf).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) {
						if (future.isSuccess()) {
							// was able to flush out data, start to read the next chunk
							ctx.channel().read();
						} else {
							future.channel().close();
						}
					}
				});
			}
		}else{
			//make sure the backend handler knows its a websocket connection.
			this.handler.setWebsocket(true);
			
			//get handle on the pipeline.
			ChannelPipeline pipeline = ctx.pipeline();
			
			//apply the websocket handlers
			builder.apply(pipeline);
			
			//remove this handler.
			pipeline.remove(this);
			
			//fire the event to move on to the next handler.
			ctx.fireChannelRead(msg);
		}

	}




	public static void writeToFile(String prefix, byte[] bytes){
		/*
		if(Config.getInstance().isDebug()){
			OutputStream out = null;
			try {
				out = new FileOutputStream("c:/temp/" + prefix + "-" + new Date().getTime() + ".txt");
				out.write(bytes);
				out.close();

			} catch (Exception e) {
				LOG.log(Level.SEVERE, null,e);
			}finally{
				IOUtils.closeQuietly(out);
			}
		}
		*/
	}


	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		if (outboundChannel != null) {
			closeOnFlush(outboundChannel);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if(cause.getMessage().startsWith(Const.ESTABLISHED_CONN_ERR)){
			LOG.log(Level.FINE, null, cause);
		}else{
			LOG.log(Level.SEVERE, null, cause);
		}
		closeOnFlush(ctx.channel());
	}

	/**
	 * Closes the specified channel after all queued write requests are flushed.
	 */
	static void closeOnFlush(Channel ch) {
		if (ch.isActive()) {
			ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}
}
