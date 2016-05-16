package com.tc.websocket.valueobjects.structures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tc.websocket.Config;
import com.tc.websocket.server.RoutingPath;
import com.tc.websocket.valueobjects.IUser;



public class UriMap {
	
	
	private static Map<String, Collection<IUser>> map = new ConcurrentHashMap<String,Collection<IUser>>(Config.getInstance().getMaxConnections()/2);
	
	
	public synchronized void add(IUser user){
		for(String uri : user.getUris()){
			Collection<IUser> list = this.get(uri);
			if(!list.contains(user)){
				list.add(user);
			}
		}
	}
	
	public synchronized void remove(IUser user){
		for(String uri : user.getUris()){
			Collection<IUser> list = this.get(uri);
			list.remove(user);
			if(list.isEmpty()){
				map.remove(user.getUri());
			}
		}
	}
	
	public synchronized Collection<IUser> get(String uri){
		Collection<IUser> users = map.get(uri);
		if(users == null){
			users = new HashSet<IUser>();
			map.put(uri, users);
		}
		return users;
	}
	
	public synchronized Collection<IUser> get(RoutingPath path){
		Collection<IUser> users = new HashSet<IUser>();
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
