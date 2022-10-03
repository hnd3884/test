package com.sun.corba.se.pept.transport;

public interface OutboundConnectionCache extends ConnectionCache
{
    Connection get(final ContactInfo p0);
    
    void put(final ContactInfo p0, final Connection p1);
    
    void remove(final ContactInfo p0);
}
