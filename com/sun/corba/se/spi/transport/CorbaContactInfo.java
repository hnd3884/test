package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.pept.transport.ContactInfo;

public interface CorbaContactInfo extends ContactInfo
{
    IOR getTargetIOR();
    
    IOR getEffectiveTargetIOR();
    
    IIOPProfile getEffectiveProfile();
    
    void setAddressingDisposition(final short p0);
    
    short getAddressingDisposition();
    
    String getMonitoringName();
}
