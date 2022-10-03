package com.adventnet.persistence.fos;

public interface FOSErrorHandler
{
    void handleError(final FOSException p0);
    
    void handleReplicationError(final int p0, final String p1, final String p2, final boolean p3);
}
