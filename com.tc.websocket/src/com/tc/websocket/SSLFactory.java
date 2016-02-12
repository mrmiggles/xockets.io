package com.tc.websocket;

import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLFactory implements ISSLFactory {
	
	
	public SSLFactory(){
		
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.ISSLFactory#createSSLSocket()
	 */
	@Override
	public Socket createSSLSocket(IConfig cfg) {
		Socket socket = null;
		try{
			SSLSocketFactory factory = this.createSSLContext().getSocketFactory();// (SSLSocketFactory) SSLSocketFactory.getDefault();
			socket = factory.createSocket();
		}catch(Exception e){
			Logger.getLogger(SSLFactory.class.getName()).log(Level.SEVERE,null,e);
		}

		return socket;
	}
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.ISSLFactory#createSSLContext()
	 */
	@Override
	public SSLContext createSSLContext(IConfig cfg){
		SSLContext sslContext = null;
		try{
			KeyStore ks = KeyStore.getInstance( cfg.getKeyStoreType() );
			File kf = new File( cfg.getKeyStore() );
			ks.load( new FileInputStream( kf ), cfg.getKeyStorePassword().toCharArray() );

			KeyManagerFactory kmf = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
			kmf.init( ks, cfg.getKeyPassword().toCharArray() );
			
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
			tmf.init( ks );

		
			sslContext = SSLContext.getInstance( "TLS" );
			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
			
		}catch(Exception e){
			Logger.getLogger(SSLFactory.class.getName()).log(Level.SEVERE,null,e);
		}
		return sslContext;
	}


	@Override
	public Socket createSSLSocket() {
		System.out.println("using default config");
		return this.createSSLSocket(Config.getInstance());
	}


	@Override
	public SSLContext createSSLContext() {
		System.out.println("using default config");
		return this.createSSLContext(Config.getInstance());
	}






}
