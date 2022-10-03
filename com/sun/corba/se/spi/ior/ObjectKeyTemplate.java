package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.orb.ORBVersion;

public interface ObjectKeyTemplate extends Writeable
{
    ORBVersion getORBVersion();
    
    int getSubcontractId();
    
    int getServerId();
    
    String getORBId();
    
    ObjectAdapterId getObjectAdapterId();
    
    byte[] getAdapterId();
    
    void write(final ObjectId p0, final OutputStream p1);
    
    CorbaServerRequestDispatcher getServerRequestDispatcher(final ORB p0, final ObjectId p1);
}
