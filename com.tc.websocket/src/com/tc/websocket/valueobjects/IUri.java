package com.tc.websocket.valueobjects;

import java.util.List;

public interface IUri {
	
	public List<String> getUris();
	public boolean containsUri(String uri);
	public boolean startsWith(String uri);
	public String getUri();
}
