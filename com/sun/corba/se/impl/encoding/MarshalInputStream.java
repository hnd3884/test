package com.sun.corba.se.impl.encoding;

import java.io.Serializable;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Object;

public interface MarshalInputStream
{
    boolean read_boolean();
    
    char read_char();
    
    char read_wchar();
    
    byte read_octet();
    
    short read_short();
    
    short read_ushort();
    
    int read_long();
    
    int read_ulong();
    
    long read_longlong();
    
    long read_ulonglong();
    
    float read_float();
    
    double read_double();
    
    String read_string();
    
    String read_wstring();
    
    void read_boolean_array(final boolean[] p0, final int p1, final int p2);
    
    void read_char_array(final char[] p0, final int p1, final int p2);
    
    void read_wchar_array(final char[] p0, final int p1, final int p2);
    
    void read_octet_array(final byte[] p0, final int p1, final int p2);
    
    void read_short_array(final short[] p0, final int p1, final int p2);
    
    void read_ushort_array(final short[] p0, final int p1, final int p2);
    
    void read_long_array(final int[] p0, final int p1, final int p2);
    
    void read_ulong_array(final int[] p0, final int p1, final int p2);
    
    void read_longlong_array(final long[] p0, final int p1, final int p2);
    
    void read_ulonglong_array(final long[] p0, final int p1, final int p2);
    
    void read_float_array(final float[] p0, final int p1, final int p2);
    
    void read_double_array(final double[] p0, final int p1, final int p2);
    
    org.omg.CORBA.Object read_Object();
    
    TypeCode read_TypeCode();
    
    Any read_any();
    
    Principal read_Principal();
    
    org.omg.CORBA.Object read_Object(final Class p0);
    
    Serializable read_value() throws Exception;
    
    void consumeEndian();
    
    int getPosition();
    
    void mark(final int p0);
    
    void reset();
    
    void performORBVersionSpecificInit();
    
    void resetCodeSetConverters();
}
