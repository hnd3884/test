package com.sun.corba.se.spi.legacy.connection;

public interface LegacyServerSocketEndPointInfo
{
    public static final String DEFAULT_ENDPOINT = "DEFAULT_ENDPOINT";
    public static final String BOOT_NAMING = "BOOT_NAMING";
    public static final String NO_NAME = "NO_NAME";
    
    String getType();
    
    String getHostName();
    
    int getPort();
    
    int getLocatorPort();
    
    void setLocatorPort(final int p0);
    
    String getName();
}
