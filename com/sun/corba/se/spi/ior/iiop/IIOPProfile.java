package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.ior.TaggedProfile;

public interface IIOPProfile extends TaggedProfile
{
    ORBVersion getORBVersion();
    
    Object getServant();
    
    GIOPVersion getGIOPVersion();
    
    String getCodebase();
}
