package com.turo.pushy.apns;

public interface ApnsClientMetricsListener
{
    void handleWriteFailure(final ApnsClient p0, final long p1);
    
    void handleNotificationSent(final ApnsClient p0, final long p1);
    
    void handleNotificationAccepted(final ApnsClient p0, final long p1);
    
    void handleNotificationRejected(final ApnsClient p0, final long p1);
    
    void handleConnectionAdded(final ApnsClient p0);
    
    void handleConnectionRemoved(final ApnsClient p0);
    
    void handleConnectionCreationFailed(final ApnsClient p0);
}
