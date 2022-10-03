package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.broker.Broker;

public interface MessageMediator
{
    Broker getBroker();
    
    ContactInfo getContactInfo();
    
    Connection getConnection();
    
    void initializeMessage();
    
    void finishSendingRequest();
    
    @Deprecated
    InputObject waitForResponse();
    
    void setOutputObject(final OutputObject p0);
    
    OutputObject getOutputObject();
    
    void setInputObject(final InputObject p0);
    
    InputObject getInputObject();
}
