package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.encoding.OutputObject;

public interface Connection
{
    boolean shouldRegisterReadEvent();
    
    boolean shouldRegisterServerReadEvent();
    
    boolean read();
    
    void close();
    
    Acceptor getAcceptor();
    
    ContactInfo getContactInfo();
    
    EventHandler getEventHandler();
    
    boolean isServer();
    
    boolean isBusy();
    
    long getTimeStamp();
    
    void setTimeStamp(final long p0);
    
    void setState(final String p0);
    
    void writeLock();
    
    void writeUnlock();
    
    void sendWithoutLock(final OutputObject p0);
    
    void registerWaiter(final MessageMediator p0);
    
    InputObject waitForResponse(final MessageMediator p0);
    
    void unregisterWaiter(final MessageMediator p0);
    
    void setConnectionCache(final ConnectionCache p0);
    
    ConnectionCache getConnectionCache();
}
