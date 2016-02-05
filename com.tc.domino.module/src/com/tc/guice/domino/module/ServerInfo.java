package com.tc.guice.domino.module;

import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.NotesException;
import lotus.domino.Session;

public class ServerInfo implements Runnable {
	private static final Logger logger = Logger.getLogger(ServerInfo.class.getName());
	private static ServerInfo wrapper = new ServerInfo();

	private boolean onServer;
	private String version;
	private String serverName;
	private String platform;


	public static ServerInfo getInstance(){
		return wrapper;
	}

	public boolean isOnServer(){
		return onServer;
	}

	//private constructor / singleton
	private ServerInfo(){
		try{
			this.run();
		}catch(Exception e){
			logger.log(Level.SEVERE,null,e);
		}
	}

	@Override
	public void run() {
		
			Session session = SessionFactory.openTrusted();
			try{

				this.onServer = session.isOnServer();
				this.version = session.getNotesVersion();
				this.serverName = session.getServerName();
				this.platform = session.getPlatform();

			}catch(NotesException n){
				logger.log(Level.SEVERE,null,n);
				
			}finally{
				SessionFactory.closeSession(session);
			}
	}

	public String getVersion() {
		return version;
	}

	public String getServerName() {
		return serverName;
	}

	public String getPlatform() {
		return platform;
	}
	
	
	public boolean isCurrentServer(String server){
		return this.getServerName().equals(server);
	}



}
