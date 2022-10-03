package com.turo.pushy.apns.server;

import io.netty.util.concurrent.Future;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import javax.net.ssl.SSLSession;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;

public class BenchmarkApnsServer extends BaseHttp2Server
{
    private final int maxConcurrentStreams;
    
    BenchmarkApnsServer(final SslContext sslContext, final EventLoopGroup eventLoopGroup, final int maxConcurrentStreams) {
        super(sslContext, eventLoopGroup);
        this.maxConcurrentStreams = maxConcurrentStreams;
    }
    
    @Override
    protected void addHandlersToPipeline(final SSLSession sslSession, final ChannelPipeline pipeline) {
        pipeline.addLast(new ChannelHandler[] { (ChannelHandler)new BenchmarkApnsServerHandler.BenchmarkApnsServerHandlerBuilder().initialSettings(Http2Settings.defaultSettings().maxConcurrentStreams((long)this.maxConcurrentStreams)).build() });
    }
}
