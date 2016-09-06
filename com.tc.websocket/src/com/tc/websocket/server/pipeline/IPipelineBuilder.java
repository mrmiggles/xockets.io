/*
 * 
 */
package com.tc.websocket.server.pipeline;

import io.netty.channel.ChannelPipeline;


// TODO: Auto-generated Javadoc
/**
 * The Interface IPipelineBuilder.
 */
public interface IPipelineBuilder {
	
	/**
	 * Apply.
	 *
	 * @param pipeline the pipeline
	 */
	public abstract void apply(ChannelPipeline pipeline);

}