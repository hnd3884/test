package com.sun.corba.se.spi.orb;

import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.impl.orb.ORBVersionImpl;

public class ORBVersionFactory
{
    private ORBVersionFactory() {
    }
    
    public static ORBVersion getFOREIGN() {
        return ORBVersionImpl.FOREIGN;
    }
    
    public static ORBVersion getOLD() {
        return ORBVersionImpl.OLD;
    }
    
    public static ORBVersion getNEW() {
        return ORBVersionImpl.NEW;
    }
    
    public static ORBVersion getJDK1_3_1_01() {
        return ORBVersionImpl.JDK1_3_1_01;
    }
    
    public static ORBVersion getNEWER() {
        return ORBVersionImpl.NEWER;
    }
    
    public static ORBVersion getPEORB() {
        return ORBVersionImpl.PEORB;
    }
    
    public static ORBVersion getORBVersion() {
        return ORBVersionImpl.PEORB;
    }
    
    public static ORBVersion create(final InputStream inputStream) {
        return byteToVersion(inputStream.read_octet());
    }
    
    private static ORBVersion byteToVersion(final byte b) {
        switch (b) {
            case 0: {
                return ORBVersionImpl.FOREIGN;
            }
            case 1: {
                return ORBVersionImpl.OLD;
            }
            case 2: {
                return ORBVersionImpl.NEW;
            }
            case 3: {
                return ORBVersionImpl.JDK1_3_1_01;
            }
            case 10: {
                return ORBVersionImpl.NEWER;
            }
            case 20: {
                return ORBVersionImpl.PEORB;
            }
            default: {
                return new ORBVersionImpl(b);
            }
        }
    }
}
