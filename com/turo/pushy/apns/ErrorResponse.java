package com.turo.pushy.apns;

import java.util.Date;

class ErrorResponse
{
    private final String reason;
    private final Date timestamp;
    
    public ErrorResponse(final String reason, final Date timestamp) {
        this.reason = reason;
        this.timestamp = timestamp;
    }
    
    String getReason() {
        return this.reason;
    }
    
    Date getTimestamp() {
        return this.timestamp;
    }
}
