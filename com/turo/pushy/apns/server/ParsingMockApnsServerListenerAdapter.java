package com.turo.pushy.apns.server;

import org.slf4j.LoggerFactory;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import com.turo.pushy.apns.PushType;
import com.turo.pushy.apns.DeliveryPriority;
import com.eatthepath.uuid.FastUUID;
import java.util.Date;
import com.turo.pushy.apns.ApnsPushNotification;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Headers;
import org.slf4j.Logger;
import io.netty.util.AsciiString;

public abstract class ParsingMockApnsServerListenerAdapter implements MockApnsServerListener
{
    private static final String APNS_PATH_PREFIX = "/3/device/";
    private static final AsciiString APNS_TOPIC_HEADER;
    private static final AsciiString APNS_PRIORITY_HEADER;
    private static final AsciiString APNS_EXPIRATION_HEADER;
    private static final AsciiString APNS_COLLAPSE_ID_HEADER;
    private static final AsciiString APNS_ID_HEADER;
    private static final AsciiString APNS_PUSH_TYPE_HEADER;
    private static final Logger log;
    
    @Override
    public void handlePushNotificationAccepted(final Http2Headers headers, final ByteBuf payload) {
        this.handlePushNotificationAccepted(parsePushNotification(headers, payload));
    }
    
    public abstract void handlePushNotificationAccepted(final ApnsPushNotification p0);
    
    @Override
    public void handlePushNotificationRejected(final Http2Headers headers, final ByteBuf payload, final RejectionReason rejectionReason, final Date deviceTokenExpirationTimestamp) {
        this.handlePushNotificationRejected(parsePushNotification(headers, payload), rejectionReason, deviceTokenExpirationTimestamp);
    }
    
    public abstract void handlePushNotificationRejected(final ApnsPushNotification p0, final RejectionReason p1, final Date p2);
    
    private static ApnsPushNotification parsePushNotification(final Http2Headers headers, final ByteBuf payload) {
        final CharSequence apnsIdSequence = (CharSequence)headers.get((Object)ParsingMockApnsServerListenerAdapter.APNS_ID_HEADER);
        UUID apnsIdFromHeaders;
        try {
            apnsIdFromHeaders = ((apnsIdSequence != null) ? FastUUID.parseUUID(apnsIdSequence) : null);
        }
        catch (final IllegalArgumentException e) {
            ParsingMockApnsServerListenerAdapter.log.error("Failed to parse `apns-id` header: {}", (Object)apnsIdSequence, (Object)e);
            apnsIdFromHeaders = null;
        }
        final UUID apnsId = apnsIdFromHeaders;
        final CharSequence pathSequence = (CharSequence)headers.get((Object)Http2Headers.PseudoHeaderName.PATH.value());
        String deviceToken;
        if (pathSequence != null) {
            final String pathString = pathSequence.toString();
            deviceToken = (pathString.startsWith("/3/device/") ? pathString.substring("/3/device/".length()) : null);
        }
        else {
            deviceToken = null;
        }
        final CharSequence topicSequence = (CharSequence)headers.get((Object)ParsingMockApnsServerListenerAdapter.APNS_TOPIC_HEADER);
        final String topic = (topicSequence != null) ? topicSequence.toString() : null;
        final Integer priorityCode = headers.getInt((Object)ParsingMockApnsServerListenerAdapter.APNS_PRIORITY_HEADER);
        DeliveryPriority priorityFromCode;
        try {
            priorityFromCode = ((priorityCode != null) ? DeliveryPriority.getFromCode(priorityCode) : null);
        }
        catch (final IllegalArgumentException e2) {
            priorityFromCode = null;
        }
        final DeliveryPriority deliveryPriority = priorityFromCode;
        final CharSequence pushTypeSequence = (CharSequence)headers.get((Object)ParsingMockApnsServerListenerAdapter.APNS_PUSH_TYPE_HEADER);
        PushType pushTypeFromHeader;
        try {
            pushTypeFromHeader = ((pushTypeSequence != null) ? PushType.getFromHeaderValue(pushTypeSequence) : null);
        }
        catch (final IllegalArgumentException e3) {
            pushTypeFromHeader = null;
        }
        final PushType pushType = pushTypeFromHeader;
        final Integer expirationTimestamp = headers.getInt((Object)ParsingMockApnsServerListenerAdapter.APNS_EXPIRATION_HEADER);
        final Date expiration = (expirationTimestamp != null) ? new Date(expirationTimestamp * 1000) : null;
        final CharSequence collapseIdSequence = (CharSequence)headers.get((Object)ParsingMockApnsServerListenerAdapter.APNS_COLLAPSE_ID_HEADER);
        final String collapseId = (collapseIdSequence != null) ? collapseIdSequence.toString() : null;
        return new LenientApnsPushNotification(deviceToken, topic, (payload != null) ? payload.toString(StandardCharsets.UTF_8) : null, expiration, deliveryPriority, pushType, collapseId, apnsId);
    }
    
    static {
        APNS_TOPIC_HEADER = new AsciiString((CharSequence)"apns-topic");
        APNS_PRIORITY_HEADER = new AsciiString((CharSequence)"apns-priority");
        APNS_EXPIRATION_HEADER = new AsciiString((CharSequence)"apns-expiration");
        APNS_COLLAPSE_ID_HEADER = new AsciiString((CharSequence)"apns-collapse-id");
        APNS_ID_HEADER = new AsciiString((CharSequence)"apns-id");
        APNS_PUSH_TYPE_HEADER = new AsciiString((CharSequence)"apns-push-type");
        log = LoggerFactory.getLogger((Class)ParsingMockApnsServerListenerAdapter.class);
    }
    
    private static class LenientApnsPushNotification implements ApnsPushNotification
    {
        private final String token;
        private final String payload;
        private final Date invalidationTime;
        private final DeliveryPriority priority;
        private final PushType pushType;
        private final String topic;
        private final String collapseId;
        private final UUID apnsId;
        
        private LenientApnsPushNotification(final String token, final String topic, final String payload, final Date invalidationTime, final DeliveryPriority priority, final PushType pushType, final String collapseId, final UUID apnsId) {
            this.token = token;
            this.payload = payload;
            this.invalidationTime = invalidationTime;
            this.priority = priority;
            this.pushType = pushType;
            this.topic = topic;
            this.collapseId = collapseId;
            this.apnsId = apnsId;
        }
        
        @Override
        public String getToken() {
            return this.token;
        }
        
        @Override
        public String getPayload() {
            return this.payload;
        }
        
        @Override
        public Date getExpiration() {
            return this.invalidationTime;
        }
        
        @Override
        public DeliveryPriority getPriority() {
            return this.priority;
        }
        
        @Override
        public PushType getPushType() {
            return this.pushType;
        }
        
        @Override
        public String getTopic() {
            return this.topic;
        }
        
        @Override
        public String getCollapseId() {
            return this.collapseId;
        }
        
        @Override
        public UUID getApnsId() {
            return this.apnsId;
        }
    }
}
