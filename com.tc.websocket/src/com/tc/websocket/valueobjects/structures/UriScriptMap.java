/*
 * 
 */
package com.tc.websocket.valueobjects.structures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tc.utils.StringCache;
import com.tc.websocket.scripts.Script;
import com.tc.websocket.server.RoutingPath;



// TODO: Auto-generated Javadoc
/**
 * The Class UriScriptMap.
 */
public class UriScriptMap {
	
	
	/** The map. */
	private static Map<String, Collection<Script>> map = new ConcurrentHashMap<String,Collection<Script>>();

	/**
	 * All.
	 *
	 * @return the collection
	 */
	public Collection<Script> all(){
		Set<Script> list = new HashSet<Script>();
		for(Collection<Script> col : map.values()){
			list.addAll(col);
		}
		return list;
	}
	
	
	/**
	 * Adds the script.
	 *
	 * @param script the script
	 */
	private synchronized void addScript(Script script){
		String[] parts = script.getUri().split(StringCache.FORWARD_SLASH);
		StringBuilder sb = new StringBuilder();
		for(String part : parts){
			if(part.length() > 0){
				sb.append(StringCache.FORWARD_SLASH).append(part);
				this.addToCollection(sb.toString(), script);
			}
		}
	}
	
	/**
	 * Adds the to collection.
	 *
	 * @param uri the uri
	 * @param script the script
	 */
	private synchronized void addToCollection(String uri, Script script){
		Collection<Script> col = this.get(uri);
		if(!col.contains(script)){
			col.add(script);
		}
	}
	
	
	/**
	 * Adds the.
	 *
	 * @param script the script
	 */
	public synchronized void add(Script script){
		this.addScript(script);
	}
	
	/**
	 * Removes the.
	 *
	 * @param script the script
	 */
	public synchronized void remove(Script script){
		for(Collection<Script> col : map.values()){
			if(col.contains(script)){
				col.remove(script);
			}
		}
	}
	
	/**
	 * Gets the.
	 *
	 * @param uri the uri
	 * @return the collection
	 */
	public synchronized Collection<Script> get(String uri){
		//find exact mactch
		Collection<Script> scripts = map.get(uri);
		if(scripts == null){
			scripts = new HashSet<Script>();
			map.put(uri, scripts);
		}
		scripts.addAll(this.findWildCards(uri));
		return scripts;
	}
	
	
	/**
	 * Find wild cards.
	 *
	 * @param uri the uri
	 * @return the collection
	 */
	private Collection<Script> findWildCards(String uri){
		Collection<Script> col = new HashSet<Script>();
		
		StringBuilder sb = new StringBuilder();
		String[] parts = uri.split(StringCache.FORWARD_SLASH);
		
		for(String part : parts){
			sb = part.equals(StringCache.EMPTY) ? sb.append(StringCache.EMPTY): sb.append(StringCache.FORWARD_SLASH).append(part);
			Collection<Script> results = map.get(sb.toString());
			if(results!=null){
				for(Script script : results){
					if(script.isWild()){
						col.add(script);
					}
				}
			}	
		}
		return col;
	}
	
	/**
	 * Gets the.
	 *
	 * @param path the path
	 * @return the collection
	 */
	public synchronized Collection<Script> get(RoutingPath path){
		return this.get(path.getUri());
	}
	
	/**
	 * Clear.
	 */
	public synchronized void clear(){
		map.clear();
	}
	
	
	/**
	 * Prints the.
	 */
	public void print(){
		for(String key : map.keySet()){
			print("Listeners for " + key);
			for(Script script : map.get(key)){
				print("\t\t\t" + script.getSource());
			}
		}
	}
	
	/**
	 * Prints the.
	 *
	 * @param o the o
	 */
	private void print(Object o){
		System.out.println(o);
	}

}
