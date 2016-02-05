package com.tc.websocket.valueobjects;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

public class SocketMessageLite extends SocketMessage {
	
	
	
	@Override
	@JsonIgnore
	public String getText() {
		return text;
	}

	@Override
	@JsonIgnore
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	@JsonIgnore
	public Map<String, Object> getData() {
		return data;
	}
	
	@Override
	@JsonIgnore
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}
