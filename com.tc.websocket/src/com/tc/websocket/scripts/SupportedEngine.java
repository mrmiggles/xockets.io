package com.tc.websocket.scripts;

import com.tc.utils.StringCache;

public enum SupportedEngine {
	
   PYTHON("py","python"),
   RUBY("rb","ruby"),
   GROOVY("gvy","groovy"),
   JAVASCRIPT("js","js"),
   SSJS("ssjs","js"),
   AGENT("agent","agent");
	
	
	
	private String ext;
	private String engine;
	
	public String ext(){return ext;}
	public String engine(){return engine;}
	
	SupportedEngine(String ext, String engine){
		this.ext = ext;
		this.engine = engine;
	}
	
	
	public static String findEngine(String resource){
		String ext = resource.substring(resource.lastIndexOf(StringCache.PERIOD) + 1, resource.length());
		String engine = null;

		for(SupportedEngine e : SupportedEngine.values()){
			if(e.ext.equalsIgnoreCase(ext)){
				engine = e.engine;
				break;
			}
		}
		return engine;
	}
}
