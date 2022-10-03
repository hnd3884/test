package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA.ORB;

public interface ObjectKey extends Writeable
{
    ObjectId getId();
    
    ObjectKeyTemplate getTemplate();
    
    byte[] getBytes(final ORB p0);
    
    CorbaServerRequestDispatcher getServerRequestDispatcher(final com.sun.corba.se.spi.orb.ORB p0);
}
