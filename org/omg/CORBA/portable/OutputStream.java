package org.omg.CORBA.portable;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Context;
import java.math.BigDecimal;
import java.io.IOException;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Object;

public abstract class OutputStream extends java.io.OutputStream
{
    public abstract InputStream create_input_stream();
    
    public abstract void write_boolean(final boolean p0);
    
    public abstract void write_char(final char p0);
    
    public abstract void write_wchar(final char p0);
    
    public abstract void write_octet(final byte p0);
    
    public abstract void write_short(final short p0);
    
    public abstract void write_ushort(final short p0);
    
    public abstract void write_long(final int p0);
    
    public abstract void write_ulong(final int p0);
    
    public abstract void write_longlong(final long p0);
    
    public abstract void write_ulonglong(final long p0);
    
    public abstract void write_float(final float p0);
    
    public abstract void write_double(final double p0);
    
    public abstract void write_string(final String p0);
    
    public abstract void write_wstring(final String p0);
    
    public abstract void write_boolean_array(final boolean[] p0, final int p1, final int p2);
    
    public abstract void write_char_array(final char[] p0, final int p1, final int p2);
    
    public abstract void write_wchar_array(final char[] p0, final int p1, final int p2);
    
    public abstract void write_octet_array(final byte[] p0, final int p1, final int p2);
    
    public abstract void write_short_array(final short[] p0, final int p1, final int p2);
    
    public abstract void write_ushort_array(final short[] p0, final int p1, final int p2);
    
    public abstract void write_long_array(final int[] p0, final int p1, final int p2);
    
    public abstract void write_ulong_array(final int[] p0, final int p1, final int p2);
    
    public abstract void write_longlong_array(final long[] p0, final int p1, final int p2);
    
    public abstract void write_ulonglong_array(final long[] p0, final int p1, final int p2);
    
    public abstract void write_float_array(final float[] p0, final int p1, final int p2);
    
    public abstract void write_double_array(final double[] p0, final int p1, final int p2);
    
    public abstract void write_Object(final org.omg.CORBA.Object p0);
    
    public abstract void write_TypeCode(final TypeCode p0);
    
    public abstract void write_any(final Any p0);
    
    @Deprecated
    public void write_Principal(final Principal principal) {
        throw new NO_IMPLEMENT();
    }
    
    @Override
    public void write(final int n) throws IOException {
        throw new NO_IMPLEMENT();
    }
    
    public void write_fixed(final BigDecimal bigDecimal) {
        throw new NO_IMPLEMENT();
    }
    
    public void write_Context(final Context context, final ContextList list) {
        throw new NO_IMPLEMENT();
    }
    
    public ORB orb() {
        throw new NO_IMPLEMENT();
    }
}
