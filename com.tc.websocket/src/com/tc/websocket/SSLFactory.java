package com.tc.websocket;

import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;


public class SSLFactory implements ISSLFactory {

	private SslContext sslCtx = null;

	private static final Logger logger= Logger.getLogger(SSLFactory.class.getName());
	public SSLFactory(){

	}	
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
			logger.log(Level.SEVERE, null, e);
		}
		return ctx;
	}


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
			logger.log(Level.SEVERE, null, e);
		}

		return ctx;
	}


	@Override
	public SslContext createInsecureClientSslCtx(IConfig cfg) {
		SslContext clientCtx = null;
		try{
			clientCtx = SslContextBuilder.forClient()
					.sslProvider(SslProvider.JDK)
					.trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
		}catch(Exception e){
			logger.log(Level.SEVERE, null, e);
		}
		return clientCtx;
	}


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
			logger.log(Level.SEVERE, null, e);
		}
		return clientCtx;
	}

}
