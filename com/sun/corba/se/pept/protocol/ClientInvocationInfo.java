package com.sun.corba.se.pept.protocol;

import java.util.Iterator;

public interface ClientInvocationInfo
{
    Iterator getContactInfoListIterator();
    
    void setContactInfoListIterator(final Iterator p0);
    
    boolean isRetryInvocation();
    
    void setIsRetryInvocation(final boolean p0);
    
    int getEntryCount();
    
    void incrementEntryCount();
    
    void decrementEntryCount();
    
    void setClientRequestDispatcher(final ClientRequestDispatcher p0);
    
    ClientRequestDispatcher getClientRequestDispatcher();
    
    void setMessageMediator(final MessageMediator p0);
    
    MessageMediator getMessageMediator();
}
