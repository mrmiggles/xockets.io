package com.tc.websocket;

import java.net.Socket;

import javax.net.ssl.SSLContext;

public interface ISSLFactory {

	public abstract Socket createSSLSocket();

	public abstract SSLContext createSSLContext();
	
	
	public abstract Socket createSSLSocket(IConfig cfg);

	public abstract SSLContext createSSLContext(IConfig cfg);

}