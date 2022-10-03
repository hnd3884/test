package com.adventnet.iam.security.antivirus.icap;

import ch.mimo.netty.handler.codec.icap.IcapResponseDecoder;
import org.jboss.netty.channel.ChannelHandler;
import ch.mimo.netty.handler.codec.icap.IcapRequestEncoder;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

public class IcapClientChannelPipeline implements ChannelPipelineFactory
{
    public ChannelPipeline getPipeline() throws Exception {
        final ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("encoder", (ChannelHandler)new IcapRequestEncoder());
        pipeline.addLast("chunkfileseperator", (ChannelHandler)new IcapStreamChunkSeperator());
        pipeline.addLast("decoder", (ChannelHandler)new IcapResponseDecoder());
        pipeline.addLast("handler", (ChannelHandler)new IcapResponseHandler());
        return pipeline;
    }
}
