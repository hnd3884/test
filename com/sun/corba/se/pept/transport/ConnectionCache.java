package com.sun.corba.se.pept.transport;

public interface ConnectionCache
{
    String getCacheType();
    
    void stampTime(final Connection p0);
    
    long numberOfConnections();
    
    long numberOfIdleConnections();
    
    long numberOfBusyConnections();
    
    boolean reclaim();
    
    void close();
}
