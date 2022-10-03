package org.apache.catalina.tribes.transport;

import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;

public interface MultiPointSender extends DataSender
{
    void sendMessage(final Member[] p0, final ChannelMessage p1) throws ChannelException;
    
    void setMaxRetryAttempts(final int p0);
    
    void setDirectBuffer(final boolean p0);
    
    void add(final Member p0);
    
    void remove(final Member p0);
}
