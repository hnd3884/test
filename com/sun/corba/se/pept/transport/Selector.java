package com.sun.corba.se.pept.transport;

public interface Selector
{
    void setTimeout(final long p0);
    
    long getTimeout();
    
    void registerInterestOps(final EventHandler p0);
    
    void registerForEvent(final EventHandler p0);
    
    void unregisterForEvent(final EventHandler p0);
    
    void close();
}
