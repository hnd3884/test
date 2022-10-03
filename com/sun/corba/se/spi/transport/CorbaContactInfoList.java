package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.pept.transport.ContactInfoList;

public interface CorbaContactInfoList extends ContactInfoList
{
    void setTargetIOR(final IOR p0);
    
    IOR getTargetIOR();
    
    void setEffectiveTargetIOR(final IOR p0);
    
    IOR getEffectiveTargetIOR();
    
    LocalClientRequestDispatcher getLocalClientRequestDispatcher();
    
    int hashCode();
}
