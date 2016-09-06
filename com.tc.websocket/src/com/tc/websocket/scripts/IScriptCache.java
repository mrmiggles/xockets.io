/*
 * 
 */
package com.tc.websocket.scripts;


// TODO: Auto-generated Javadoc
/**
 * The Interface IScriptCache.
 */
public interface IScriptCache {
	
	/**
	 * Clear.
	 */
	public abstract void clear();

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the object
	 */
	public abstract Object get(String key);

	/**
	 * Gets the accessed times.
	 *
	 * @return the accessed times
	 */
	public abstract long getAccessedTimes();

	/**
	 * Gets the added times.
	 *
	 * @return the added times
	 */
	public abstract long getAddedTimes();

	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 */
	public abstract int getCapacity();

	/**
	 * Gets the discarded times.
	 *
	 * @return the discarded times
	 */
	public abstract long getDiscardedTimes();

	/**
	 * Gets the in cache times.
	 *
	 * @return the in cache times
	 */
	public abstract long getInCacheTimes();

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public abstract void put(String key, Object value);

	/**
	 * Removes the.
	 *
	 * @param key the key
	 */
	public abstract void remove(String key);

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public abstract int size();

}
