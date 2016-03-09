package com.tc.websocket.valueobjects.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tc.websocket.Config;
import com.tc.websocket.server.RoutingPath;
import com.tc.websocket.valueobjects.IUser;



public class UriMap {
	
	
	private static Map<String, List<IUser>> map = new ConcurrentHashMap<String,List<IUser>>(Config.getInstance().getMaxConnections()/2);
	
	
	public synchronized void add(IUser user){
		List<IUser> list = this.get(user.getUri());
		if(!list.contains(user)){
			list.add(user);
		}
	}
	
	public synchronized void remove(IUser user){
		List<IUser> list = this.get(user.getUri());
		list.remove(user);
		if(list.isEmpty()){
			map.remove(user.getUri());
		}
	}
	
	public synchronized List<IUser> get(String uri){
		List<IUser> users = map.get(uri);
		if(users == null){
			users = new ArrayList<IUser>();
			map.put(uri, users);
		}
		return users;
	}
	
	public synchronized List<IUser> get(RoutingPath path){
		List<IUser> users = new ArrayList<IUser>();
		users.addAll(this.get(path.getUri()));
		
		if(path.isWild()){
			
			for(String uri : map.keySet()){
				if(path.getUri().contains(uri) || uri.contains(path.getUri())){
					users.addAll(map.get(uri));
				}
			}
			
		}
		
		return users;
	}
	
	public synchronized void clear(){
		map.clear();
	}
	

}
