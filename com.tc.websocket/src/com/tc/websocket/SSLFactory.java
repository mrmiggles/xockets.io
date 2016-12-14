/*
 * 
 */
package com.tc.websocket;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;



// TODO: Auto-generated Javadoc
/**
 * A factory for creating SSL objects.
 */
public class SSLFactory implements ISSLFactory {

	/** The ssl ctx. */
	private SslContext sslCtx;

	/** The Constant LOG. */
	private static final Logger LOG= Logger.getLogger(SSLFactory.class.getName());
	
	/**
	 * Instantiates a new SSL factory.
	 */
	public SSLFactory(){

	}	
	
	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.ISSLFactory#createSslContext(com.tc.websocket.IConfig)
	 */
	@Override
	public synchronized SslContext createSslContext(IConfig cfg) {
		if(sslCtx ==null){
			
			if(cfg.isKeyStore()){
				sslCtx= this.createSslContextWithKeyStore(cfg,true);
			}else{
				sslCtx= this.createServerSslContext(cfg);
			}

		}
		return sslCtx;
	}

	/**
	 * Creates a new SSL object.
	 *
	 * @param cfg the cfg
	 * @param server the server
	 * @return the ssl context
	 */
	private synchronized SslContext createSslContextWithKeyStore(IConfig cfg, boolean server){
		SslContext ctx = null;
		try{
			KeyStore ks = KeyStore.getInstance( cfg.getKeyStoreType() );
			File kf = new File( cfg.getKeyStore() );
			ks.load( new FileInputStream( kf ), cfg.getKeyStorePassword().toCharArray() );

			KeyManagerFactory kmf = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
			kmf.init( ks, cfg.getKeyPassword().toCharArray() );

			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
			tmf.init( ks );


			if(server){
				ctx = SslContextBuilder
						.forServer(kmf)
						.trustManager(tmf)
						.sslProvider(SslProvider.JDK)
						.build();
			}else{
				ctx = SslContextBuilder
						.forClient()
						.trustManager(tmf)
						.sslProvider(SslProvider.JDK)
						.build();
			}


		}catch(Exception e){
			LOG.log(Level.SEVERE, null, e);
		}
		return ctx;
	}


	/**
	 * Creates a new SslContext object.
	 *
	 * @param cfg the cfg
	 * @return the ssl context
	 */
	private synchronized SslContext createServerSslContext(IConfig cfg){
		SslContext ctx = null;
		try{
				SslProvider provider = OpenSsl.isAlpnSupported() ? SslProvider.OPENSSL : SslProvider.JDK;
				
				if(provider.equals(SslProvider.OPENSSL)){
					cfg.print("Using OpenSSL for network encryption.");
				}
				
				ctx = SslContextBuilder
						.forServer(new File(cfg.getCertFile()), new File(cfg.getKeyFile()), cfg.getKeyPassword())
						.sslProvider(provider)
						.build();
				
		}catch(Exception e){
			LOG.log(Level.SEVERE, null, e);
		}

		return ctx;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.ISSLFactory#createInsecureClientSslCtx(com.tc.websocket.IConfig)
	 */
	@Override
	public SslContext createInsecureClientSslCtx(IConfig cfg) {
		SslContext clientCtx = null;
		try{
			clientCtx = SslContextBuilder.forClient()
					.sslProvider(SslProvider.JDK)
					.trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
		}catch(Exception e){
			LOG.log(Level.SEVERE, null, e);
		}
		return clientCtx;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.ISSLFactory#createClientSslCtx(com.tc.websocket.IConfig)
	 */
	@Override
	public synchronized SslContext createClientSslCtx(IConfig cfg){

		SslContext clientCtx = null;
		try{
			if(cfg.isKeyStore()){
				clientCtx = this.createSslContextWithKeyStore(cfg, false);
			}else{
				clientCtx = this.createInsecureClientSslCtx(cfg);
			}
		}catch(Exception e){
			LOG.log(Level.SEVERE, null, e);
		}
		return clientCtx;
	}

}
