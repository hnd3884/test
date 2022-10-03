package org.apache.catalina.tribes;

public interface ErrorHandler
{
    void handleError(final ChannelException p0, final UniqueId p1);
    
    void handleCompletion(final UniqueId p0);
}
