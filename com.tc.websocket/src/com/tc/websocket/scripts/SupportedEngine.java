/*
 * 
 */
package com.tc.websocket.scripts;

import com.tc.utils.StringCache;


// TODO: Auto-generated Javadoc
/**
 * The Enum SupportedEngine.
 */
public enum SupportedEngine {
	
   /** The python. */
   PYTHON("py","python"),
   
   /** The ruby. */
   RUBY("rb","ruby"),
   
   /** The groovy. */
   GROOVY("gvy","groovy"),
   
   /** The javascript. */
   JAVASCRIPT("js","js"),
   
   /** The ssjs. */
   SSJS("ssjs","js"),
   
   /** The agent. */
   AGENT("agent","agent"),
   
   /** The beanshell. */
   BEANSHELL("bsh","beanshell");
	
	
	
	/** The ext. */
	private String ext;
	
	/** The engine. */
	private String engine;
	
	/**
	 * Ext.
	 *
	 * @return the string
	 */
	public String ext(){return ext;}
	
	/**
	 * Engine.
	 *
	 * @return the string
	 */
	public String engine(){return engine;}
	
	/**
	 * Instantiates a new supported engine.
	 *
	 * @param ext the ext
	 * @param engine the engine
	 */
	SupportedEngine(String ext, String engine){
		this.ext = ext;
		this.engine = engine;
	}
	
	
	/**
	 * Find engine.
	 *
	 * @param resource the resource
	 * @return the string
	 */
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
