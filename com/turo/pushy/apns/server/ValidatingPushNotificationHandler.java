package com.turo.pushy.apns.server;

import java.util.regex.Matcher;
import com.turo.pushy.apns.PushType;
import com.turo.pushy.apns.DeliveryPriority;
import java.nio.charset.StandardCharsets;
import io.netty.handler.codec.http.HttpMethod;
import com.eatthepath.uuid.FastUUID;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Headers;
import java.util.regex.Pattern;
import io.netty.util.AsciiString;
import java.util.Date;
import java.util.Set;
import java.util.Map;

abstract class ValidatingPushNotificationHandler implements PushNotificationHandler
{
    private final Map<String, Set<String>> deviceTokensByTopic;
    private final Map<String, Date> expirationTimestampsByDeviceToken;
    private static final String APNS_PATH_PREFIX = "/3/device/";
    private static final AsciiString APNS_TOPIC_HEADER;
    private static final AsciiString APNS_PRIORITY_HEADER;
    private static final AsciiString APNS_ID_HEADER;
    private static final AsciiString APNS_COLLAPSE_ID_HEADER;
    private static final AsciiString APNS_PUSH_TYPE_HEADER;
    private static final Pattern DEVICE_TOKEN_PATTERN;
    private static final int MAX_PAYLOAD_SIZE = 4096;
    private static final int MAX_COLLAPSE_ID_SIZE = 64;
    
    ValidatingPushNotificationHandler(final Map<String, Set<String>> deviceTokensByTopic, final Map<String, Date> expirationTimestampsByDeviceToken) {
        this.deviceTokensByTopic = deviceTokensByTopic;
        this.expirationTimestampsByDeviceToken = expirationTimestampsByDeviceToken;
    }
    
    @Override
    public void handlePushNotification(final Http2Headers headers, final ByteBuf payload) throws RejectedNotificationException {
        try {
            final CharSequence apnsIdSequence = (CharSequence)headers.get((Object)ValidatingPushNotificationHandler.APNS_ID_HEADER);
            if (apnsIdSequence != null) {
                FastUUID.parseUUID(apnsIdSequence);
            }
        }
        catch (final IllegalArgumentException e) {
            throw new RejectedNotificationException(RejectionReason.BAD_MESSAGE_ID);
        }
        if (!HttpMethod.POST.asciiName().contentEquals((CharSequence)headers.get((Object)Http2Headers.PseudoHeaderName.METHOD.value()))) {
            throw new RejectedNotificationException(RejectionReason.METHOD_NOT_ALLOWED);
        }
        final CharSequence topicSequence = (CharSequence)headers.get((Object)ValidatingPushNotificationHandler.APNS_TOPIC_HEADER);
        if (topicSequence == null) {
            throw new RejectedNotificationException(RejectionReason.MISSING_TOPIC);
        }
        final String topic = topicSequence.toString();
        final CharSequence collapseIdSequence = (CharSequence)headers.get((Object)ValidatingPushNotificationHandler.APNS_COLLAPSE_ID_HEADER);
        if (collapseIdSequence != null && collapseIdSequence.toString().getBytes(StandardCharsets.UTF_8).length > 64) {
            throw new RejectedNotificationException(RejectionReason.BAD_COLLAPSE_ID);
        }
        final Integer priorityCode = headers.getInt((Object)ValidatingPushNotificationHandler.APNS_PRIORITY_HEADER);
        if (priorityCode != null) {
            try {
                DeliveryPriority.getFromCode(priorityCode);
            }
            catch (final IllegalArgumentException e2) {
                throw new RejectedNotificationException(RejectionReason.BAD_PRIORITY);
            }
        }
        final CharSequence pushTypeSequence = (CharSequence)headers.get((Object)ValidatingPushNotificationHandler.APNS_PUSH_TYPE_HEADER);
        if (pushTypeSequence != null) {
            try {
                PushType.getFromHeaderValue(pushTypeSequence);
            }
            catch (final IllegalArgumentException e2) {
                throw new RejectedNotificationException(RejectionReason.INVALID_PUSH_TYPE);
            }
        }
        final CharSequence pathSequence = (CharSequence)headers.get((Object)Http2Headers.PseudoHeaderName.PATH.value());
        if (pathSequence == null) {
            throw new RejectedNotificationException(RejectionReason.BAD_PATH);
        }
        final String pathString = pathSequence.toString();
        if (pathSequence.toString().equals("/3/device/")) {
            throw new RejectedNotificationException(RejectionReason.MISSING_DEVICE_TOKEN);
        }
        if (!pathString.startsWith("/3/device/")) {
            throw new RejectedNotificationException(RejectionReason.BAD_PATH);
        }
        final String deviceToken = pathString.substring("/3/device/".length());
        final Matcher tokenMatcher = ValidatingPushNotificationHandler.DEVICE_TOKEN_PATTERN.matcher(deviceToken);
        if (!tokenMatcher.matches()) {
            throw new RejectedNotificationException(RejectionReason.BAD_DEVICE_TOKEN);
        }
        final Date expirationTimestamp = this.expirationTimestampsByDeviceToken.get(deviceToken);
        if (expirationTimestamp != null) {
            throw new UnregisteredDeviceTokenException(expirationTimestamp);
        }
        final Set<String> allowedDeviceTokensForTopic = this.deviceTokensByTopic.get(topic);
        if (allowedDeviceTokensForTopic == null || !allowedDeviceTokensForTopic.contains(deviceToken)) {
            throw new RejectedNotificationException(RejectionReason.DEVICE_TOKEN_NOT_FOR_TOPIC);
        }
        this.verifyAuthentication(headers);
        if (payload == null || payload.readableBytes() == 0) {
            throw new RejectedNotificationException(RejectionReason.PAYLOAD_EMPTY);
        }
        if (payload.readableBytes() > 4096) {
            throw new RejectedNotificationException(RejectionReason.PAYLOAD_TOO_LARGE);
        }
    }
    
    protected abstract void verifyAuthentication(final Http2Headers p0) throws RejectedNotificationException;
    
    static {
        APNS_TOPIC_HEADER = new AsciiString((CharSequence)"apns-topic");
        APNS_PRIORITY_HEADER = new AsciiString((CharSequence)"apns-priority");
        APNS_ID_HEADER = new AsciiString((CharSequence)"apns-id");
        APNS_COLLAPSE_ID_HEADER = new AsciiString((CharSequence)"apns-collapse-id");
        APNS_PUSH_TYPE_HEADER = new AsciiString((CharSequence)"apns-push-type");
        DEVICE_TOKEN_PATTERN = Pattern.compile("[0-9a-fA-F]{64}");
    }
}
