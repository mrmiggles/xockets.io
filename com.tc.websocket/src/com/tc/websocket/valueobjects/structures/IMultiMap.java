package com.tc.websocket.valueobjects.structures;

import java.util.Map;


public interface IMultiMap<K, V> extends Map<K,V> {

	public abstract void putWithKeys(V value, K... keys);

	public abstract void removeWithKeys(K... keys);

}