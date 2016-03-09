package com.tc.websocket.valueobjects.structures;

import java.util.concurrent.ConcurrentHashMap;

public class MultiMap<K, V> extends ConcurrentHashMap<K,V> implements IMultiMap<K, V>{
	
	private static final long serialVersionUID = -8419187370120465167L;

	public MultiMap(int capacity){
		super(capacity);

	}
	
	public MultiMap(){
		super();
	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IMultiMap#putWithKeys(V, K)
	 */
	@Override
	public void putWithKeys(V value, K ...keys){
		for(K key : keys){
			this.put(key, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IMultiMap#removeWithKeys(K)
	 */
	@Override
	public void removeWithKeys(K ...keys){
		for(K k : keys){
			this.remove(k);
		}
	}

	
}
