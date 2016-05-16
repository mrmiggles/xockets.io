package com.tc.websocket.runners;

import java.util.HashSet;
import java.util.Set;

import com.tc.websocket.valueobjects.IUser;

public class BatchSend implements Runnable{
	
	private Set<IUser> users = new HashSet<IUser>();
	private String message;
	
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public void addUser(IUser user){
		this.users.add(user);
	}
	
	public void removeUser(IUser user){
		this.users.remove(user);
	}
	
	public int count(){
		return users.size();
	}

	@Override
	public void run() {
		for(IUser user : this.users){
			user.send(message);
		}
	}

}
