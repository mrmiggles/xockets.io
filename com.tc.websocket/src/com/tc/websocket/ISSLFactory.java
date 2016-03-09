package com.tc.websocket;

import io.netty.handler.ssl.SslContext;

public interface ISSLFactory {

	public abstract SslContext createSslContext(IConfig cfg);
	public abstract SslContext createClientSslCtx(IConfig cfg);
	public abstract SslContext createInsecureClientSslCtx(IConfig cfg);
}