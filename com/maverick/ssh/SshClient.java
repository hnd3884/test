package com.maverick.ssh;

public interface SshClient extends Client
{
    void connect(final SshTransport p0, final SshContext p1, final SshConnector p2, final String p3, final String p4, final String p5, final boolean p6) throws SshException;
    
    int authenticate(final SshAuthentication p0) throws SshException;
    
    SshSession openSessionChannel() throws SshException, ChannelOpenException;
    
    SshSession openSessionChannel(final ChannelEventListener p0) throws SshException, ChannelOpenException;
    
    SshTunnel openForwardingChannel(final String p0, final int p1, final String p2, final int p3, final String p4, final int p5, final SshTransport p6, final ChannelEventListener p7) throws SshException, ChannelOpenException;
    
    SshClient openRemoteClient(final String p0, final int p1, final String p2, final SshConnector p3) throws SshException, ChannelOpenException;
    
    SshClient openRemoteClient(final String p0, final int p1, final String p2) throws SshException, ChannelOpenException;
    
    boolean requestRemoteForwarding(final String p0, final int p1, final String p2, final int p3, final ForwardingRequestListener p4) throws SshException;
    
    boolean cancelRemoteForwarding(final String p0, final int p1) throws SshException;
    
    void disconnect();
    
    boolean isAuthenticated();
    
    boolean isConnected();
    
    String getRemoteIdentification();
    
    String getUsername();
    
    SshClient duplicate() throws SshException;
    
    SshContext getContext();
    
    int getChannelCount();
    
    int getVersion();
    
    boolean isBuffered();
}
