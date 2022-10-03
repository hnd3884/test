package com.sun.corba.se.pept.transport;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import java.nio.channels.SelectionKey;
import java.nio.channels.SelectableChannel;

public interface EventHandler
{
    void setUseSelectThreadToWait(final boolean p0);
    
    boolean shouldUseSelectThreadToWait();
    
    SelectableChannel getChannel();
    
    int getInterestOps();
    
    void setSelectionKey(final SelectionKey p0);
    
    SelectionKey getSelectionKey();
    
    void handleEvent();
    
    void setUseWorkerThreadForEvent(final boolean p0);
    
    boolean shouldUseWorkerThreadForEvent();
    
    void setWork(final Work p0);
    
    Work getWork();
    
    Acceptor getAcceptor();
    
    Connection getConnection();
}
