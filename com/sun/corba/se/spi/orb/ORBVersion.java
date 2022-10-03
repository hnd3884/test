package com.sun.corba.se.spi.orb;

import org.omg.CORBA.portable.OutputStream;

public interface ORBVersion extends Comparable
{
    public static final byte FOREIGN = 0;
    public static final byte OLD = 1;
    public static final byte NEW = 2;
    public static final byte JDK1_3_1_01 = 3;
    public static final byte NEWER = 10;
    public static final byte PEORB = 20;
    
    byte getORBType();
    
    void write(final OutputStream p0);
    
    boolean lessThan(final ORBVersion p0);
}
