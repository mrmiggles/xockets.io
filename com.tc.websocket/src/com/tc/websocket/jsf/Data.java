package com.tc.websocket.jsf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Data {
	private static Map<String, HashMap<String,Object>> data = new ConcurrentHashMap<String,HashMap<String,Object>>();

	private static final Data insta = new Data();

	private Data(){
		//singleton
	}

	public static Data insta(){
		return insta;
	}


	public Map<String,Object> get(String key){
		HashMap<String,Object> storedData = data.get(key);
		if(storedData == null){
			storedData = new HashMap<String,Object>();
			data.put(key, storedData);
		}
		return storedData;
	}

	public void put(String key, String name, Object value){
		this.get(key).put(name,value);
	}

	public void remove(String key){
		data.remove(key);
	}



}
