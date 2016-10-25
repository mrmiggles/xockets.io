/*
 * 
 */
package com.tc.websocket.valueobjects;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;


// TODO: Auto-generated Javadoc
/**
 * The Class SocketMessageLite.
 */
public class SocketMessageLite extends SocketMessage {
	
	
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.SocketMessage#getText()
	 */
	@Override
	public String getText() {
		return text;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.SocketMessage#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		this.text = text;
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.SocketMessage#getData()
	 */
	@Override
	@JsonIgnore
	public Map<String, Object> getData() {
		return data;
	}
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.SocketMessage#setData(java.util.Map)
	 */
	@Override
	@JsonIgnore
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}
