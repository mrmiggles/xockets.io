package com.tc.websocket.embeded.clients;

import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;

public interface IWebSocketClient {
	
	public void connect() throws InterruptedException;
	
	public void disconnect();
	
	public abstract void onOpen(WebSocketClientHandshaker handShaker);

	public abstract void onMessage(String message);

	public abstract void onClose();
	
	public abstract void onError(Throwable ex);
	
	public boolean isOpen();
	
	public void send(String message);

}
