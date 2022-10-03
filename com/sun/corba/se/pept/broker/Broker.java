package com.sun.corba.se.pept.broker;

import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;

public interface Broker
{
    ClientInvocationInfo createOrIncrementInvocationInfo();
    
    ClientInvocationInfo getInvocationInfo();
    
    void releaseOrDecrementInvocationInfo();
    
    TransportManager getTransportManager();
}
