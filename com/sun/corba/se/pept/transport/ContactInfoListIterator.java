package com.sun.corba.se.pept.transport;

import java.util.Iterator;

public interface ContactInfoListIterator extends Iterator
{
    ContactInfoList getContactInfoList();
    
    void reportSuccess(final ContactInfo p0);
    
    boolean reportException(final ContactInfo p0, final RuntimeException p1);
    
    RuntimeException getFailureException();
}
