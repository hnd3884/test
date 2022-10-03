package com.turo.pushy.apns;

import com.eatthepath.uuid.FastUUID;
import java.util.Date;
import java.util.UUID;

class SimplePushNotificationResponse<T extends ApnsPushNotification> implements PushNotificationResponse<T>
{
    private final T pushNotification;
    private final boolean success;
    private final UUID apnsId;
    private final String rejectionReason;
    private final Date tokenExpirationTimestamp;
    
    SimplePushNotificationResponse(final T pushNotification, final boolean success, final UUID apnsId, final String rejectionReason, final Date tokenExpirationTimestamp) {
        this.pushNotification = pushNotification;
        this.success = success;
        this.apnsId = apnsId;
        this.rejectionReason = rejectionReason;
        this.tokenExpirationTimestamp = tokenExpirationTimestamp;
    }
    
    @Override
    public T getPushNotification() {
        return this.pushNotification;
    }
    
    @Override
    public boolean isAccepted() {
        return this.success;
    }
    
    @Override
    public UUID getApnsId() {
        return this.apnsId;
    }
    
    @Override
    public String getRejectionReason() {
        return this.rejectionReason;
    }
    
    @Override
    public Date getTokenInvalidationTimestamp() {
        return this.tokenExpirationTimestamp;
    }
    
    @Override
    public String toString() {
        return "SimplePushNotificationResponse{pushNotification=" + this.pushNotification + ", success=" + this.success + ", apnsId=" + ((this.apnsId != null) ? FastUUID.toString(this.apnsId) : null) + ", rejectionReason='" + this.rejectionReason + '\'' + ", tokenExpirationTimestamp=" + this.tokenExpirationTimestamp + '}';
    }
}
