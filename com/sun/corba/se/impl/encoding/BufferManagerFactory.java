package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.INTERNAL;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class BufferManagerFactory
{
    public static final int GROW = 0;
    public static final int COLLECT = 1;
    public static final int STREAM = 2;
    
    public static BufferManagerRead newBufferManagerRead(final GIOPVersion giopVersion, final byte b, final ORB orb) {
        if (b != 0) {
            return new BufferManagerReadGrow(orb);
        }
        switch (giopVersion.intValue()) {
            case 256: {
                return new BufferManagerReadGrow(orb);
            }
            case 257:
            case 258: {
                return new BufferManagerReadStream(orb);
            }
            default: {
                throw new INTERNAL("Unknown GIOP version: " + giopVersion);
            }
        }
    }
    
    public static BufferManagerRead newBufferManagerRead(final int n, final byte b, final ORB orb) {
        if (b != 0) {
            if (n != 0) {
                throw ORBUtilSystemException.get(orb, "rpc.encoding").invalidBuffMgrStrategy("newBufferManagerRead");
            }
            return new BufferManagerReadGrow(orb);
        }
        else {
            switch (n) {
                case 0: {
                    return new BufferManagerReadGrow(orb);
                }
                case 1: {
                    throw new INTERNAL("Collect strategy invalid for reading");
                }
                case 2: {
                    return new BufferManagerReadStream(orb);
                }
                default: {
                    throw new INTERNAL("Unknown buffer manager read strategy: " + n);
                }
            }
        }
    }
    
    public static BufferManagerWrite newBufferManagerWrite(final int n, final byte b, final ORB orb) {
        if (b != 0) {
            if (n != 0) {
                throw ORBUtilSystemException.get(orb, "rpc.encoding").invalidBuffMgrStrategy("newBufferManagerWrite");
            }
            return new BufferManagerWriteGrow(orb);
        }
        else {
            switch (n) {
                case 0: {
                    return new BufferManagerWriteGrow(orb);
                }
                case 1: {
                    return new BufferManagerWriteCollect(orb);
                }
                case 2: {
                    return new BufferManagerWriteStream(orb);
                }
                default: {
                    throw new INTERNAL("Unknown buffer manager write strategy: " + n);
                }
            }
        }
    }
    
    public static BufferManagerWrite newBufferManagerWrite(final GIOPVersion giopVersion, final byte b, final ORB orb) {
        if (b != 0) {
            return new BufferManagerWriteGrow(orb);
        }
        return newBufferManagerWrite(orb.getORBData().getGIOPBuffMgrStrategy(giopVersion), b, orb);
    }
    
    public static BufferManagerRead defaultBufferManagerRead(final ORB orb) {
        return new BufferManagerReadGrow(orb);
    }
}
