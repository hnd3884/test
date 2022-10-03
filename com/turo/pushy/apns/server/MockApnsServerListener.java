package com.turo.pushy.apns.server;

import java.util.Date;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Headers;

public interface MockApnsServerListener
{
    void handlePushNotificationAccepted(final Http2Headers p0, final ByteBuf p1);
    
    void handlePushNotificationRejected(final Http2Headers p0, final ByteBuf p1, final RejectionReason p2, final Date p3);
}
