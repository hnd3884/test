package com.turo.pushy.apns.server;

import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import com.turo.pushy.apns.auth.AuthenticationToken;
import io.netty.handler.codec.http2.Http2Headers;
import java.util.Date;
import org.slf4j.Logger;
import io.netty.util.AsciiString;
import java.util.Set;
import com.turo.pushy.apns.auth.ApnsVerificationKey;
import java.util.Map;

class TokenAuthenticationValidatingPushNotificationHandler extends ValidatingPushNotificationHandler
{
    private final Map<String, ApnsVerificationKey> verificationKeysByKeyId;
    private final Map<ApnsVerificationKey, Set<String>> topicsByVerificationKey;
    private String expectedTeamId;
    private static final AsciiString APNS_TOPIC_HEADER;
    private static final AsciiString APNS_AUTHORIZATION_HEADER;
    private static final long AUTHENTICATION_TOKEN_EXPIRATION_MILLIS;
    private static final Logger log;
    
    TokenAuthenticationValidatingPushNotificationHandler(final Map<String, Set<String>> deviceTokensByTopic, final Map<String, Date> expirationTimestampsByDeviceToken, final Map<String, ApnsVerificationKey> verificationKeysByKeyId, final Map<ApnsVerificationKey, Set<String>> topicsByVerificationKey) {
        super(deviceTokensByTopic, expirationTimestampsByDeviceToken);
        this.verificationKeysByKeyId = verificationKeysByKeyId;
        this.topicsByVerificationKey = topicsByVerificationKey;
    }
    
    @Override
    protected void verifyAuthentication(final Http2Headers headers) throws RejectedNotificationException {
        final CharSequence authorizationSequence = (CharSequence)headers.get((Object)TokenAuthenticationValidatingPushNotificationHandler.APNS_AUTHORIZATION_HEADER);
        if (authorizationSequence == null) {
            throw new RejectedNotificationException(RejectionReason.MISSING_PROVIDER_TOKEN);
        }
        final String authorizationString = authorizationSequence.toString();
        if (!authorizationString.startsWith("bearer")) {
            throw new RejectedNotificationException(RejectionReason.MISSING_PROVIDER_TOKEN);
        }
        final String base64EncodedAuthenticationToken = authorizationString.substring("bearer".length()).trim();
        if (base64EncodedAuthenticationToken.trim().length() == 0) {
            throw new RejectedNotificationException(RejectionReason.MISSING_PROVIDER_TOKEN);
        }
        AuthenticationToken authenticationToken;
        try {
            authenticationToken = new AuthenticationToken(base64EncodedAuthenticationToken);
        }
        catch (final IllegalArgumentException e) {
            throw new RejectedNotificationException(RejectionReason.INVALID_PROVIDER_TOKEN);
        }
        final ApnsVerificationKey verificationKey = this.verificationKeysByKeyId.get(authenticationToken.getKeyId());
        if (verificationKey == null) {
            throw new RejectedNotificationException(RejectionReason.INVALID_PROVIDER_TOKEN);
        }
        try {
            if (!authenticationToken.verifySignature(verificationKey)) {
                throw new RejectedNotificationException(RejectionReason.INVALID_PROVIDER_TOKEN);
            }
        }
        catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException e2) {
            TokenAuthenticationValidatingPushNotificationHandler.log.error("Failed to verify authentication token signature.", (Throwable)e2);
            throw new RuntimeException(e2);
        }
        if (this.expectedTeamId == null) {
            this.expectedTeamId = authenticationToken.getTeamId();
        }
        if (!this.expectedTeamId.equals(authenticationToken.getTeamId())) {
            throw new RejectedNotificationException(RejectionReason.INVALID_PROVIDER_TOKEN);
        }
        if (authenticationToken.getIssuedAt().getTime() + TokenAuthenticationValidatingPushNotificationHandler.AUTHENTICATION_TOKEN_EXPIRATION_MILLIS < System.currentTimeMillis()) {
            throw new RejectedNotificationException(RejectionReason.EXPIRED_PROVIDER_TOKEN);
        }
        final CharSequence topicSequence = (CharSequence)headers.get((Object)TokenAuthenticationValidatingPushNotificationHandler.APNS_TOPIC_HEADER);
        if (topicSequence == null) {
            throw new RejectedNotificationException(RejectionReason.MISSING_TOPIC);
        }
        final String topic = topicSequence.toString();
        final Set<String> topicsAllowedForVerificationKey = this.topicsByVerificationKey.get(verificationKey);
        if (topicsAllowedForVerificationKey == null || !topicsAllowedForVerificationKey.contains(topic)) {
            throw new RejectedNotificationException(RejectionReason.INVALID_PROVIDER_TOKEN);
        }
    }
    
    static {
        APNS_TOPIC_HEADER = new AsciiString((CharSequence)"apns-topic");
        APNS_AUTHORIZATION_HEADER = new AsciiString((CharSequence)"authorization");
        AUTHENTICATION_TOKEN_EXPIRATION_MILLIS = TimeUnit.HOURS.toMillis(1L);
        log = LoggerFactory.getLogger((Class)TokenAuthenticationValidatingPushNotificationHandler.class);
    }
}
