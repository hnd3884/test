package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import java.util.Iterator;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;

public class CorbaInvocationInfo implements ClientInvocationInfo
{
    private boolean isRetryInvocation;
    private int entryCount;
    private ORB orb;
    private Iterator contactInfoListIterator;
    private ClientRequestDispatcher clientRequestDispatcher;
    private MessageMediator messageMediator;
    
    private CorbaInvocationInfo() {
    }
    
    public CorbaInvocationInfo(final ORB orb) {
        this.orb = orb;
        this.isRetryInvocation = false;
        this.entryCount = 0;
    }
    
    @Override
    public Iterator getContactInfoListIterator() {
        return this.contactInfoListIterator;
    }
    
    @Override
    public void setContactInfoListIterator(final Iterator contactInfoListIterator) {
        this.contactInfoListIterator = contactInfoListIterator;
    }
    
    @Override
    public boolean isRetryInvocation() {
        return this.isRetryInvocation;
    }
    
    @Override
    public void setIsRetryInvocation(final boolean isRetryInvocation) {
        this.isRetryInvocation = isRetryInvocation;
    }
    
    @Override
    public int getEntryCount() {
        return this.entryCount;
    }
    
    @Override
    public void incrementEntryCount() {
        ++this.entryCount;
    }
    
    @Override
    public void decrementEntryCount() {
        --this.entryCount;
    }
    
    @Override
    public void setClientRequestDispatcher(final ClientRequestDispatcher clientRequestDispatcher) {
        this.clientRequestDispatcher = clientRequestDispatcher;
    }
    
    @Override
    public ClientRequestDispatcher getClientRequestDispatcher() {
        return this.clientRequestDispatcher;
    }
    
    @Override
    public void setMessageMediator(final MessageMediator messageMediator) {
        this.messageMediator = messageMediator;
    }
    
    @Override
    public MessageMediator getMessageMediator() {
        return this.messageMediator;
    }
}
