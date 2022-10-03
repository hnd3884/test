package com.turo.pushy.apns.server;

import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.channel.ChannelPipeline;
import javax.net.ssl.SSLSession;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;

public class MockApnsServer extends BaseHttp2Server
{
    private final PushNotificationHandlerFactory handlerFactory;
    private final MockApnsServerListener listener;
    private final int maxConcurrentStreams;
    
    MockApnsServer(final SslContext sslContext, final EventLoopGroup eventLoopGroup, final PushNotificationHandlerFactory handlerFactory, final MockApnsServerListener listener, final int maxConcurrentStreams) {
        super(sslContext, eventLoopGroup);
        this.handlerFactory = handlerFactory;
        this.listener = listener;
        this.maxConcurrentStreams = maxConcurrentStreams;
    }
    
    @Override
    protected void addHandlersToPipeline(final SSLSession sslSession, final ChannelPipeline pipeline) throws Exception {
        final PushNotificationHandler pushNotificationHandler = this.handlerFactory.buildHandler(sslSession);
        final MockApnsServerHandler serverHandler = new MockApnsServerHandler.MockApnsServerHandlerBuilder().pushNotificationHandler(pushNotificationHandler).initialSettings(Http2Settings.defaultSettings().maxConcurrentStreams((long)this.maxConcurrentStreams)).listener(this.listener).build();
        pipeline.addLast(new ChannelHandler[] { (ChannelHandler)serverHandler });
    }
}
