package com.turo.pushy.apns.server;

import io.netty.handler.codec.http2.Http2Headers;
import java.util.HashSet;
import java.util.Objects;
import java.util.Date;
import java.util.Map;
import io.netty.util.AsciiString;
import java.util.Set;

class TlsAuthenticationValidatingPushNotificationHandler extends ValidatingPushNotificationHandler
{
    private final Set<String> allowedTopics;
    private static final AsciiString APNS_TOPIC_HEADER;
    
    TlsAuthenticationValidatingPushNotificationHandler(final Map<String, Set<String>> deviceTokensByTopic, final Map<String, Date> expirationTimestampsByDeviceToken, final String baseTopic) {
        super(deviceTokensByTopic, expirationTimestampsByDeviceToken);
        Objects.requireNonNull(baseTopic, "Base topic must not be null for mock server handlers using TLS-based authentication.");
        (this.allowedTopics = new HashSet<String>()).add(baseTopic);
        this.allowedTopics.add(baseTopic + ".voip");
        this.allowedTopics.add(baseTopic + ".complication");
    }
    
    @Override
    protected void verifyAuthentication(final Http2Headers headers) throws RejectedNotificationException {
        final CharSequence topicSequence = (CharSequence)headers.get((Object)TlsAuthenticationValidatingPushNotificationHandler.APNS_TOPIC_HEADER);
        final String topic = (topicSequence != null) ? topicSequence.toString() : null;
        if (!this.allowedTopics.contains(topic)) {
            throw new RejectedNotificationException(RejectionReason.BAD_TOPIC);
        }
    }
    
    static {
        APNS_TOPIC_HEADER = new AsciiString((CharSequence)"apns-topic");
    }
}
