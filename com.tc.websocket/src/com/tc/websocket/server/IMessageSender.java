package com.tc.websocket.server;
import com.tc.websocket.valueobjects.SocketMessage;

public interface IMessageSender {
	
	public void sendMessage(SocketMessage msg);
	
	public void sendMessage(String to, String text);
	
	public void sendMessage(String json);
	
	public void sendMessageWithDelay(SocketMessage msg, int seconds);
	

}
