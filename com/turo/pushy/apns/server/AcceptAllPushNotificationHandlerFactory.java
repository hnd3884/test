package com.turo.pushy.apns.server;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Headers;
import javax.net.ssl.SSLSession;

public class AcceptAllPushNotificationHandlerFactory implements PushNotificationHandlerFactory
{
    @Override
    public PushNotificationHandler buildHandler(final SSLSession sslSession) {
        return new PushNotificationHandler() {
            @Override
            public void handlePushNotification(final Http2Headers headers, final ByteBuf payload) {
            }
        };
    }
}
