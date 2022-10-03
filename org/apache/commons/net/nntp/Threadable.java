package org.apache.commons.net.nntp;

public interface Threadable
{
    boolean isDummy();
    
    String messageThreadId();
    
    String[] messageThreadReferences();
    
    String simplifiedSubject();
    
    boolean subjectIsReply();
    
    void setChild(final Threadable p0);
    
    void setNext(final Threadable p0);
    
    Threadable makeDummy();
}
