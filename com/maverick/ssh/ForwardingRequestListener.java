package com.maverick.ssh;

public interface ForwardingRequestListener
{
    SshTransport createConnection(final String p0, final int p1) throws SshException;
    
    void initializeTunnel(final SshTunnel p0);
}
