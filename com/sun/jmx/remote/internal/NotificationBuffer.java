package com.sun.jmx.remote.internal;

import javax.management.remote.NotificationResult;

public interface NotificationBuffer
{
    NotificationResult fetchNotifications(final NotificationBufferFilter p0, final long p1, final long p2, final int p3) throws InterruptedException;
    
    void dispose();
}
