package com.tc.websocket.scripts;

import com.ibm.commons.util.SystemCache;

public class ScriptCache implements IScriptCache{
	
	private static final SystemCache cache = new SystemCache(ScriptCache.class.getName(),1000);
	private static final ScriptCache sCache = new ScriptCache();
	
	private ScriptCache(){
		
	}
	
	public static ScriptCache insta(){
		return sCache;
	}
	

	@Override
	public void clear(){
		cache.clear();
	}
	

	@Override
	public Object get(String key){
		return cache.get(key);
	}


	@Override
	public long getAccessedTimes(){
		return cache.getAccessedTimes();
	}
	

	@Override
	public long getAddedTimes(){
		return cache.getAddedTimes();
	}

	
	@Override
	public int getCapacity(){
		return cache.getCapacity();
	}
	

	@Override
	public long getDiscardedTimes(){
		return cache.getDiscardedTimes();
	}
	

	@Override
	public long getInCacheTimes(){
		return cache.getInCacheTimes();
	}
	

	@Override
	public String getName(){
		return cache.getName();
	}
	

	@Override
	public void put(String key, Object value){
		cache.put(key,value);
	}
	

	@Override
	public void remove(String key){
		cache.remove(key);
	}
	

	@Override
	public int size(){
		return cache.size();
}

}
