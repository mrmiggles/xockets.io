/*
 * © Copyright Tek Counsel LLC 2016
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package com.tc.websocket.server;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.tc.xpage.profiler.Stopwatch;


// TODO: Auto-generated Javadoc
/**
 * The Class ContextWrapper.
 */
public class ContextWrapper implements ChannelHandlerContext {
	
	/** The Constant RESOURCE_DESC. */
	public static final String RESOURCE_DESC="resourceDescriptor";

	/** The ctx. */
	private ChannelHandlerContext ctx;
	
	/** The messages. */
	private Queue<String> messages = new ConcurrentLinkedQueue<String>(); //used in case user is not write-able.
	
	/**
	 * Instantiates a new context wrapper.
	 */
	public ContextWrapper(){
		
	}
	

	/**
	 * Inits the.
	 *
	 * @param ctx the ctx
	 * @return the context wrapper
	 */
	public ContextWrapper init(ChannelHandlerContext ctx){
		this.ctx = ctx;
		return this;
	}


	/**
	 * Sets the resource descriptor.
	 *
	 * @param resourceDescriptor the new resource descriptor
	 */
	public void setResourceDescriptor(String resourceDescriptor){
		Attribute<Object> attr = this.ctx.channel().attr(AttributeKey.valueOf(RESOURCE_DESC));
		attr.set(resourceDescriptor);
	}

	/**
	 * Gets the resource descriptor.
	 *
	 * @return the resource descriptor
	 */
	public String getResourceDescriptor(){
		Attribute<Object> attr = this.ctx.channel().attr(AttributeKey.valueOf(RESOURCE_DESC));
		return (String) attr.get();
	}


	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#attr(io.netty.util.AttributeKey)
	 */
	@Override
	public <T> Attribute<T> attr(AttributeKey<T> attKey) {
		return ctx.channel().attr(attKey);
	}


	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#hasAttr(io.netty.util.AttributeKey)
	 */
	@Override
	public <T> boolean hasAttr(AttributeKey<T> attKey) {
		return ctx.channel().hasAttr(attKey);
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#alloc()
	 */
	@Override
	public ByteBufAllocator alloc() {
		return ctx.alloc();
	}


	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#bind(java.net.SocketAddress)
	 */
	@Override
	public ChannelFuture bind(SocketAddress socketAddr) {
		return ctx.bind(socketAddr);
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#bind(java.net.SocketAddress, io.netty.channel.ChannelPromise)
	 */
	@Override
	public ChannelFuture bind(SocketAddress socketAddr, ChannelPromise cPromise) {
		return ctx.bind(socketAddr,cPromise);
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#channel()
	 */
	@Override
	public Channel channel() {
		return ctx.channel();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#close()
	 */
	@Override
	public ChannelFuture close() {
		return ctx.close();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#close(io.netty.channel.ChannelPromise)
	 */
	@Override
	public ChannelFuture close(ChannelPromise promise) {
		return ctx.close(promise);
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#connect(java.net.SocketAddress)
	 */
	@Override
	public ChannelFuture connect(SocketAddress socketAddr) {
		return ctx.connect(socketAddr);
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#connect(java.net.SocketAddress, java.net.SocketAddress)
	 */
	@Override
	public ChannelFuture connect(SocketAddress addr1, SocketAddress addr2) {
		return ctx.connect(addr1,addr2);
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#connect(java.net.SocketAddress, io.netty.channel.ChannelPromise)
	 */
	@Override
	public ChannelFuture connect(SocketAddress addr, ChannelPromise promise) {
		return ctx.connect(addr,promise);
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#connect(java.net.SocketAddress, java.net.SocketAddress, io.netty.channel.ChannelPromise)
	 */
	@Override
	public ChannelFuture connect(SocketAddress addr1, SocketAddress addr2,ChannelPromise promise) {
		return ctx.connect(addr1,addr2,promise);
	}


	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#deregister()
	 */
	@Override
	public ChannelFuture deregister() {
		return ctx.deregister();
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#deregister(io.netty.channel.ChannelPromise)
	 */
	@Override
	public ChannelFuture deregister(ChannelPromise promise) {
		return ctx.deregister(promise);
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#disconnect()
	 */
	@Override
	public ChannelFuture disconnect() {
		return ctx.disconnect();
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#disconnect(io.netty.channel.ChannelPromise)
	 */
	@Override
	public ChannelFuture disconnect(ChannelPromise promise) {
		return ctx.disconnect(promise);
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#executor()
	 */
	@Override
	public EventExecutor executor() {
		return ctx.executor();
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#fireChannelActive()
	 */
	@Override
	public ChannelHandlerContext fireChannelActive() {
		return ctx.fireChannelActive();
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#fireChannelInactive()
	 */
	@Override
	public ChannelHandlerContext fireChannelInactive() {
		return ctx.fireChannelInactive();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#fireChannelRead(java.lang.Object)
	 */
	@Override
	public ChannelHandlerContext fireChannelRead(Object o) {
		return ctx.fireChannelRead(o);
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#fireChannelReadComplete()
	 */
	@Override
	public ChannelHandlerContext fireChannelReadComplete() {
		return ctx.fireChannelReadComplete();
	}





	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#fireChannelRegistered()
	 */
	@Override
	public ChannelHandlerContext fireChannelRegistered() {
		return ctx.fireChannelRegistered();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#fireChannelUnregistered()
	 */
	@Override
	public ChannelHandlerContext fireChannelUnregistered() {
		return ctx.fireChannelUnregistered();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#fireChannelWritabilityChanged()
	 */
	@Override
	public ChannelHandlerContext fireChannelWritabilityChanged() {
		return ctx.fireChannelWritabilityChanged();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#fireExceptionCaught(java.lang.Throwable)
	 */
	@Override
	public ChannelHandlerContext fireExceptionCaught(Throwable throwable) {
		return ctx.fireExceptionCaught(throwable);
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#fireUserEventTriggered(java.lang.Object)
	 */
	@Override
	public ChannelHandlerContext fireUserEventTriggered(Object o) {
		return ctx.fireUserEventTriggered(o);
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#flush()
	 */
	@Override
	public ChannelHandlerContext flush() {
		return ctx.flush();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#handler()
	 */
	@Override
	public ChannelHandler handler() {
		return ctx.handler();
	}


	
	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#isRemoved()
	 */
	@Override
	public boolean isRemoved() {
		return ctx.isRemoved();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#name()
	 */
	@Override
	public String name() {
		return ctx.name();
	}





	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#newFailedFuture(java.lang.Throwable)
	 */
	@Override
	public ChannelFuture newFailedFuture(Throwable throwable) {
		return ctx.newFailedFuture(throwable);
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#newProgressivePromise()
	 */
	@Override
	public ChannelProgressivePromise newProgressivePromise() {
		return ctx.newProgressivePromise();
	}


	



	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#newPromise()
	 */
	@Override
	public ChannelPromise newPromise() {
		return ctx.newPromise();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#newSucceededFuture()
	 */
	@Override
	public ChannelFuture newSucceededFuture() {
		return ctx.newSucceededFuture();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#pipeline()
	 */
	@Override
	public ChannelPipeline pipeline() {
		return ctx.pipeline();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerContext#read()
	 */
	@Override
	public ChannelHandlerContext read() {
		return ctx.read();
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#voidPromise()
	 */
	@Override
	public ChannelPromise voidPromise() {
		return ctx.voidPromise();
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#write(java.lang.Object)
	 */
	@Override
	public ChannelFuture write(Object o) {
		return ctx.write(o);
	}


	


	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#write(java.lang.Object, io.netty.channel.ChannelPromise)
	 */
	@Override
	public ChannelFuture write(Object o, ChannelPromise promise) {
		return ctx.write(o, promise);
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#writeAndFlush(java.lang.Object)
	 */
	@Override
	public ChannelFuture writeAndFlush(Object o) {
		return ctx.writeAndFlush(o);
	}


	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelOutboundInvoker#writeAndFlush(java.lang.Object, io.netty.channel.ChannelPromise)
	 */
	@Override
	public ChannelFuture writeAndFlush(Object o, ChannelPromise promise) {
		return ctx.writeAndFlush(o,promise);
	}

	

	/**
	 * Checks if is closed.
	 *
	 * @return true, if is closed
	 */
	public boolean isClosed(){
		return !this.isOpen();
	}

	
	
	/**
	 * Checks if is open.
	 *
	 * @return true, if is open
	 */
	public boolean isOpen(){
		 return this.ctx!=null && this.ctx.channel()!=null && this.ctx.channel().isOpen();
	}

	


	/**
	 * Send.
	 *
	 * @param message the message
	 */
	public void send(final String message){
		this.messages.add(message);
		this.processQueue();
	}
	
	
	/**
	 * Send and flush.
	 *
	 * @param message the message
	 */
	public void sendAndFlush(final String message){
		this.messages.add(message);
		this.processQueue();
	}


	


	/*
	@Override
	public ChannelHandlerInvoker invoker() {
		return ctx.invoker();
	}
	*/
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return this.ctx.hashCode();
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.ctx.toString() + "." + this.getResourceDescriptor();
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		boolean b= false;
		
		if(o instanceof ContextWrapper){
			ContextWrapper wrapper = (ContextWrapper)o;
			b = wrapper.ctx.equals(this.ctx);
		}
		
		return b;
	}
	
	/**
	 * Process queue.
	 */
	@Stopwatch(time=50)
	public void processQueue() {
		boolean written = false;
		while(!this.messages.isEmpty() && ctx.channel().isWritable()){
			String msg = messages.poll();
			if(msg!=null){
				written = true;
				this.ctx.write(new TextWebSocketFrame(msg), ctx.channel().voidPromise());
			}
		}
		if(written) ctx.flush();
	}

}
