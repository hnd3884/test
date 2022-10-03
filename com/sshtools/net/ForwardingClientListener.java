package com.sshtools.net;

import com.maverick.ssh.SshTunnel;

public interface ForwardingClientListener
{
    public static final int LOCAL_FORWARDING = 1;
    public static final int REMOTE_FORWARDING = 2;
    public static final int X11_FORWARDING = 3;
    
    void forwardingStarted(final int p0, final String p1, final String p2, final int p3);
    
    void forwardingStopped(final int p0, final String p1, final String p2, final int p3);
    
    void channelFailure(final int p0, final String p1, final String p2, final int p3, final boolean p4, final Throwable p5);
    
    void channelOpened(final int p0, final String p1, final SshTunnel p2);
    
    void channelClosed(final int p0, final String p1, final SshTunnel p2);
}
