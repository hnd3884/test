package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.pept.transport.ContactInfoListIterator;

public interface CorbaContactInfoListIterator extends ContactInfoListIterator
{
    void reportAddrDispositionRetry(final CorbaContactInfo p0, final short p1);
    
    void reportRedirect(final CorbaContactInfo p0, final IOR p1);
}
