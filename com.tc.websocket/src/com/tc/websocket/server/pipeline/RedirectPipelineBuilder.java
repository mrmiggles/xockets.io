package com.tc.websocket.server.pipeline;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import com.tc.websocket.server.handler.RedirectionHandler;

public class RedirectPipelineBuilder implements IPipelineBuilder {

	@Override
	public void apply(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new RedirectionHandler());
		
	}

}
