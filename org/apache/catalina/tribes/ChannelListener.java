package org.apache.catalina.tribes;

import java.io.Serializable;

public interface ChannelListener
{
    void messageReceived(final Serializable p0, final Member p1);
    
    boolean accept(final Serializable p0, final Member p1);
    
    boolean equals(final Object p0);
    
    int hashCode();
}
