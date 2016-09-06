/*
 * 
 */
package com.tc.websocket.valueobjects;

import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Interface IUri.
 */
public interface IUri {
	
	/**
	 * Gets the uris.
	 *
	 * @return the uris
	 */
	public List<String> getUris();
	
	/**
	 * Contains uri.
	 *
	 * @param uri the uri
	 * @return true, if successful
	 */
	public boolean containsUri(String uri);
	
	/**
	 * Starts with.
	 *
	 * @param uri the uri
	 * @return true, if successful
	 */
	public boolean startsWith(String uri);
	
	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public String getUri();
}
