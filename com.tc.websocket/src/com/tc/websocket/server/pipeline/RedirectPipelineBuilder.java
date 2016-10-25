/*
 * 
 */
package com.tc.websocket.server.pipeline;

import com.tc.websocket.server.handler.RedirectionHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;


// TODO: Auto-generated Javadoc
/**
 * The Class RedirectPipelineBuilder.
 */
public class RedirectPipelineBuilder implements IPipelineBuilder {

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.pipeline.IPipelineBuilder#apply(io.netty.channel.ChannelPipeline)
	 */
	@Override
	public void apply(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new RedirectionHandler());
		
	}

}
