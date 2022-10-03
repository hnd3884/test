package org.apache.catalina.tribes;

public interface MessageListener
{
    void messageReceived(final ChannelMessage p0);
    
    boolean accept(final ChannelMessage p0);
}
