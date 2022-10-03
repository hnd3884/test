package com.sun.corba.se.pept.transport;

public interface InboundConnectionCache extends ConnectionCache
{
    Connection get(final Acceptor p0);
    
    void put(final Acceptor p0, final Connection p1);
    
    void remove(final Connection p0);
    
    Acceptor getAcceptor();
}
