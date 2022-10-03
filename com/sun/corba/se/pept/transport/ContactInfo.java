package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.broker.Broker;

public interface ContactInfo
{
    Broker getBroker();
    
    ContactInfoList getContactInfoList();
    
    ClientRequestDispatcher getClientRequestDispatcher();
    
    boolean isConnectionBased();
    
    boolean shouldCacheConnection();
    
    String getConnectionCacheType();
    
    void setConnectionCache(final OutboundConnectionCache p0);
    
    OutboundConnectionCache getConnectionCache();
    
    Connection createConnection();
    
    MessageMediator createMessageMediator(final Broker p0, final ContactInfo p1, final Connection p2, final String p3, final boolean p4);
    
    MessageMediator createMessageMediator(final Broker p0, final Connection p1);
    
    MessageMediator finishCreatingMessageMediator(final Broker p0, final Connection p1, final MessageMediator p2);
    
    InputObject createInputObject(final Broker p0, final MessageMediator p1);
    
    OutputObject createOutputObject(final MessageMediator p0);
    
    int hashCode();
}
