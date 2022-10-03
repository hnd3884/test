package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import org.omg.CORBA.portable.BoxedValueHelper;
import java.io.Serializable;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Context;
import java.math.BigDecimal;
import java.io.IOException;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Object;
import org.omg.CORBA.ORB;
import java.io.OutputStream;

abstract class CDROutputStreamBase extends OutputStream
{
    protected CDROutputStream parent;
    
    public void setParent(final CDROutputStream parent) {
        this.parent = parent;
    }
    
    public void init(final ORB orb, final BufferManagerWrite bufferManagerWrite, final byte b) {
        this.init(orb, false, bufferManagerWrite, b, true);
    }
    
    protected abstract void init(final ORB p0, final boolean p1, final BufferManagerWrite p2, final byte p3, final boolean p4);
    
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
    
    public abstract void write_Principal(final Principal p0);
    
    @Override
    public void write(final int n) throws IOException {
        throw new NO_IMPLEMENT();
    }
    
    public abstract void write_fixed(final BigDecimal p0);
    
    public void write_Context(final Context context, final ContextList list) {
        throw new NO_IMPLEMENT();
    }
    
    public abstract ORB orb();
    
    public abstract void write_value(final Serializable p0);
    
    public abstract void write_value(final Serializable p0, final Class p1);
    
    public abstract void write_value(final Serializable p0, final String p1);
    
    public abstract void write_value(final Serializable p0, final BoxedValueHelper p1);
    
    public abstract void write_abstract_interface(final Object p0);
    
    public abstract void start_block();
    
    public abstract void end_block();
    
    public abstract void putEndian();
    
    public abstract void writeTo(final OutputStream p0) throws IOException;
    
    public abstract byte[] toByteArray();
    
    public abstract void write_Abstract(final Object p0);
    
    public abstract void write_Value(final Serializable p0);
    
    public abstract void write_any_array(final Any[] p0, final int p1, final int p2);
    
    public abstract String[] _truncatable_ids();
    
    abstract void setHeaderPadding(final boolean p0);
    
    public abstract int getSize();
    
    public abstract int getIndex();
    
    public abstract void setIndex(final int p0);
    
    public abstract ByteBuffer getByteBuffer();
    
    public abstract void setByteBuffer(final ByteBuffer p0);
    
    public abstract boolean isLittleEndian();
    
    public abstract ByteBufferWithInfo getByteBufferWithInfo();
    
    public abstract void setByteBufferWithInfo(final ByteBufferWithInfo p0);
    
    public abstract BufferManagerWrite getBufferManager();
    
    public abstract void write_fixed(final BigDecimal p0, final short p1, final short p2);
    
    public abstract void writeOctetSequenceTo(final org.omg.CORBA.portable.OutputStream p0);
    
    public abstract GIOPVersion getGIOPVersion();
    
    public abstract void writeIndirection(final int p0, final int p1);
    
    abstract void freeInternalCaches();
    
    abstract void printBuffer();
    
    abstract void alignOnBoundary(final int p0);
    
    public abstract void start_value(final String p0);
    
    public abstract void end_value();
}
