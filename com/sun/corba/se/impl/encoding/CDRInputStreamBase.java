package com.sun.corba.se.impl.encoding;

import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.ULongLongSeqHolder;
import org.omg.CORBA.ULongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.UShortSeqHolder;
import org.omg.CORBA.ShortSeqHolder;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA.WCharSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.portable.BoxedValueHelper;
import java.io.Serializable;
import org.omg.CORBA.Context;
import java.math.BigDecimal;
import java.io.IOException;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Object;
import java.nio.ByteBuffer;
import org.omg.CORBA.ORB;
import java.io.InputStream;

abstract class CDRInputStreamBase extends InputStream
{
    protected CDRInputStream parent;
    
    public void setParent(final CDRInputStream parent) {
        this.parent = parent;
    }
    
    public abstract void init(final ORB p0, final ByteBuffer p1, final int p2, final boolean p3, final BufferManagerRead p4);
    
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
    
    public abstract Principal read_Principal();
    
    @Override
    public int read() throws IOException {
        throw new NO_IMPLEMENT();
    }
    
    public abstract BigDecimal read_fixed();
    
    public Context read_Context() {
        throw new NO_IMPLEMENT();
    }
    
    public abstract org.omg.CORBA.Object read_Object(final Class p0);
    
    public abstract ORB orb();
    
    public abstract Serializable read_value();
    
    public abstract Serializable read_value(final Class p0);
    
    public abstract Serializable read_value(final BoxedValueHelper p0);
    
    public abstract Serializable read_value(final String p0);
    
    public abstract Serializable read_value(final Serializable p0);
    
    public abstract Object read_abstract_interface();
    
    public abstract Object read_abstract_interface(final Class p0);
    
    public abstract void consumeEndian();
    
    public abstract int getPosition();
    
    public abstract Object read_Abstract();
    
    public abstract Serializable read_Value();
    
    public abstract void read_any_array(final AnySeqHolder p0, final int p1, final int p2);
    
    public abstract void read_boolean_array(final BooleanSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_char_array(final CharSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_wchar_array(final WCharSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_octet_array(final OctetSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_short_array(final ShortSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_ushort_array(final UShortSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_long_array(final LongSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_ulong_array(final ULongSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_ulonglong_array(final ULongLongSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_longlong_array(final LongLongSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_float_array(final FloatSeqHolder p0, final int p1, final int p2);
    
    public abstract void read_double_array(final DoubleSeqHolder p0, final int p1, final int p2);
    
    public abstract String[] _truncatable_ids();
    
    @Override
    public abstract void mark(final int p0);
    
    @Override
    public abstract void reset();
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    public abstract CDRInputStreamBase dup();
    
    public abstract BigDecimal read_fixed(final short p0, final short p1);
    
    public abstract boolean isLittleEndian();
    
    abstract void setHeaderPadding(final boolean p0);
    
    public abstract ByteBuffer getByteBuffer();
    
    public abstract void setByteBuffer(final ByteBuffer p0);
    
    public abstract void setByteBufferWithInfo(final ByteBufferWithInfo p0);
    
    public abstract int getBufferLength();
    
    public abstract void setBufferLength(final int p0);
    
    public abstract int getIndex();
    
    public abstract void setIndex(final int p0);
    
    public abstract void orb(final ORB p0);
    
    public abstract BufferManagerRead getBufferManager();
    
    public abstract GIOPVersion getGIOPVersion();
    
    abstract CodeBase getCodeBase();
    
    abstract void printBuffer();
    
    abstract void alignOnBoundary(final int p0);
    
    abstract void performORBVersionSpecificInit();
    
    public abstract void resetCodeSetConverters();
    
    public abstract void start_value();
    
    public abstract void end_value();
}
