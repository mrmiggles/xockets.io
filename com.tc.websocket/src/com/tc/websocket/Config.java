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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

import org.apache.commons.lang3.SystemUtils;

import com.tc.guice.domino.module.ServerInfo;
import com.tc.guice.domino.module.SessionFactory;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.utils.XSPUtils;


// TODO: Auto-generated Javadoc
/**
 * The Class Config.
 */
public class Config implements Runnable, IConfig {
	
	/** The logger. */
	private static Logger LOGGER = Logger.getLogger(Config.class.getName());


	/** The allow anonymous. */
	private boolean allowAnonymous;
	
	/** The encrypted. */
	private boolean encrypted;
	
	/** The key store. */
	private String keyStore;
	
	/** The key store password. */
	private String keyStorePassword;
	
	/** The key password. */
	private String keyPassword;
	
	/** The key store type. */
	private String keyStoreType;
	
	/** The port. */
	private int port;
	
	/** The redirect ports. */
	private List<Integer> redirectPorts;
	
	/** The debug. */
	private boolean debug;
	
	/** The profiled. */
	private boolean profiled;
	
	/** The native transport. */
	private boolean nativeTransport;

	/** The key file. */
	private String keyFile;
	
	/** The cert file. */
	private String certFile;
	
	/** The leak detector. */
	private boolean leakDetector;



	/** The websocket filter. */
	private String websocketFilter;
	
	/** The on server. */
	private boolean onServer;
	
	/** The test mode. */
	private boolean testMode;
	
	/** The max size. */
	private long maxSize;
	
	/** The max connections. */
	private int maxConnections;
	
	/** The ping interval. */
	private int pingInterval;
	
	/** The purge interval. */
	private int purgeInterval;

	/** The clustered. */
	private boolean clustered;
	
	/** The broadcast server. */
	private String broadcastServer;
	
	/** The clustermate monitor. */
	private String clustermateMonitor;
	
	/** The clustermate expiration. */
	private int clustermateExpiration;
	
	/** The error. */
	private String error;
	
	/** The send buffer. */
	private int sendBuffer;
	
	/** The receive buffer. */
	private int receiveBuffer;
	
	/** The client cache max. */
	private int clientCacheMax;
	
	/** The compression enabled. */
	private boolean compressionEnabled;


	/** The proxy backend host. */
	private String proxyBackendHost;
	
	/** The proxy backend port. */
	private int proxyBackendPort;



	/** The username. */
	//server side Id used for generating sessions.
	private String username;
	
	/** The password. */
	private String password;
	
	/** The thread count. */
	private int threadCount;



	/** The event loop threads. */
	private int eventLoopThreads;
	
	/** The valid. */
	private boolean valid;
	
	/** The allowed origins. */
	private List<String> allowedOrigins;
	
	/** The props. */
	private Properties props;
	
	
	private String charset;

	/** The config. */
	private static IConfig config = new Config();


	/**
	 * Instantiates a new config.
	 */
	//private constructor / singleton
	private Config(){
		this.run();
	}

	/**
	 * Gets the single instance of Config.
	 *
	 * @return single instance of Config
	 */
	public static IConfig getInstance(){
		return config;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isAllowAnonymous()
	 */
	@Override
	public boolean isAllowAnonymous() {
		return allowAnonymous;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isBroadcastServer()
	 */
	@Override
	public boolean isBroadcastServer(){
		return ServerInfo.getInstance().getServerName().equals(this.getBroadcastServer());
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isEncrypted()
	 */
	@Override
	public boolean isEncrypted() {
		return encrypted;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getPort()
	 */
	@Override
	public int getPort() {
		return port;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getKeyStore()
	 */
	@Override
	public String getKeyStore() {
		return keyStore;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getKeyStorePassword()
	 */
	@Override
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getKeyPassword()
	 */
	@Override
	public String getKeyPassword() {
		return keyPassword;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isDebug()
	 */
	@Override
	public boolean isDebug(){
		return debug;
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getKeyStoreType()
	 */
	@Override
	public String getKeyStoreType(){
		return this.keyStoreType;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getWebsocketFilter()
	 */
	@Override
	public String getWebsocketFilter(){
		return this.websocketFilter;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isTestMode()
	 */
	@Override
	public boolean isTestMode(){
		return this.testMode;
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#print(java.lang.Object)
	 */
	@Override
	public void print(Object o){
		System.out.println("xockets.io service: " + o);
	}


	

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		//create a trsuted session

		print("Opening trusted session to gain access to the config document.");
		Session s = SessionFactory.openTrusted();

		StringBuilder sb = new StringBuilder(300);

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
				print("XPagesPreload=1 is required in the notes.ini");
				return;
			}


			String preload = this.env(s, "XPagesPreloadDB", StringCache.EMPTY);
			if(!preload.startsWith(Const.WEBSOCKET_PATH)){
				print("XPagesPreloadDB=websocket.nsf is required in the notes.ini.  Make sure websocket.nsf is the first parameter.");
				return;
			}


			Database db = s.getDatabase(StrUtils.EMPTY_STRING, Const.WEBSOCKET_PATH);
			if (db == null || !db.isOpen()){
				print(Const.WEBSOCKET_PATH +  " is required!");
				return;
			}

			//check to see if the config data is stored in the websocket.nsf
			Document docConfig = db.getProfileDocument(Const.PROFILE_FORM, Const.PROFILE_KEY);


			if (!StrUtils.isEmpty(docConfig.getItemValueString("config"))){
				this.props = XSPUtils.loadProps(docConfig, Const.PROFILE_FIELD);
				print("Loading websocket configuration from " + Const.WEBSOCKET_PATH);
			}else{
				print("Loading websocket configuration from notes.ini");
			}


			//default to empty and zero.
			this.proxyBackendHost = env(s,Params.WEBSOCKET_PROXY_BACKEND_HOST.name(),"");
			this.proxyBackendPort=envAsInt(s,Params.WEBSOCKET_PROXY_BACKEND_PORT.name(),0);
			if(this.isProxy()){
				print("INFO: Websocket server will proxy http/s requests for " + this.proxyBackendHost + ":" + this.proxyBackendPort);
			}

			this.redirectPorts = this.envAsIntegerList(s,Params.WEBSOCKET_REDIRECT_PORTS.name(), StringCache.EMPTY);

			
			this.charset = this.env(s, Params.WEBSOCKET_CHARSET.name(), Charset.defaultCharset().toString());

			//setup the creds used for server-side operations.
			this.username = env(s,Params.WEBSOCKET_USER.name(),"");
			this.password=env(s,Params.WEBSOCKET_PASSWORD.name(),"");

			if(StrUtils.hasEmpty(username, password)){
				print("****WARNING: WEBSOCKET_USER and WEBSOCKET_PASSWORD are not setup.  xockets.io will run under a trusted session.****");
			}else{
				print("xockets.io operations will run under userId " + this.username);
			}


			this.leakDetector=envAsBool(s,Params.WEBSOCKET_LEAK_DETECTOR.name(),false);
			if(this.isLeakDetector()){
				print ("****WARNING: Leak detection is turned on.  This may impact performance.****");
			}


			this.nativeTransport=envAsBool(s,Params.WEBSOCKET_NATIVE_TRANSPORT.name(),false);
			if(this.nativeTransport && !SystemUtils.IS_OS_LINUX){
				print("****WARNING: Native transport only available on Linux based machines. Reverting to NIO transport.****");
				this.nativeTransport=false;
			}

			this.onServer = s.isOnServer();

			this.sendBuffer=envAsInt(s,Params.WEBSOCKET_SEND_BUFFER.name(),Const.WEBSOCKET_SEND_BUFFER);
			print("Send buffer is " + sendBuffer + " bytes");
			

			this.receiveBuffer=envAsInt(s,Params.WEBSOCKET_RECEIVE_BUFFER.name(),Const.WEBSOCKET_RECEIVE_BUFFER);
			print("Receive buffer is " + this.receiveBuffer + " bytes");
			


			this.allowedOrigins = envAsList(s,Params.WEBSOCKET_ALLOWED_ORIGINS.name(), Const.WEBSOCKET_ALLOWED_ORIGINS);
			if (allowedOrigins.contains(StringCache.STAR)){
				print ("****WARNING: All origins are allowed. Not recommended****");
			}else{
				print("Allowed origins are " + allowedOrigins.toString());
			}

			this.profiled = envAsBool(s, Params.WEBSOCKET_PROFILED.name(), false);
			if(this.isProfiled()){
				print("****WARNING: Profiling enabled on the websocket server****");
			}

			//get the thread count
			this.eventLoopThreads = envAsInt(s,Params.WEBSOCKET_EVENT_LOOP_THREADS.name(), Const.WEBSOCKET_EVENT_LOOP_THREADS);
			print("Loading " + this.eventLoopThreads + " event loop thread(s).");


			this.threadCount = envAsInt(s,Params.WEBSOCKET_THREAD_COUNT.name(), Const.WEBSOCKET_THREAD_COUNT);
			print("Loading " + this.threadCount + " other background thread(s).");

			//set the ping interval, default to 60 seconds
			this.pingInterval = envAsInt(s,Params.WEBSOCKET_PING_INTERVAL.name(),Const.WEBSOCKET_PING_INTERVAL);

			//run the purge every 900 seconds (15 minutes)
			this.purgeInterval = envAsInt(s,Params.WEBSOCKET_PURGE_INTERVAL.name(), Const.WEBSOCKET_PURGE_INTERVAL);
			print ("Purge interval is " + purgeInterval + " seconds.");

			this.compressionEnabled = envAsBool(s,Params.WEBSOCKET_COMPRESSION_ENABLED.name(),Const.WEBSOCKET_COMPRESSION_ENABLED);
			if(compressionEnabled){
				print("Compression enabled.");
			}else{
				print("Compression disabled");
			}

			this.clientCacheMax= envAsInt(s,Params.WEBSOCKET_CLIENT_CACHE_MAX.name(), 1000);
			this.port= envAsInt(s,Params.WEBSOCKET_PORT.name(), 8889);

			this.maxConnections=envAsInt(s,Params.WEBSOCKET_MAX_CONNECTIONS.name(), Const.WEBSOCKET_MAX_CONNECTIONS);
			print ("Maximum connections " + maxConnections);

			this.maxSize = envAsLong(s, Params.WEBSOCKET_MAX_MSG_SIZE.name(), Const.WEBSOCKET_MAX_MSG_SIZE);
			if(maxSize < Const.WEBSOCKET_MAX_MSG_SIZE){
				print("****WARNING: Max message size of " + maxSize + " is less than the default of " + Const.WEBSOCKET_MAX_MSG_SIZE + " bytes");
			}


			this.encrypted=new Boolean(this.envAsBool(s, Params.WEBSOCKET_ENCRYPT.name(), Const.WEBSOCKET_ENCRYPT));
			if(encrypted){	
				
				//certificate file and keyfile.
				this.certFile = env(s,Params.WEBSOCKET_CERT_FILE.name(),"");
				this.keyFile=env(s,Params.WEBSOCKET_KEY_FILE.name(),"");
				
				//set all the keystore attributes
				this.keyStore=env(s, Params.WEBSOCKET_KEYSTORE_PATH.name(), "");
				this.keyPassword=env(s,Params.WEBSOCKET_KEY_PASSWORD.name(),"");
				this.keyStorePassword=env(s,Params.WEBSOCKET_KEYSTORE_PASSWORD.name(),"");
				this.keyStoreType=env(s,Params.WEBSOCKET_KEYSTORE_TYPE.name(),"");
				
				
				if(StrUtils.areEmpty(certFile, keyFile, keyStore,keyPassword,keyStorePassword,keyStoreType)){
					sb.append("****ERROR: SSL/TLS encryption is enabled, but neither Keystore nor cert/key files are configured.  xockets.io will not run.****");
				}
				
			}else{
				print("****WARNING: Websocket connections not encrypted.****");
			}


			//these config options are used in a clustered environment.
			this.clustered=new Boolean(envAsBool(s,Params.WEBSOCKET_CLUSTERED.name(), Const.WEBSOCKET_CLUSTERED));
			if(clustered){
				this.clustermateMonitor=env(s,Params.WEBSOCKET_CLUSTERMATE_MONITOR.name(), "");
				this.clustermateExpiration= this.envAsInt(s, "WEBSOCKET_CLUSTERMATE_EXPIRATION", 300);

				if(StrUtils.isEmpty(clustermateMonitor) || this.clustermateExpiration==-1){
					sb.append("\nif clustering:\n WEBSOCKET_CLUSTERMATE_MONITOR\nWEBSOCKET_CLUSTERMATE_EXPIRATION\nare required.");
				}

				if(ServerInfo.getInstance().isCurrentServer(clustermateMonitor)){
					sb.append("WEBSOCKET_CLUSTERMATE_MONITOR must point to a different server in the cluster.  It cannot point to itself.");
				}
			}

			this.broadcastServer=env(s, Params.WEBSOCKET_BROADCAST_SERVER.name(), ServerInfo.getInstance().getServerName());

			//other settings
			this.debug=envAsBool(s,Params.WEBSOCKET_DEBUG.name(), Const.WEBSOCKET_DEBUG);
			if(this.isDebug()){
				print("****WARNING: In debug mode.  Logs will be flooded.****");
			}

			this.allowAnonymous=envAsBool(s,Params.WEBSOCKET_ALLOW_ANONYMOUS.name(), Const.WEBSOCKET_ALLOW_ANONYMOUS);
			if(this.allowAnonymous){
				print("****WARNING: Anonymous access is allowed.****");
			}

			this.testMode=envAsBool(s,Params.WEBSOCKET_TEST_MODE.name(), Const.WEBSOCKET_TEST_MODE);
			if(this.isTestMode()){
				print("****WARNING: In test mode****");
			}


			this.websocketFilter=env(s,Params.WEBSOCKET_FILTER.name(),null);
			if(this.websocketFilter == null){
				print("No websocket filter registered.");
			}


			if(sb.length()==0){
				valid = true;
			}else{
				this.error = sb.toString();
			}

		} catch (NotesException e) {
			LOGGER.log(Level.SEVERE,null,e);

		} catch(Exception e){
			LOGGER.log(Level.SEVERE,null, e);

		} finally{
			SessionFactory.closeSession(s);
		}

	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getEventLoopThreads()
	 */
	@Override
	public int getEventLoopThreads() {
		return eventLoopThreads;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isValid()
	 */
	@Override
	public boolean isValid(){
		return valid;
	}

	/**
	 * Gets the value.
	 *
	 * @param s the s
	 * @param key the key
	 * @return the value
	 * @throws NotesException the notes exception
	 */
	private String getValue(Session s, String key) throws NotesException{
		String str=null;
		if(this.props!=null){
			str = props.getProperty(key);
		}else{
			str = s.getEnvironmentString(key,true);
		}
		return str;
	}

	/**
	 * Env as bool.
	 *
	 * @param s the s
	 * @param key the key
	 * @param defaultValue the default value
	 * @return true, if successful
	 * @throws NotesException the notes exception
	 */
	private boolean envAsBool(Session s, String key, boolean defaultValue) throws NotesException{
		String str=this.getValue(s, key);
		if(!StrUtils.isEmpty(str)){
			return new Boolean(str);
		}
		return defaultValue;
	}

	/**
	 * Env.
	 *
	 * @param s the s
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the string
	 * @throws NotesException the notes exception
	 */
	private String env(Session s, String key, String defaultValue) throws NotesException{
		String str= this.getValue(s,key);
		if(StrUtils.isEmpty(str)){
			return defaultValue;
		}
		return str;
	}

	/**
	 * Env as list.
	 *
	 * @param s the s
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the list
	 * @throws NotesException the notes exception
	 */
	private List<String> envAsList(Session s, String key, String defaultValue) throws NotesException{
		String str=this.getValue(s,key);
		
		//if both values are empty... return an empty ArrayList.
		if(StrUtils.areEmpty(str, defaultValue)){
			return new ArrayList<String>();
			
		}else if(StrUtils.isEmpty(str)){
			str = defaultValue;
		}
		
		
		String[] arr = str.split(StringCache.COMMA);
		List<String> list = new ArrayList<String>();
		for(String value : arr){
			list.add(value.trim());
		}
		return list;
	}

	/**
	 * Env as integer list.
	 *
	 * @param s the s
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the list
	 * @throws NotesException the notes exception
	 */
	private List<Integer> envAsIntegerList(Session s, String key, String defaultValue) throws NotesException{
		List<String> list = this.envAsList(s, key, defaultValue);
		List<Integer> ilist = new ArrayList<Integer>();
		for(String str : list){
			ilist.add(Integer.parseInt(str));
		}
		return ilist;
	}

	/**
	 * Env as int.
	 *
	 * @param s the s
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the int
	 * @throws NotesException the notes exception
	 */
	private int envAsInt(Session s, String key, int defaultValue) throws NotesException{
		String str= this.getValue(s,key);
		if(StrUtils.isEmpty(str)){
			return defaultValue;
		}
		return Integer.parseInt(str);
	}

	/**
	 * Env as long.
	 *
	 * @param s the s
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the long
	 * @throws NotesException the notes exception
	 */
	private long envAsLong(Session s, String key, long defaultValue) throws NotesException{
		String str= this.getValue(s, key);
		if(StrUtils.isEmpty(str)){
			return defaultValue;
		}
		return Long.parseLong(str);
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isOnServer()
	 */
	@Override
	public boolean isOnServer() {
		return onServer;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#setOnServer(boolean)
	 */
	@Override
	public void setOnServer(boolean onServer) {
		this.onServer = onServer;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getBroadcastServer()
	 */
	@Override
	public String getBroadcastServer() {
		return broadcastServer;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getClustermateMonitor()
	 */
	@Override
	public String getClustermateMonitor(){
		return this.clustermateMonitor;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getClustermateExpiration()
	 */
	@Override
	public int getClustermateExpiration() {
		return clustermateExpiration;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isClustered()
	 */
	@Override
	public boolean isClustered(){
		return this.clustered;
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getError()
	 */
	@Override
	public String getError(){
		return this.error;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getMaxSize()
	 */
	@Override
	public long getMaxSize() {
		return maxSize;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getMaxConnections()
	 */
	@Override
	public int getMaxConnections() {
		return maxConnections;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#setMaxConnections(int)
	 */
	@Override
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getPingInterval()
	 */
	@Override
	public int getPingInterval() {
		return pingInterval;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getThreadCount()
	 */
	@Override
	public int getThreadCount() {
		return threadCount;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#setThreadCount(int)
	 */
	@Override
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getPurgeInterval()
	 */
	@Override
	public int getPurgeInterval() {
		return purgeInterval;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#setPurgeInterval(int)
	 */
	@Override
	public void setPurgeInterval(int purgeInterval) {
		this.purgeInterval = purgeInterval;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getAllowedOrigins()
	 */
	@Override
	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isAllowedOrigin(java.lang.String)
	 */
	@Override
	public boolean isAllowedOrigin(String origin){
		boolean b = false;
		URI uri=null;
		try {
			uri = new URI(origin);
			b=this.getAllowedOrigins().contains(uri.getHost()) || this.getAllowedOrigins().contains(StringCache.STAR);
		} catch (URISyntaxException e) {
			LOGGER.log(Level.SEVERE, null, e);
		}
		return b;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isProfiled()
	 */
	@Override
	public boolean isProfiled() {
		return profiled;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getPassword()
	 */
	@Override
	public String getPassword() {
		return password;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getClientCacheMax()
	 */
	@Override
	public int getClientCacheMax() {
		return this.clientCacheMax;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isCompressionEnabled()
	 */
	@Override
	public boolean isCompressionEnabled() {
		return compressionEnabled;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getSendBuffer()
	 */
	@Override
	public int getSendBuffer() {
		return sendBuffer;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getReceiveBuffer()
	 */
	@Override
	public int getReceiveBuffer() {
		return receiveBuffer;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isNativeTransport()
	 */
	@Override
	public boolean isNativeTransport(){
		return this.nativeTransport;
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getProxyBackendPort()
	 */
	@Override
	public int getProxyBackendPort() {
		return this.proxyBackendPort;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getProxyBackendHost()
	 */
	@Override
	public String getProxyBackendHost() {
		return this.proxyBackendHost;
	}



	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isLeakDetector()
	 */
	@Override
	public boolean isLeakDetector() {
		return leakDetector;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isProxy()
	 */
	@Override
	public boolean isProxy() {
		return !StrUtils.isEmpty(this.getProxyBackendHost()) && this.getProxyBackendPort() > 0;
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getRedirectPorts()
	 */
	@Override
	public List<Integer> getRedirectPorts() {
		return this.redirectPorts;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getKeyFile()
	 */
	@Override
	public String getKeyFile() {
		return this.keyFile;
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getCertFile()
	 */
	@Override
	public String getCertFile() {
		return this.certFile;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isKeyStore()
	 */
	@Override
	public boolean isKeyStore(){
		return StrUtils.hasEmpty(this.getCertFile(), this.getKeyFile());
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#isEmptyCredentials()
	 */
	@Override
	public boolean isEmptyCredentials() {
		return StrUtils.hasEmpty(this.getUsername(),this.getPassword());
	}

	
	 
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#getVersion()
	 */
	@Override
	public String getVersion() {
		return "";
	}

	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.IConfig#property(java.lang.String)
	 */
	@Override
	public String property(String key) {
		return this.props.getProperty(key);
	}

	@Override
	public Charset getCharSet() {
		return Charset.forName(this.charset);
	}

}
