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


package com.tc.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.SystemUtils;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.tc.guice.domino.module.ServerInfo;
import com.tc.guice.domino.module.SessionFactory;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.utils.XSPUtils;

public class Config implements Runnable, IConfig {
	private static Logger logger = Logger.getLogger(Config.class.getName());

	private boolean allowAnonymous;
	private boolean encrypted;
	private String keyStore;
	private String keyStorePassword;
	private String keyPassword;
	private String keyStoreType;
	private int port;
	private boolean debug;
	private boolean profiled;
	private boolean nativeTransport;


	private String websocketFilter;
	private boolean onServer;
	private boolean testMode;
	private long maxSize;
	private int maxConnections;
	private int pingInterval;
	private int purgeInterval;

	private boolean clustered;
	private String broadcastServer;
	private String clustermateMonitor;
	private int clustermateExpiration;
	private String error;
	private int sendBuffer;
	private int receiveBuffer;
	private int clientCacheMax;
	private boolean compressionEnabled;
	



	//server side Id used for generating sessions.
	private String username;
	private String password;
	private int threadCount;
	
	

	private int eventLoopThreads;
	private boolean valid;
	private List<String> allowedOrigins;
	private Properties props;

	private static IConfig config = new Config();


	//private constructor / singleton
	private Config(){
		this.run();
	}

	public static IConfig getInstance(){
		return config;
	}


	@Override
	public boolean isAllowAnonymous() {
		return allowAnonymous;
	}
	
	
	@Override
	public boolean isBroadcastServer(){
		return ServerInfo.getInstance().getServerName().equals(this.getBroadcastServer());
	}


	@Override
	public boolean isEncrypted() {
		return encrypted;
	}


	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getKeyStore() {
		return keyStore;
	}

	@Override
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	@Override
	public String getKeyPassword() {
		return keyPassword;
	}

	@Override
	public boolean isDebug(){
		return debug;
	}


	@Override
	public String getKeyStoreType(){
		return this.keyStoreType;
	}

	@Override
	public String getWebsocketFilter(){
		return this.websocketFilter;
	}

	@Override
	public boolean isTestMode(){
		return this.testMode;
	}


	@Override
	public void print(Object o){
		System.out.println("websocket service: " + o);
	}


	@Override
	public void run() {
		//create a trsuted session
		
		print("Opening trusted session to gain access to the config document.");
		Session s = SessionFactory.openTrusted();
		
	

		StringBuilder sb = new StringBuilder();

		//bind to the notes.ini settings.
		try {
			
			if(s==null){
				sb.append("Unable to obtain session.  Make sure the following parameters are setup in your notes.ini\n\n");
				sb.append("XPagesPreload=1\nXPagesPreloadDB=websocket.nsf\n\n");
				this.valid = false;
				this.error = sb.toString();
				return;
			}
			
			this.print("Config session is " + s.getEffectiveUserName());
			
			
			int preloadFlag = envAsInt(s, "XPagesPreload", -1);
			if(preloadFlag == -1){
				sb.append("XPagesPreload=1 is required in the notes.ini");
			}
			
			
			String preload = this.env(s, "XPagesPreloadDB", StringCache.EMPTY);
			if(!preload.startsWith(Const.WEBSOCKET_PATH)){
				sb.append("XPagesPreloadDB=websocket.nsf is required in the notes.ini.  Make sure websocket.nsf is the first parameter.");
			}


			Database db = s.getDatabase(StrUtils.EMPTY_STRING, Const.WEBSOCKET_PATH);
			if (db == null || !db.isOpen()){
				sb.append(Const.WEBSOCKET_PATH +  " is required!");
			}
			
			//check to see if the config data is stored in the websocket.nsf
			Document docConfig = db.getProfileDocument(Const.PROFILE_FORM, Const.PROFILE_KEY);
			
			
			if (!StrUtils.isEmpty(docConfig.getItemValueString("config"))){
				this.props = XSPUtils.loadProps(docConfig, Const.PROFILE_FIELD);
				print("Loading websocket configuration from " + Const.WEBSOCKET_PATH);
			}else{
				print("Loading websocket configuration from notes.ini");
			}
			
			//setup the creds used for server-side operations.
			this.username = env(s,"WEBSOCKET_USER","");
			this.password=env(s,"WEBSOCKET_PASSWORD","");
			
			if(StrUtils.isEmpty(username) || StrUtils.isEmpty(password)){
				sb.append("WEBSOCKET_USER and WEBSOCKET_PASSWORD must be populated.");
			}else{
				print("Websocket operations will run under userId " + this.username);
			}
			
			
			this.nativeTransport=envAsBool(s,"WEBSOCKET_NATIVE_TRANSPORT",false);
			if(this.nativeTransport && !SystemUtils.IS_OS_LINUX){
				print("****WARNING: Native transport only available on Linux based machines. Reverting to NIO transport.****");
				this.nativeTransport=false;
			}
		
			this.onServer = s.isOnServer();
			
			this.sendBuffer=envAsInt(s,"WEBSOCKET_SEND_BUFFER",Const.WEBSOCKET_SEND_BUFFER);
			if(sendBuffer < Const.WEBSOCKET_SEND_BUFFER){
				print("Send buffer too low, restoring default " + Const.WEBSOCKET_SEND_BUFFER + " bytes");
				sendBuffer = Const.WEBSOCKET_SEND_BUFFER;
			}else{
				print("Send buffer is " + sendBuffer + " bytes");
			}
			
			this.receiveBuffer=envAsInt(s,"WEBSOCKET_RECEIVE_BUFFER",Const.WEBSOCKET_RECEIVE_BUFFER);
			if(receiveBuffer < Const.WEBSOCKET_RECEIVE_BUFFER){
				print("Receive buffer too low, restoring default " + Const.WEBSOCKET_RECEIVE_BUFFER + " bytes");
				sendBuffer = Const.WEBSOCKET_RECEIVE_BUFFER;
			}else{
				print("Receive buffer is " + this.receiveBuffer + " bytes");
			}
			
			
			this.allowedOrigins = envAsList(s,"WEBSOCKET_ALLOWED_ORIGINS", new ArrayList<String>());
			if(this.allowedOrigins.isEmpty()){
				sb.append("WEBSOCKET_ALLOWED_ORIGINS must be populated.");
			}else if (allowedOrigins.contains(StringCache.STAR)){
				print ("****WARNING: All origins are allowed. not recommended****");
			}else{
				print("Allowed origins are " + allowedOrigins.toString());
			}
			
			this.profiled = envAsBool(s,"WEBSOCKET_PROFILED", false);
			if(this.isProfiled()){
				print("****WARNING: Profiling enabled on the websocket server****");
			}
			
			//get the thread count
			this.eventLoopThreads = envAsInt(s,"WEBSOCKET_EVENT_LOOP_THREADS", Const.WEBSOCKET_EVENT_LOOP_THREADS);
			print("Loading " + this.eventLoopThreads + " event loop thread(s).");
			
			this.threadCount = envAsInt(s,"WEBSOCKET_THREAD_COUNT", Const.WEBSOCKET_THREAD_COUNT);
			print("Loading " + this.threadCount + " other background thread(s).");
			
			//set the ping interval, default to 60 seconds
			this.pingInterval = envAsInt(s,"WEBSOCKET_PING_INTERVAL",Const.WEBSOCKET_PING_INTERVAL);
			
			//run the purge every 900 seconds (15 minutes)
			this.purgeInterval = envAsInt(s,"WEBSOCKET_PURGE_INTERVAL", Const.WEBSOCKET_PURGE_INTERVAL);
			print ("Purge interval is " + purgeInterval + " seconds.");
			
			this.compressionEnabled = envAsBool(s,"WEBSOCKET_COMPRESSION_ENABLED",Const.WEBSOCKET_COMPRESSION_ENABLED);
			if(compressionEnabled){
				print("Compression enabled.");
			}else{
				print("Compression disabled");
			}
			
			this.clientCacheMax= envAsInt(s,"WEBSOCKET_CLIENT_CACHE_MAX", 1000);
			this.port= envAsInt(s,"WEBSOCKET_PORT", 8889);
		
			this.maxConnections=envAsInt(s,"WEBSOCKET_MAX_CONNECTIONS", Const.WEBSOCKET_MAX_CONNECTIONS);
			print ("Maximum connections " + maxConnections);

			this.maxSize = envAsLong(s, "WEBSOCKET_MAX_MSG_SIZE", Const.WEBSOCKET_MAX_MSG_SIZE);
			if(maxSize < Const.WEBSOCKET_MAX_MSG_SIZE){
				print("****WARNING: Max message size of " + maxSize + " is less than the default of " + Const.WEBSOCKET_MAX_MSG_SIZE + " bytes");
			}


			this.encrypted=new Boolean(this.envAsBool(s, "WEBSOCKET_ENCRYPT", Const.WEBSOCKET_ENCRYPT));
			if(encrypted){	
				this.keyStore=env(s, "WEBSOCKET_KEYSTORE_PATH", "");
				this.keyPassword=env(s,"WEBSOCKET_KEY_PASSWORD","");
				this.keyStorePassword=env(s,"WEBSOCKET_KEYSTORE_PASSWORD","");
				this.keyStoreType=env(s,"WEBSOCKET_KEYSTORE_TYPE","");

				String[] check = {keyStore,keyStorePassword,keyStorePassword,keyStoreType};
				for(String str : check){
					if(StrUtils.isEmpty(str)){
						sb.append("if network encryption \nWEBSOCKET_KEYSTORE_PATH\nWEBSOCKET_KEY_PASSWORD\nWEBSOCKET_KEYSTORE_TYPE\nare required\n");
					}
				}
			}else{
				print("****WARNING: Websocket connections not encrypted.****");
			}


			//these config options are used in a clustered environment.
			this.clustered=new Boolean(envAsBool(s,"WEBSOCKET_CLUSTERED", Const.WEBSOCKET_CLUSTERED));
			if(clustered){
				this.clustermateMonitor=env(s,"WEBSOCKET_CLUSTERMATE_MONITOR", "");
				this.clustermateExpiration= this.envAsInt(s, "WEBSOCKET_CLUSTERMATE_EXPIRATION", 300);

				if(StrUtils.isEmpty(clustermateMonitor) || this.clustermateExpiration==-1){
					sb.append("\nif clustering:\n WEBSOCKET_CLUSTERMATE_MONITOR\nWEBSOCKET_CLUSTERMATE_EXPIRATION\nare required.");
				}

				if(ServerInfo.getInstance().isCurrentServer(clustermateMonitor)){
					sb.append("WEBSOCKET_CLUSTERMATE_MONITOR must point to a different server in the cluster.  It cannot point to itself.");
				}
			}

			this.broadcastServer=env(s, "WEBSOCKET_BROADCAST_SERVER", ServerInfo.getInstance().getServerName());

			//other settings
			this.debug=envAsBool(s,"WEBSOCKET_DEBUG", Const.WEBSOCKET_DEBUG);
			if(this.isDebug()){
				print("****WARNING: In debug mode.  Logs will be flooded.****");
			}

			this.allowAnonymous=envAsBool(s,"WEBSOCKET_ALLOW_ANONYMOUS", Const.WEBSOCKET_ALLOW_ANONYMOUS);
			
			this.testMode=envAsBool(s,"WEBSOCKET_TEST_MODE", Const.WEBSOCKET_TEST_MODE);
			if(this.isTestMode()){
				print("****WARNING: In test mode****");
			}
			
			
			this.websocketFilter=env(s,"WEBSOCKET_FILTER",null);
			if(this.websocketFilter == null){
				print("No websocket filter registered.");
			}


			if(sb.length()==0){
				valid = true;
			}else{
				this.error = sb.toString();
			}

		} catch (NotesException e) {
			logger.log(Level.SEVERE,null,e);

		} catch(Exception e){
			logger.log(Level.SEVERE,null, e);

		} finally{
			SessionFactory.closeSession(s);
		}

	}

	@Override
	public int getEventLoopThreads() {
		return eventLoopThreads;
	}

	@Override
	public boolean isValid(){
		return valid;
	}
	
	private String getValue(Session s, String key) throws NotesException{
		String str=null;
		if(this.props!=null){
			str = props.getProperty(key);
		}else{
			str = s.getEnvironmentString(key,true);
		}
		return str;
	}

	private boolean envAsBool(Session s, String key, boolean defaultValue) throws NotesException{
		String str=this.getValue(s, key);
		if(!StrUtils.isEmpty(str)){
			return new Boolean(str);
		}
		return defaultValue;
	}

	private String env(Session s, String key, String defaultValue) throws NotesException{
		String str= this.getValue(s,key);
		if(StrUtils.isEmpty(str)){
			return defaultValue;
		}
		return str;
	}
	
	private List<String> envAsList(Session s, String key, List<String> defaultValue) throws NotesException{
		String str=this.getValue(s,key);
		if(StrUtils.isEmpty(str)){
			return defaultValue;
		}
		String[] arr = str.split(",");
		List<String> list = new ArrayList<String>();
		for(String value : arr){
			list.add(value.trim());
		}
		return list;
	}

	private int envAsInt(Session s, String key, int defaultValue) throws NotesException{
		String str= this.getValue(s,key);
		if(StrUtils.isEmpty(str)){
			return defaultValue;
		}
		return Integer.parseInt(str);
	}

	private long envAsLong(Session s, String key, long defaultValue) throws NotesException{
		String str= this.getValue(s, key);
		if(StrUtils.isEmpty(str)){
			return defaultValue;
		}
		return Long.parseLong(str);
	}


	@Override
	public boolean isOnServer() {
		return onServer;
	}

	@Override
	public void setOnServer(boolean onServer) {
		this.onServer = onServer;
	}


	@Override
	public String getBroadcastServer() {
		return broadcastServer;
	}


	@Override
	public String getClustermateMonitor(){
		return this.clustermateMonitor;
	}

	@Override
	public int getClustermateExpiration() {
		return clustermateExpiration;
	}

	@Override
	public boolean isClustered(){
		return this.clustered;
	}


	@Override
	public String getError(){
		return this.error;
	}

	@Override
	public long getMaxSize() {
		return maxSize;
	}

	@Override
	public int getMaxConnections() {
		return maxConnections;
	}

	@Override
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	@Override
	public int getPingInterval() {
		return pingInterval;
	}

	@Override
	public int getThreadCount() {
		return threadCount;
	}

	@Override
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	@Override
	public int getPurgeInterval() {
		return purgeInterval;
	}

	@Override
	public void setPurgeInterval(int purgeInterval) {
		this.purgeInterval = purgeInterval;
	}

	@Override
	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	@Override
	public boolean isAllowedOrigin(String origin){
		boolean b = false;
		URI uri=null;
		try {
			uri = new URI(origin);
			b=this.getAllowedOrigins().contains(uri.getHost()) || this.getAllowedOrigins().contains(StringCache.STAR);
		} catch (URISyntaxException e) {
			logger.log(Level.SEVERE, null, e);
		}
		return b;
	}

	
	@Override
	public boolean isProfiled() {
		return profiled;
	}

	@Override
	public String getUsername() {
		return username;
	}



	@Override
	public String getPassword() {
		return password;
	}


	@Override
	public int getClientCacheMax() {
		return this.clientCacheMax;
	}
	
	
	@Override
	public boolean isCompressionEnabled() {
		return compressionEnabled;
	}

	@Override
	public int getSendBuffer() {
		return sendBuffer;
	}

	@Override
	public int getReceiveBuffer() {
		return receiveBuffer;
	}
	
	@Override
	public boolean isNativeTransport(){
		return this.nativeTransport;
	}

}
