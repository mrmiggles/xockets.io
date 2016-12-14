/*
 * 
 */
package com.tc.websocket.runners;

import java.util.HashSet;
import java.util.Set;

import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;


// TODO: Auto-generated Javadoc
/**
 * The Class BatchSend.
 */
public class BatchSend implements Runnable{
	
	/** The users. */
	private Set<IUser> users = new HashSet<IUser>();
	
	/** The message. */
	private SocketMessage message;
	
	
	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(SocketMessage message){
		this.message = message;
	}
	
	/**
	 * Adds the user.
	 *
	 * @param user the user
	 */
	public void addUser(IUser user){
		this.users.add(user);
	}
	
	/**
	 * Removes the user.
	 *
	 * @param user the user
	 */
	public void removeUser(IUser user){
		this.users.remove(user);
	}
	
	/**
	 * Count.
	 *
	 * @return the int
	 */
	public int count(){
		return users.size();
	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		for(IUser user : this.users){
			user.send(message);
		}
	}

}
