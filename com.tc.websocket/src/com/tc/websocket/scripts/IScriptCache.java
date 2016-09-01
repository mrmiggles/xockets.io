package com.tc.websocket.scripts;

public interface IScriptCache {
	
	public abstract void clear();

	public abstract Object get(String key);

	public abstract long getAccessedTimes();

	public abstract long getAddedTimes();

	public abstract int getCapacity();

	public abstract long getDiscardedTimes();

	public abstract long getInCacheTimes();

	public abstract String getName();

	public abstract void put(String key, Object value);

	public abstract void remove(String key);

	public abstract int size();

}
