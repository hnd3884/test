package com.sun.corba.se.pept.transport;

import java.util.Collection;

public interface TransportManager
{
    ByteBufferPool getByteBufferPool(final int p0);
    
    OutboundConnectionCache getOutboundConnectionCache(final ContactInfo p0);
    
    Collection getOutboundConnectionCaches();
    
    InboundConnectionCache getInboundConnectionCache(final Acceptor p0);
    
    Collection getInboundConnectionCaches();
    
    Selector getSelector(final int p0);
    
    void registerAcceptor(final Acceptor p0);
    
    Collection getAcceptors();
    
    void unregisterAcceptor(final Acceptor p0);
    
    void close();
}
