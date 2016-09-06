/*
 * 
 */
package com.tc.websocket;

import io.netty.handler.ssl.SslContext;


// TODO: Auto-generated Javadoc
/**
 * A factory for creating ISSL objects.
 */
public interface ISSLFactory {

	/**
	 * Creates a new ISSL object.
	 *
	 * @param cfg the cfg
	 * @return the ssl context
	 */
	public abstract SslContext createSslContext(IConfig cfg);
	
	/**
	 * Creates a new ISSL object.
	 *
	 * @param cfg the cfg
	 * @return the ssl context
	 */
	public abstract SslContext createClientSslCtx(IConfig cfg);
	
	/**
	 * Creates a new ISSL object.
	 *
	 * @param cfg the cfg
	 * @return the ssl context
	 */
	public abstract SslContext createInsecureClientSslCtx(IConfig cfg);
}