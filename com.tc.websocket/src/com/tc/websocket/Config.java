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

public class Config implements Runnable, IConfig {
	private static Logger LOGGER = Logger.getLogger(Config.class.getName());


	private boolean allowAnonymous;
	private boolean encrypted;
	private String keyStore;
	private String keyStorePassword;
	private String keyPassword;
	private String keyStoreType;
	private int port;
	private List<Integer> redirectPorts;
	private boolean debug;
	private boolean profiled;
	private boolean nativeTransport;

	private String keyFile;
	private String certFile;
	private boolean leakDetector;



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


	private String proxyBackendHost;
	private int proxyBackendPort;



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
		System.out.println("xockets.io service: " + o);
	}


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

	private List<Integer> envAsIntegerList(Session s, String key, String defaultValue) throws NotesException{
		List<String> list = this.envAsList(s, key, defaultValue);
		List<Integer> ilist = new ArrayList<Integer>();
		for(String str : list){
			ilist.add(Integer.parseInt(str));
		}
		return ilist;
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
			LOGGER.log(Level.SEVERE, null, e);
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


	@Override
	public int getProxyBackendPort() {
		return this.proxyBackendPort;
	}

	@Override
	public String getProxyBackendHost() {
		return this.proxyBackendHost;
	}



	@Override
	public boolean isLeakDetector() {
		return leakDetector;
	}

	@Override
	public boolean isProxy() {
		return !StrUtils.isEmpty(this.getProxyBackendHost()) && this.getProxyBackendPort() > 0;
	}



	@Override
	public List<Integer> getRedirectPorts() {
		return this.redirectPorts;
	}

	@Override
	public String getKeyFile() {
		return this.keyFile;
	}

	@Override
	public String getCertFile() {
		return this.certFile;
	}


	@Override
	public boolean isKeyStore(){
		return StrUtils.hasEmpty(this.getCertFile(), this.getKeyFile());
	}

	@Override
	public boolean isEmptyCredentials() {
		return StrUtils.hasEmpty(this.getUsername(),this.getPassword());
	}

	@Override
	public String getVersion() {
		return "";
	}

}
