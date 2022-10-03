package com.maverick.ssh2;

public interface TransportProtocolListener
{
    void onDisconnect(final String p0, final int p1);
    
    void onIdle(final long p0);
}
