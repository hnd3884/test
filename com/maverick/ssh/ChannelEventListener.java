package com.maverick.ssh;

public interface ChannelEventListener
{
    void channelOpened(final SshChannel p0);
    
    void channelClosing(final SshChannel p0);
    
    void channelClosed(final SshChannel p0);
    
    void channelEOF(final SshChannel p0);
    
    void dataReceived(final SshChannel p0, final byte[] p1, final int p2, final int p3);
    
    void dataSent(final SshChannel p0, final byte[] p1, final int p2, final int p3);
    
    void extendedDataReceived(final SshChannel p0, final byte[] p1, final int p2, final int p3, final int p4);
}
