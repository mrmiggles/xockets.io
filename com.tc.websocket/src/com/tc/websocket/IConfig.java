package com.tc.websocket;

import java.util.List;

public interface IConfig {
	
	
	public abstract int getProxyBackendPort();
	
	public abstract String getProxyBackendHost();
	
	public abstract boolean isProxy();

	public abstract boolean isAllowAnonymous();

	public abstract boolean isBroadcastServer();

	public abstract boolean isEncrypted();

	public abstract int getPort();
	
	public abstract List<Integer> getRedirectPorts();
	
	public abstract String getKeyStore();

	public abstract String getKeyStorePassword();

	public abstract String getKeyPassword();

	public abstract boolean isDebug();

	public abstract String getKeyStoreType();

	public abstract String getWebsocketFilter();

	public abstract boolean isTestMode();

	public abstract void print(Object o);

	public abstract void run();
	
	public abstract boolean isNativeTransport();

	public abstract int getEventLoopThreads();

	public abstract boolean isValid();

	public abstract boolean isOnServer();

	public abstract void setOnServer(boolean onServer);

	public abstract String getBroadcastServer();

	public abstract String getClustermateMonitor();

	public abstract int getClustermateExpiration();

	public abstract boolean isClustered();

	public abstract String getError();

	public abstract long getMaxSize();

	public abstract int getMaxConnections();

	public abstract void setMaxConnections(int maxConnections);

	public abstract int getPingInterval();

	public abstract int getThreadCount();

	public abstract void setThreadCount(int threadCount);

	public abstract int getPurgeInterval();

	public abstract void setPurgeInterval(int purgeInterval);

	public abstract List<String> getAllowedOrigins();

	public abstract boolean isAllowedOrigin(String origin);

	public abstract boolean isProfiled();

	public abstract String getUsername();

	public abstract String getPassword();

	public abstract int getClientCacheMax();

	public abstract boolean isCompressionEnabled();

	public abstract int getSendBuffer();

	public abstract int getReceiveBuffer();
	
	public boolean isLeakDetector();
	
	public String getKeyFile();
	
	public String getCertFile();
	
	public boolean isKeyStore();
	
	public boolean isEmptyCredentials();
	
	public String getVersion();

}