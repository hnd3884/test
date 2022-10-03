package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import java.util.Iterator;
import com.sun.corba.se.spi.orb.ORB;
import java.util.List;

public interface IOR extends List, Writeable, MakeImmutable
{
    ORB getORB();
    
    String getTypeId();
    
    Iterator iteratorById(final int p0);
    
    String stringify();
    
    org.omg.IOP.IOR getIOPIOR();
    
    boolean isNil();
    
    boolean isEquivalent(final IOR p0);
    
    IORTemplateList getIORTemplates();
    
    IIOPProfile getProfile();
}
