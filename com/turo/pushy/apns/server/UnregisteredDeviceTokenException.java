package com.turo.pushy.apns.server;

import java.util.Objects;
import java.util.Date;

public class UnregisteredDeviceTokenException extends RejectedNotificationException
{
    private final Date deviceTokenExpirationTimestamp;
    
    public UnregisteredDeviceTokenException(final Date deviceTokenExpirationTimestamp) {
        super(RejectionReason.UNREGISTERED);
        Objects.requireNonNull(deviceTokenExpirationTimestamp, "Device token expiration timestamp must not be null.");
        this.deviceTokenExpirationTimestamp = deviceTokenExpirationTimestamp;
    }
    
    Date getDeviceTokenExpirationTimestamp() {
        return this.deviceTokenExpirationTimestamp;
    }
}
