package com.turo.pushy.apns.server;

import java.util.Objects;

public class RejectedNotificationException extends Exception
{
    private final RejectionReason errorReason;
    
    public RejectedNotificationException(final RejectionReason rejectionReason) {
        Objects.requireNonNull(rejectionReason, "Error reason must not be null.");
        this.errorReason = rejectionReason;
    }
    
    RejectionReason getRejectionReason() {
        return this.errorReason;
    }
}
