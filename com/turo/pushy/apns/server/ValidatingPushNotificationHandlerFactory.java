package com.turo.pushy.apns.server;

import java.util.regex.Matcher;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.util.regex.Pattern;
import javax.net.ssl.SSLSession;
import java.util.Collections;
import com.turo.pushy.apns.auth.ApnsVerificationKey;
import java.util.Date;
import java.util.Set;
import java.util.Map;

public class ValidatingPushNotificationHandlerFactory implements PushNotificationHandlerFactory
{
    private final Map<String, Set<String>> deviceTokensByTopic;
    private final Map<String, Date> expirationTimestampsByDeviceToken;
    private final Map<String, ApnsVerificationKey> verificationKeysByKeyId;
    private final Map<ApnsVerificationKey, Set<String>> topicsByVerificationKey;
    
    public ValidatingPushNotificationHandlerFactory(final Map<String, Set<String>> deviceTokensByTopic, final Map<String, Date> expirationTimestampsByDeviceToken, final Map<String, ApnsVerificationKey> verificationKeysByKeyId, final Map<ApnsVerificationKey, Set<String>> topicsByVerificationKey) {
        this.deviceTokensByTopic = ((deviceTokensByTopic != null) ? deviceTokensByTopic : Collections.emptyMap());
        this.expirationTimestampsByDeviceToken = ((expirationTimestampsByDeviceToken != null) ? expirationTimestampsByDeviceToken : Collections.emptyMap());
        this.verificationKeysByKeyId = ((verificationKeysByKeyId != null) ? verificationKeysByKeyId : Collections.emptyMap());
        this.topicsByVerificationKey = ((topicsByVerificationKey != null) ? topicsByVerificationKey : Collections.emptyMap());
    }
    
    @Override
    public PushNotificationHandler buildHandler(final SSLSession sslSession) {
        try {
            final String principalName = sslSession.getPeerPrincipal().getName();
            final Pattern pattern = Pattern.compile(".*UID=([^,]+).*");
            final Matcher matcher = pattern.matcher(principalName);
            if (matcher.matches()) {
                final String baseTopic = matcher.group(1);
                return new TlsAuthenticationValidatingPushNotificationHandler(this.deviceTokensByTopic, this.expirationTimestampsByDeviceToken, baseTopic);
            }
            throw new IllegalArgumentException("Client certificate does not specify a base topic.");
        }
        catch (final SSLPeerUnverifiedException e) {
            return new TokenAuthenticationValidatingPushNotificationHandler(this.deviceTokensByTopic, this.expirationTimestampsByDeviceToken, this.verificationKeysByKeyId, this.topicsByVerificationKey);
        }
    }
}
