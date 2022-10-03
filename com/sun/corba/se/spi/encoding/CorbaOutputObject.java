package com.sun.corba.se.spi.encoding;

import java.io.IOException;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.impl.encoding.BufferManagerWrite;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.impl.encoding.CDROutputStream;

public abstract class CorbaOutputObject extends CDROutputStream implements OutputObject
{
    public CorbaOutputObject(final ORB orb, final GIOPVersion giopVersion, final byte b, final boolean b2, final BufferManagerWrite bufferManagerWrite, final byte b3, final boolean b4) {
        super(orb, giopVersion, b, b2, bufferManagerWrite, b3, b4);
    }
    
    public abstract void writeTo(final CorbaConnection p0) throws IOException;
}
