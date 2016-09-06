/*
 * 
 */
package com.tc.websocket.valueobjects.structures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.server.RoutingPath;
import com.tc.websocket.valueobjects.IUser;




// TODO: Auto-generated Javadoc
/**
 * The Class UriUserMap.
 */
public class UriUserMap {
	
	
	/** The map. */
	private static Map<String, Collection<IUser>> map = new ConcurrentHashMap<String,Collection<IUser>>(Config.getInstance().getMaxConnections()/2);
	
	
	/**
	 * Adds the.
	 *
	 * @param user the user
	 */
	public synchronized void add(IUser user){
		for(String uri : user.getUris()){
			Collection<IUser> list = this.get(uri);
			if(!list.contains(user)){
				list.add(user);
			}
			
			//now lets append user's Id
			Collection<IUser> direct = this.get(uri + StringCache.FORWARD_SLASH + user.getUserId());
			if(!direct.contains(user)){
				direct.add(user);
			}
		}

	}
	
	/**
	 * Removes the.
	 *
	 * @param user the user
	 */
	public synchronized void remove(IUser user){
		for(String uri : user.getUris()){
			Collection<IUser> list = this.get(uri);
			list.remove(user);
			if(list.isEmpty()){
				map.remove(user.getUri());
			}
			
			//remove the direct uris
			map.remove(uri + StringCache.FORWARD_SLASH + user.getUserId());
		}
	}
	
	/**
	 * Gets the.
	 *
	 * @param uri the uri
	 * @return the collection
	 */
	public synchronized Collection<IUser> get(String uri){
		Collection<IUser> users = map.get(uri);
		if(users == null){
			users = new HashSet<IUser>();
			map.put(uri, users);
		}
		return users;
	}
	
	/**
	 * Gets the.
	 *
	 * @param path the path
	 * @return the collection
	 */
	public synchronized Collection<IUser> get(RoutingPath path){
		Collection<IUser> users = new HashSet<IUser>();
		users.addAll(this.get(path.getUri()));
		
		if(path.isWild()){
			
			for(Entry<String, Collection<IUser>> entry : map.entrySet()){
				String uri = entry.getKey();
				if(path.getUri().contains(uri) || uri.contains(path.getUri())){
					users.addAll(entry.getValue());
				}
			}
		}
		
		return users;
	}
	
	/**
	 * Clear.
	 */
	public synchronized void clear(){
		map.clear();
	}
	

}
