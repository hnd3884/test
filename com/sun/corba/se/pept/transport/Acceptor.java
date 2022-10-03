package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.broker.Broker;

public interface Acceptor
{
    boolean initialize();
    
    boolean initialized();
    
    String getConnectionCacheType();
    
    void setConnectionCache(final InboundConnectionCache p0);
    
    InboundConnectionCache getConnectionCache();
    
    boolean shouldRegisterAcceptEvent();
    
    void accept();
    
    void close();
    
    EventHandler getEventHandler();
    
    MessageMediator createMessageMediator(final Broker p0, final Connection p1);
    
    MessageMediator finishCreatingMessageMediator(final Broker p0, final Connection p1, final MessageMediator p2);
    
    InputObject createInputObject(final Broker p0, final MessageMediator p1);
    
    OutputObject createOutputObject(final Broker p0, final MessageMediator p1);
}
