package com.sun.corba.se.impl.orb;

import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.spi.orb.ORBVersion;

public class ORBVersionImpl implements ORBVersion
{
    private byte orbType;
    public static final ORBVersion FOREIGN;
    public static final ORBVersion OLD;
    public static final ORBVersion NEW;
    public static final ORBVersion JDK1_3_1_01;
    public static final ORBVersion NEWER;
    public static final ORBVersion PEORB;
    
    public ORBVersionImpl(final byte orbType) {
        this.orbType = orbType;
    }
    
    @Override
    public byte getORBType() {
        return this.orbType;
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        outputStream.write_octet(this.orbType);
    }
    
    @Override
    public String toString() {
        return "ORBVersionImpl[" + Byte.toString(this.orbType) + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ORBVersion && ((ORBVersion)o).getORBType() == this.orbType;
    }
    
    @Override
    public int hashCode() {
        return this.orbType;
    }
    
    @Override
    public boolean lessThan(final ORBVersion orbVersion) {
        return this.orbType < orbVersion.getORBType();
    }
    
    @Override
    public int compareTo(final Object o) {
        return this.getORBType() - ((ORBVersion)o).getORBType();
    }
    
    static {
        FOREIGN = new ORBVersionImpl((byte)0);
        OLD = new ORBVersionImpl((byte)1);
        NEW = new ORBVersionImpl((byte)2);
        JDK1_3_1_01 = new ORBVersionImpl((byte)3);
        NEWER = new ORBVersionImpl((byte)10);
        PEORB = new ORBVersionImpl((byte)20);
    }
}
