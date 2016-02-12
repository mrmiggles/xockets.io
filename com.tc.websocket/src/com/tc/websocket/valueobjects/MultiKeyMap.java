package com.tc.websocket.valueobjects;

import java.util.concurrent.ConcurrentHashMap;

public class MultiKeyMap<K, V> extends ConcurrentHashMap<K,V>{
	
	private static final long serialVersionUID = -8173256423136448229L;

	
	public MultiKeyMap(int capacity){
		super(capacity);

	}
	
	public MultiKeyMap(){
		super();
	}

	public void putWithKeys(V value, K ...keys){
		for(K key : keys){
			this.put(key, value);
		}
	}
	
	public void remove(K ...keys){
		for(K k : keys){
			this.remove(k);
		}
	}
}
