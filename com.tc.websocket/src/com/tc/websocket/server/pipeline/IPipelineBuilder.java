package com.tc.websocket.server.pipeline;

import io.netty.channel.ChannelPipeline;

public interface IPipelineBuilder {
	
	public abstract void apply(ChannelPipeline pipeline);

}