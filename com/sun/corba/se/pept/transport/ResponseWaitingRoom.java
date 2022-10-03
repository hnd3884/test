package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;

public interface ResponseWaitingRoom
{
    void registerWaiter(final MessageMediator p0);
    
    InputObject waitForResponse(final MessageMediator p0);
    
    void responseReceived(final InputObject p0);
    
    void unregisterWaiter(final MessageMediator p0);
    
    int numberRegistered();
}
