package org.omg.CORBA.portable;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Context;
import java.math.BigDecimal;
import java.io.IOException;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Object;

public abstract class InputStream extends java.io.InputStream
{
    public abstract boolean read_boolean();
    
    public abstract char read_char();
    
    public abstract char read_wchar();
    
    public abstract byte read_octet();
    
    public abstract short read_short();
    
    public abstract short read_ushort();
    
    public abstract int read_long();
    
    public abstract int read_ulong();
    
    public abstract long read_longlong();
    
    public abstract long read_ulonglong();
    
    public abstract float read_float();
    
    public abstract double read_double();
    
    public abstract String read_string();
    
    public abstract String read_wstring();
    
    public abstract void read_boolean_array(final boolean[] p0, final int p1, final int p2);
    
    public abstract void read_char_array(final char[] p0, final int p1, final int p2);
    
    public abstract void read_wchar_array(final char[] p0, final int p1, final int p2);
    
    public abstract void read_octet_array(final byte[] p0, final int p1, final int p2);
    
    public abstract void read_short_array(final short[] p0, final int p1, final int p2);
    
    public abstract void read_ushort_array(final short[] p0, final int p1, final int p2);
    
    public abstract void read_long_array(final int[] p0, final int p1, final int p2);
    
    public abstract void read_ulong_array(final int[] p0, final int p1, final int p2);
    
    public abstract void read_longlong_array(final long[] p0, final int p1, final int p2);
    
    public abstract void read_ulonglong_array(final long[] p0, final int p1, final int p2);
    
    public abstract void read_float_array(final float[] p0, final int p1, final int p2);
    
    public abstract void read_double_array(final double[] p0, final int p1, final int p2);
    
    public abstract org.omg.CORBA.Object read_Object();
    
    public abstract TypeCode read_TypeCode();
    
    public abstract Any read_any();
    
    @Deprecated
    public Principal read_Principal() {
        throw new NO_IMPLEMENT();
    }
    
    @Override
    public int read() throws IOException {
        throw new NO_IMPLEMENT();
    }
    
    public BigDecimal read_fixed() {
        throw new NO_IMPLEMENT();
    }
    
    public Context read_Context() {
        throw new NO_IMPLEMENT();
    }
    
    public org.omg.CORBA.Object read_Object(final Class clazz) {
        throw new NO_IMPLEMENT();
    }
    
    public ORB orb() {
        throw new NO_IMPLEMENT();
    }
}
