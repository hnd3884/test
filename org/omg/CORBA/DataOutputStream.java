package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.ValueBase;

public interface DataOutputStream extends ValueBase
{
    void write_any(final Any p0);
    
    void write_boolean(final boolean p0);
    
    void write_char(final char p0);
    
    void write_wchar(final char p0);
    
    void write_octet(final byte p0);
    
    void write_short(final short p0);
    
    void write_ushort(final short p0);
    
    void write_long(final int p0);
    
    void write_ulong(final int p0);
    
    void write_longlong(final long p0);
    
    void write_ulonglong(final long p0);
    
    void write_float(final float p0);
    
    void write_double(final double p0);
    
    void write_string(final String p0);
    
    void write_wstring(final String p0);
    
    void write_Object(final org.omg.CORBA.Object p0);
    
    void write_Abstract(final Object p0);
    
    void write_Value(final Serializable p0);
    
    void write_TypeCode(final TypeCode p0);
    
    void write_any_array(final Any[] p0, final int p1, final int p2);
    
    void write_boolean_array(final boolean[] p0, final int p1, final int p2);
    
    void write_char_array(final char[] p0, final int p1, final int p2);
    
    void write_wchar_array(final char[] p0, final int p1, final int p2);
    
    void write_octet_array(final byte[] p0, final int p1, final int p2);
    
    void write_short_array(final short[] p0, final int p1, final int p2);
    
    void write_ushort_array(final short[] p0, final int p1, final int p2);
    
    void write_long_array(final int[] p0, final int p1, final int p2);
    
    void write_ulong_array(final int[] p0, final int p1, final int p2);
    
    void write_ulonglong_array(final long[] p0, final int p1, final int p2);
    
    void write_longlong_array(final long[] p0, final int p1, final int p2);
    
    void write_float_array(final float[] p0, final int p1, final int p2);
    
    void write_double_array(final double[] p0, final int p1, final int p2);
}
