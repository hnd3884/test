package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.protocol.MessageMediator;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;

public interface CorbaResponseWaitingRoom extends ResponseWaitingRoom
{
    void signalExceptionToAllWaiters(final SystemException p0);
    
    MessageMediator getMessageMediator(final int p0);
}
