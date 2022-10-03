package com.turo.pushy.apns;

interface ApnsChannelPoolMetricsListener
{
    void handleConnectionAdded();
    
    void handleConnectionRemoved();
    
    void handleConnectionCreationFailed();
}
