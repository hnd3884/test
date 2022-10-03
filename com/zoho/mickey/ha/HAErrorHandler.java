package com.zoho.mickey.ha;

public interface HAErrorHandler
{
    void handleError(final HAException p0);
    
    void handleReplicationError(final int p0, final String p1, final String p2, final boolean p3);
}
