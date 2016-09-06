/*
 * 
 */
package com.tc.websocket.scripts;

import com.ibm.commons.util.SystemCache;


// TODO: Auto-generated Javadoc
/**
 * The Class ScriptCache.
 */
public class ScriptCache implements IScriptCache{
	
	/** The Constant cache. */
	private static final SystemCache cache = new SystemCache(ScriptCache.class.getName(),1000);
	
	/** The Constant sCache. */
	private static final ScriptCache sCache = new ScriptCache();
	
	/**
	 * Instantiates a new script cache.
	 */
	private ScriptCache(){
		
	}
	
	/**
	 * Insta.
	 *
	 * @return the script cache
	 */
	public static ScriptCache insta(){
		return sCache;
	}
	

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#clear()
	 */
	@Override
	public void clear(){
		cache.clear();
	}
	


	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#get(java.lang.String)
	 */
	@Override
	public Object get(String key){
		return cache.get(key);
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#getAccessedTimes()
	 */
	@Override
	public long getAccessedTimes(){
		return cache.getAccessedTimes();
	}
	


	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#getAddedTimes()
	 */
	@Override
	public long getAddedTimes(){
		return cache.getAddedTimes();
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#getCapacity()
	 */
	@Override
	public int getCapacity(){
		return cache.getCapacity();
	}
	

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#getDiscardedTimes()
	 */
	@Override
	public long getDiscardedTimes(){
		return cache.getDiscardedTimes();
	}
	


	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#getInCacheTimes()
	 */
	@Override
	public long getInCacheTimes(){
		return cache.getInCacheTimes();
	}
	


	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#getName()
	 */
	@Override
	public String getName(){
		return cache.getName();
	}
	


	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#put(java.lang.String, java.lang.Object)
	 */
	@Override
	public void put(String key, Object value){
		cache.put(key,value);
	}
	


	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#remove(java.lang.String)
	 */
	@Override
	public void remove(String key){
		cache.remove(key);
	}
	


	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.IScriptCache#size()
	 */
	@Override
	public int size(){
		return cache.size();
}

}
