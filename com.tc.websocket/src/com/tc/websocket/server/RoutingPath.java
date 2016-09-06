/*
 * © Copyright Tek Counsel LLC 2016
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package com.tc.websocket.server;

import java.util.Vector;

import com.tc.utils.StringCache;
import com.tc.websocket.Const;



// TODO: Auto-generated Javadoc
/**
 * The Class RoutingPath.
 */
public class RoutingPath {

	/** The uri. */
	private String uri;
	
	/** The roles. */
	private String[] roles;
	
	/** The db path. */
	private String dbPath;
	
	/** The wild. */
	private boolean wild;


	/**
	 * Instantiates a new routing path.
	 *
	 * @param uri the uri
	 */
	public RoutingPath(String uri){
		this.roles = this.extractRoles(uri);
		this.dbPath = this.extractDbPath(uri);
		this.uri = this.extractUri(uri);
	}



	/**
	 * Extract db path.
	 *
	 * @param uri the uri
	 * @return the string
	 */
	private String extractDbPath(String uri){
		uri = uri.toLowerCase();
		if(!uri.contains(StringCache.DOT_NSF)) return Const.WEBSOCKET_PATH;//return the websocket.nsf as default.
		int nsf = uri.indexOf(StringCache.DOT_NSF);
		String dbPath=uri.substring(0,nsf) + StringCache.DOT_NSF;
		return dbPath;
	}

	/**
	 * Extract roles.
	 *
	 * @param uri the uri
	 * @return the string[]
	 */
	private String[] extractRoles(String uri){

		if(!uri.contains(StringCache.OPEN_BRACKET) && !uri.contains(StringCache.CLOSE_BRACKET)) return null;

		String[] roles=null;
		int start = uri.indexOf(StringCache.OPEN_BRACKET);
		int end = uri.lastIndexOf(StringCache.CLOSE_BRACKET);
		String strRoles = uri.substring(start,end + 1);
		if(strRoles.contains(StringCache.COMMA)){
			roles = strRoles.split(StringCache.COMMA);
		}else{
			roles = new String[1];
			roles[0]=strRoles;
		}
		return roles;
	}

	/**
	 * Extract uri.
	 *
	 * @param uri the uri
	 * @return the string
	 */
	private String extractUri(String uri){
		StringBuilder sb = new StringBuilder();
		int len = uri.length();
		for(int i=0;i < len; i++){
			char c = uri.charAt(i);
			if(c=='['){
				break;
			}else{
				sb.append(c);
			}
		}
		uri = sb.toString();
		if (uri.endsWith("/")){
			uri = uri.substring(0,uri.length() -1);
		}
		
		if(uri.endsWith(StringCache.STAR)){
			uri = uri.replace(StringCache.STAR, StringCache.EMPTY);
			wild = true;
		}
		
		return uri;
	}
	

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}



	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public String[] getRoles() {
		return roles;
	}



	/**
	 * Gets the db path.
	 *
	 * @return the db path
	 */
	public String getDbPath() {
		return dbPath;
	}

	/**
	 * Checks for roles.
	 *
	 * @return true, if successful
	 */
	public boolean hasRoles(){
		return roles!=null;
	}

	/**
	 * Checks if is wild.
	 *
	 * @return true, if is wild
	 */
	public boolean isWild(){
		return wild;
	}

	/**
	 * Checks if is member.
	 *
	 * @param dbRoles the db roles
	 * @return true, if is member
	 */
	public boolean isMember(Vector<String> dbRoles){
		boolean b = false;
		if(this.hasRoles()){
			for(String role : this.getRoles()){
				if(dbRoles.contains(role)){
					b = true;
					break;
				}
			}
		}
		return b;
	}
	
}
