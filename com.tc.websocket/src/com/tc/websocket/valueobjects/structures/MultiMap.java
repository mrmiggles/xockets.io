/*
 * 
 */
package com.tc.websocket.valueobjects.structures;

import java.util.concurrent.ConcurrentHashMap;


// TODO: Auto-generated Javadoc
/**
 * The Class MultiMap.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class MultiMap<K, V> extends ConcurrentHashMap<K,V> implements IMultiMap<K, V>{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8419187370120465167L;

	/**
	 * Instantiates a new multi map.
	 *
	 * @param capacity the capacity
	 */
	public MultiMap(int capacity){
		super(capacity);

	}
	
	/**
	 * Instantiates a new multi map.
	 */
	public MultiMap(){
		super();
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.structures.IMultiMap#putWithKeys(java.lang.Object, java.lang.Object[])
	 */
	@Override
	public void putWithKeys(V value, K ...keys){
		for(K key : keys){
			this.put(key, value);
		}
	}
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.structures.IMultiMap#removeWithKeys(java.lang.Object[])
	 */
	@Override
	public void removeWithKeys(K ...keys){
		for(K k : keys){
			this.remove(k);
		}
	}

	
}
