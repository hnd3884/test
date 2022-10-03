package com.sun.corba.se.spi.transport;

import java.util.List;
import com.sun.corba.se.pept.transport.ContactInfo;

public interface IIOPPrimaryToContactInfo
{
    void reset(final ContactInfo p0);
    
    boolean hasNext(final ContactInfo p0, final ContactInfo p1, final List p2);
    
    ContactInfo next(final ContactInfo p0, final ContactInfo p1, final List p2);
}
