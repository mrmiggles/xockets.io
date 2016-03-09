/*
 * Copyright 2016 Tek Counsel LLC
 *
 * Both licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.tc.websocket.server.handler;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;

import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.IConfig;


@ChannelHandler.Sharable
public class RedirectionHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req)throws Exception {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);

		HttpHeaders headers = req.headers();

		IConfig cfg = Config.getInstance();

		StringBuilder sb = new StringBuilder();

		if (cfg.isEncrypted()) {
			sb.append(StringCache.HTTPS);
		} else {
			sb.append(StringCache.HTTP);
		}
		
		//finish up the url.
		sb.append(headers.get(HttpHeaderNames.HOST)).append(StringCache.COLON).append(cfg.getPort()).append(req.uri());

		//apply the redirect url
		response.headers().set(HttpHeaderNames.LOCATION, sb.toString());

		// Close the connection as soon as the redirect is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

}
