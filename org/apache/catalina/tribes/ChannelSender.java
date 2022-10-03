package org.apache.catalina.tribes;

import java.io.IOException;

public interface ChannelSender extends Heartbeat
{
    void add(final Member p0);
    
    void remove(final Member p0);
    
    void start() throws IOException;
    
    void stop();
    
    void heartbeat();
    
    void sendMessage(final ChannelMessage p0, final Member[] p1) throws ChannelException;
    
    Channel getChannel();
    
    void setChannel(final Channel p0);
}
