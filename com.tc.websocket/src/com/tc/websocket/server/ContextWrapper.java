package com.tc.websocket.server;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;

import java.net.SocketAddress;

public class ContextWrapper implements ChannelHandlerContext {
	public static final String RESOURCE_DESC="resourceDescriptor";

	private ChannelHandlerContext ctx;

	public ContextWrapper(ChannelHandlerContext ctx){
		this.ctx = ctx;
	}


	public void setResourceDescriptor(String resourceDescriptor){
		Attribute<Object> attr = this.ctx.attr(AttributeKey.valueOf(RESOURCE_DESC));
		attr.set(resourceDescriptor);
	}

	public String getResourceDescriptor(){
		Attribute<Object> attr = this.ctx.attr(AttributeKey.valueOf(RESOURCE_DESC));
		return (String) attr.get();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#attr(io.netty.util.AttributeKey)
	 */

	@Override
	public <T> Attribute<T> attr(AttributeKey<T> attKey) {
		return ctx.attr(attKey);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#hasAttr(io.netty.util.AttributeKey)
	 */

	@Override
	public <T> boolean hasAttr(AttributeKey<T> attKey) {
		return ctx.hasAttr(attKey);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#alloc()
	 */


	@Override
	public ByteBufAllocator alloc() {
		return ctx.alloc();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#bind(java.net.SocketAddress)
	 */


	@Override
	public ChannelFuture bind(SocketAddress socketAddr) {
		return ctx.bind(socketAddr);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#bind(java.net.SocketAddress, io.netty.channel.ChannelPromise)
	 */


	@Override
	public ChannelFuture bind(SocketAddress socketAddr, ChannelPromise cPromise) {
		return ctx.bind(socketAddr,cPromise);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#channel()
	 */


	@Override
	public Channel channel() {
		return ctx.channel();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#close()
	 */


	@Override
	public ChannelFuture close() {
		return ctx.close();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#close(io.netty.channel.ChannelPromise)
	 */


	@Override
	public ChannelFuture close(ChannelPromise promise) {
		return ctx.close(promise);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#connect(java.net.SocketAddress)
	 */


	@Override
	public ChannelFuture connect(SocketAddress socketAddr) {
		return ctx.connect(socketAddr);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#connect(java.net.SocketAddress, java.net.SocketAddress)
	 */


	@Override
	public ChannelFuture connect(SocketAddress addr1, SocketAddress addr2) {
		return ctx.connect(addr1,addr2);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#connect(java.net.SocketAddress, io.netty.channel.ChannelPromise)
	 */


	@Override
	public ChannelFuture connect(SocketAddress addr, ChannelPromise promise) {
		return ctx.connect(addr,promise);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#connect(java.net.SocketAddress, java.net.SocketAddress, io.netty.channel.ChannelPromise)
	 */


	@Override
	public ChannelFuture connect(SocketAddress addr1, SocketAddress addr2,ChannelPromise promise) {
		return ctx.connect(addr1,addr2,promise);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#deregister()
	 */


	@Override
	public ChannelFuture deregister() {
		return ctx.deregister();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#deregister(io.netty.channel.ChannelPromise)
	 */


	@Override
	public ChannelFuture deregister(ChannelPromise promise) {
		return ctx.deregister(promise);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#disconnect()
	 */


	@Override
	public ChannelFuture disconnect() {
		return ctx.disconnect();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#disconnect(io.netty.channel.ChannelPromise)
	 */


	@Override
	public ChannelFuture disconnect(ChannelPromise promise) {
		return ctx.disconnect(promise);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#executor()
	 */


	@Override
	public EventExecutor executor() {
		return ctx.executor();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#fireChannelActive()
	 */


	@Override
	public ChannelHandlerContext fireChannelActive() {
		return ctx.fireChannelActive();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#fireChannelInactive()
	 */


	@Override
	public ChannelHandlerContext fireChannelInactive() {
		return ctx.fireChannelInactive();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#fireChannelRead(java.lang.Object)
	 */


	@Override
	public ChannelHandlerContext fireChannelRead(Object o) {
		return ctx.fireChannelRead(o);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#fireChannelReadComplete()
	 */


	@Override
	public ChannelHandlerContext fireChannelReadComplete() {
		return ctx.fireChannelReadComplete();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#fireChannelRegistered()
	 */


	@Override
	public ChannelHandlerContext fireChannelRegistered() {
		return ctx.fireChannelRegistered();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#fireChannelUnregistered()
	 */


	@Override
	public ChannelHandlerContext fireChannelUnregistered() {
		return ctx.fireChannelUnregistered();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#fireChannelWritabilityChanged()
	 */


	@Override
	public ChannelHandlerContext fireChannelWritabilityChanged() {
		return ctx.fireChannelWritabilityChanged();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#fireExceptionCaught(java.lang.Throwable)
	 */


	@Override
	public ChannelHandlerContext fireExceptionCaught(Throwable throwable) {
		return ctx.fireExceptionCaught(throwable);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#fireUserEventTriggered(java.lang.Object)
	 */


	@Override
	public ChannelHandlerContext fireUserEventTriggered(Object o) {
		return ctx.fireUserEventTriggered(o);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#flush()
	 */


	@Override
	public ChannelHandlerContext flush() {
		return ctx.flush();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#handler()
	 */


	@Override
	public ChannelHandler handler() {
		return ctx.handler();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#isRemoved()
	 */


	@Override
	public boolean isRemoved() {
		return ctx.isRemoved();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#name()
	 */


	@Override
	public String name() {
		return ctx.name();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#newFailedFuture(java.lang.Throwable)
	 */


	@Override
	public ChannelFuture newFailedFuture(Throwable throwable) {
		return ctx.newFailedFuture(throwable);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#newProgressivePromise()
	 */


	@Override
	public ChannelProgressivePromise newProgressivePromise() {
		return ctx.newProgressivePromise();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#newPromise()
	 */


	@Override
	public ChannelPromise newPromise() {
		return ctx.newPromise();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#newSucceededFuture()
	 */


	@Override
	public ChannelFuture newSucceededFuture() {
		return ctx.newSucceededFuture();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#pipeline()
	 */


	@Override
	public ChannelPipeline pipeline() {
		return ctx.pipeline();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#read()
	 */


	@Override
	public ChannelHandlerContext read() {
		return ctx.read();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#voidPromise()
	 */


	@Override
	public ChannelPromise voidPromise() {
		return ctx.voidPromise();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#write(java.lang.Object)
	 */


	@Override
	public ChannelFuture write(Object o) {
		return ctx.write(o);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#write(java.lang.Object, io.netty.channel.ChannelPromise)
	 */


	@Override
	public ChannelFuture write(Object o, ChannelPromise promise) {
		return ctx.write(o, promise);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#writeAndFlush(java.lang.Object)
	 */


	@Override
	public ChannelFuture writeAndFlush(Object o) {
		return ctx.writeAndFlush(o);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#writeAndFlush(java.lang.Object, io.netty.channel.ChannelPromise)
	 */


	@Override
	public ChannelFuture writeAndFlush(Object o, ChannelPromise promise) {
		return ctx.writeAndFlush(o,promise);
	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#isClosed()
	 */

	public boolean isClosed(){
		return this.ctx.channel().isOpen() == false;
	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#isOpen()
	 */

	public boolean isOpen(){
		return this.ctx.channel().isOpen();
	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#send(java.lang.String)
	 */

	public void send(final String message){
		this.ctx.writeAndFlush(new TextWebSocketFrame(message), ctx.channel().voidPromise());
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IChannelHandlerContext#invoker()
	 */


	@Override
	public ChannelHandlerInvoker invoker() {
		return ctx.invoker();
	}

}
