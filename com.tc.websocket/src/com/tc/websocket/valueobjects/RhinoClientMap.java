package com.tc.websocket.valueobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tc.websocket.embeded.clients.RhinoClient;

public class RhinoClientMap {
	
	
	private static Map<String, List<RhinoClient>> map = new ConcurrentHashMap<String,List<RhinoClient>>();
	
	
	public synchronized void add(RhinoClient client){
		String uri = client.getUser().getUri();
		List<RhinoClient> list = this.get(uri);
		if(!list.contains(client)){
			list.add(client);
		}
	}
	
	public synchronized void remove(RhinoClient client){
		List<RhinoClient> list = this.get(client.getUser().getUri());
		list.remove(client);
		if(list.isEmpty()){
			//map.remove(client.getURI().toString());
		}
	}
	
	public synchronized List<RhinoClient> get(String uri){
		List<RhinoClient> clients = map.get(uri);
		if(clients == null){
			clients = new ArrayList<RhinoClient>();
			map.put(uri, clients);
		}
		return clients;
	}
	
	
	public synchronized List<RhinoClient> getAll(){
		List<RhinoClient> list = new ArrayList<RhinoClient>();
		for(String key : map.keySet()){
			List<RhinoClient> clients = map.get(key);
			list.addAll(clients);
		}
		return list;
	}
	

}
