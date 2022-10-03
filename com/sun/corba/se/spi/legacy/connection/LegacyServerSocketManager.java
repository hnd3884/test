package com.sun.corba.se.spi.legacy.connection;

public interface LegacyServerSocketManager
{
    int legacyGetTransientServerPort(final String p0);
    
    int legacyGetPersistentServerPort(final String p0);
    
    int legacyGetTransientOrPersistentServerPort(final String p0);
    
    LegacyServerSocketEndPointInfo legacyGetEndpoint(final String p0);
    
    boolean legacyIsLocalServerPort(final int p0);
}
