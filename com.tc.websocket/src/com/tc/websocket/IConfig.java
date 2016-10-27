/*
 * 
 */
package com.tc.websocket;

import java.nio.charset.Charset;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Interface IConfig.
 */
public interface IConfig {
	
	
	/**
	 * Gets the proxy backend port.
	 *
	 * @return the proxy backend port
	 */
	public abstract int getProxyBackendPort();
	
	/**
	 * Gets the proxy backend host.
	 *
	 * @return the proxy backend host
	 */
	public abstract String getProxyBackendHost();
	
	/**
	 * Checks if is proxy.
	 *
	 * @return true, if is proxy
	 */
	public abstract boolean isProxy();

	/**
	 * Checks if is allow anonymous.
	 *
	 * @return true, if is allow anonymous
	 */
	public abstract boolean isAllowAnonymous();

	/**
	 * Checks if is broadcast server.
	 *
	 * @return true, if is broadcast server
	 */
	public abstract boolean isBroadcastServer();

	/**
	 * Checks if is encrypted.
	 *
	 * @return true, if is encrypted
	 */
	public abstract boolean isEncrypted();

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public abstract int getPort();
	
	/**
	 * Gets the redirect ports.
	 *
	 * @return the redirect ports
	 */
	public abstract List<Integer> getRedirectPorts();
	
	/**
	 * Gets the key store.
	 *
	 * @return the key store
	 */
	public abstract String getKeyStore();

	/**
	 * Gets the key store password.
	 *
	 * @return the key store password
	 */
	public abstract String getKeyStorePassword();

	/**
	 * Gets the key password.
	 *
	 * @return the key password
	 */
	public abstract String getKeyPassword();

	/**
	 * Checks if is debug.
	 *
	 * @return true, if is debug
	 */
	public abstract boolean isDebug();

	/**
	 * Gets the key store type.
	 *
	 * @return the key store type
	 */
	public abstract String getKeyStoreType();

	/**
	 * Gets the websocket filter.
	 *
	 * @return the websocket filter
	 */
	public abstract String getWebsocketFilter();

	/**
	 * Checks if is test mode.
	 *
	 * @return true, if is test mode
	 */
	public abstract boolean isTestMode();

	/**
	 * Prints the.
	 *
	 * @param o the o
	 */
	public abstract void print(Object o);

	/**
	 * Run.
	 */
	public abstract void run();
	
	/**
	 * Checks if is native transport.
	 *
	 * @return true, if is native transport
	 */
	public abstract boolean isNativeTransport();

	/**
	 * Gets the event loop threads.
	 *
	 * @return the event loop threads
	 */
	public abstract int getEventLoopThreads();

	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public abstract boolean isValid();

	/**
	 * Checks if is on server.
	 *
	 * @return true, if is on server
	 */
	public abstract boolean isOnServer();

	/**
	 * Sets the on server.
	 *
	 * @param onServer the new on server
	 */
	public abstract void setOnServer(boolean onServer);

	/**
	 * Gets the broadcast server.
	 *
	 * @return the broadcast server
	 */
	public abstract String getBroadcastServer();

	/**
	 * Gets the clustermate monitor.
	 *
	 * @return the clustermate monitor
	 */
	public abstract String getClustermateMonitor();

	/**
	 * Gets the clustermate expiration.
	 *
	 * @return the clustermate expiration
	 */
	public abstract int getClustermateExpiration();

	/**
	 * Checks if is clustered.
	 *
	 * @return true, if is clustered
	 */
	public abstract boolean isClustered();

	/**
	 * Gets the error.
	 *
	 * @return the error
	 */
	public abstract String getError();

	/**
	 * Gets the max size.
	 *
	 * @return the max size
	 */
	public abstract long getMaxSize();

	/**
	 * Gets the max connections.
	 *
	 * @return the max connections
	 */
	public abstract int getMaxConnections();

	/**
	 * Sets the max connections.
	 *
	 * @param maxConnections the new max connections
	 */
	public abstract void setMaxConnections(int maxConnections);

	/**
	 * Gets the ping interval.
	 *
	 * @return the ping interval
	 */
	public abstract int getPingInterval();

	/**
	 * Gets the thread count.
	 *
	 * @return the thread count
	 */
	public abstract int getThreadCount();

	/**
	 * Sets the thread count.
	 *
	 * @param threadCount the new thread count
	 */
	public abstract void setThreadCount(int threadCount);

	/**
	 * Gets the purge interval.
	 *
	 * @return the purge interval
	 */
	public abstract int getPurgeInterval();

	/**
	 * Sets the purge interval.
	 *
	 * @param purgeInterval the new purge interval
	 */
	public abstract void setPurgeInterval(int purgeInterval);

	/**
	 * Gets the allowed origins.
	 *
	 * @return the allowed origins
	 */
	public abstract List<String> getAllowedOrigins();

	/**
	 * Checks if is allowed origin.
	 *
	 * @param origin the origin
	 * @return true, if is allowed origin
	 */
	public abstract boolean isAllowedOrigin(String origin);

	/**
	 * Checks if is profiled.
	 *
	 * @return true, if is profiled
	 */
	public abstract boolean isProfiled();

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public abstract String getUsername();

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public abstract String getPassword();

	/**
	 * Gets the client cache max.
	 *
	 * @return the client cache max
	 */
	public abstract int getClientCacheMax();

	/**
	 * Checks if is compression enabled.
	 *
	 * @return true, if is compression enabled
	 */
	public abstract boolean isCompressionEnabled();

	/**
	 * Gets the send buffer.
	 *
	 * @return the send buffer
	 */
	public abstract int getSendBuffer();

	/**
	 * Gets the receive buffer.
	 *
	 * @return the receive buffer
	 */
	public abstract int getReceiveBuffer();
	
	/**
	 * Checks if is leak detector.
	 *
	 * @return true, if is leak detector
	 */
	public boolean isLeakDetector();
	
	/**
	 * Gets the key file.
	 *
	 * @return the key file
	 */
	public String getKeyFile();
	
	/**
	 * Gets the cert file.
	 *
	 * @return the cert file
	 */
	public String getCertFile();
	
	/**
	 * Checks if is key store.
	 *
	 * @return true, if is key store
	 */
	public boolean isKeyStore();
	
	/**
	 * Checks if is empty credentials.
	 *
	 * @return true, if is empty credentials
	 */
	public boolean isEmptyCredentials();
	
	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion();
	
	/**
	 * Property.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String property(String key);
	
	
	public Charset getCharSet();
	
	public boolean isCertAuth();

}