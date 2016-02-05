package com.tc.websocket;

import java.net.Socket;

import javax.net.ssl.SSLContext;

public interface ISSLFactory {

	public abstract Socket createSSLSocket();

	public abstract SSLContext createSSLContext();

}