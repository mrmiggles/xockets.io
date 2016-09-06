/*
 * 
 */
package com.tc.websocket.valueobjects.structures;

import java.util.Map;



// TODO: Auto-generated Javadoc
/**
 * The Interface IMultiMap.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public interface IMultiMap<K, V> extends Map<K,V> {

	/**
	 * Put with keys.
	 *
	 * @param value the value
	 * @param keys the keys
	 */
	public abstract void putWithKeys(V value, K... keys);

	/**
	 * Removes the with keys.
	 *
	 * @param keys the keys
	 */
	public abstract void removeWithKeys(K... keys);

}