package com.maverick.ssh;

public interface SshTunnel extends SshChannel, SshTransport
{
    int getPort();
    
    String getListeningAddress();
    
    int getListeningPort();
    
    String getOriginatingHost();
    
    int getOriginatingPort();
    
    boolean isLocal();
    
    boolean isX11();
    
    SshTransport getTransport();
    
    boolean isLocalEOF();
    
    boolean isRemoteEOF();
}
