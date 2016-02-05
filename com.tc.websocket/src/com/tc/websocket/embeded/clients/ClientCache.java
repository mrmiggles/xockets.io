package com.tc.websocket.embeded.clients;

import com.ibm.commons.util.SystemCache;

public class ClientCache implements IClientCache {
	
	private static final SystemCache cache = new SystemCache(ClientCache.class.getName(),1000);
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#clear()
	 */
	@Override
	public void clear(){
		cache.clear();
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#get(java.lang.String)
	 */
	@Override
	public Object get(String key){
		return cache.get(key);
	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#getAccessedTimes()
	 */
	@Override
	public long getAccessedTimes(){
		return cache.getAccessedTimes();
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#getAddedTimes()
	 */
	@Override
	public long getAddedTimes(){
		return cache.getAddedTimes();
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#getCapacity()
	 */
	@Override
	public int getCapacity(){
		return cache.getCapacity();
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#getDiscardedTimes()
	 */
	@Override
	public long getDiscardedTimes(){
		return cache.getDiscardedTimes();
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#getInCacheTimes()
	 */
	@Override
	public long getInCacheTimes(){
		return cache.getInCacheTimes();
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#getName()
	 */
	@Override
	public String getName(){
		return cache.getName();
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#put(java.lang.String, java.lang.Object)
	 */
	@Override
	public void put(String key, Object value){
		cache.put(key,value);
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#remove(java.lang.String)
	 */
	@Override
	public void remove(String key){
		cache.remove(key);
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IRhinoCache#size()
	 */
	@Override
	public int size(){
		return cache.size();
	}
}
