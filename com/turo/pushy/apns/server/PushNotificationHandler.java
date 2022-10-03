package com.turo.pushy.apns.server;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Headers;

public interface PushNotificationHandler
{
    void handlePushNotification(final Http2Headers p0, final ByteBuf p1) throws RejectedNotificationException;
}
