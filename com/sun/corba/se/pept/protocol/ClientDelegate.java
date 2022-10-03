package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.pept.broker.Broker;

public interface ClientDelegate
{
    Broker getBroker();
    
    ContactInfoList getContactInfoList();
}
