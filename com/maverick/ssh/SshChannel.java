package com.maverick.ssh;

import com.maverick.ssh.message.SshMessageRouter;

public interface SshChannel extends SshIO
{
    int getChannelId();
    
    boolean isClosed();
    
    void addChannelEventListener(final ChannelEventListener p0);
    
    void setAutoConsumeInput(final boolean p0);
    
    SshMessageRouter getMessageRouter();
}
