package com.sun.corba.se.impl.encoding;

import java.nio.ByteBuffer;
import com.sun.corba.se.pept.protocol.MessageMediator;
import org.omg.CORBA.portable.BoxedValueHelper;
import java.io.Serializable;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Context;
import java.math.BigDecimal;
import java.io.IOException;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.CORBA.DataOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class CDROutputStream extends OutputStream implements MarshalOutputStream, DataOutputStream, ValueOutputStream
{
    private CDROutputStreamBase impl;
    protected ORB orb;
    protected ORBUtilSystemException wrapper;
    protected CorbaMessageMediator corbaMessageMediator;
    
    public CDROutputStream(final ORB orb, final GIOPVersion giopVersion, final byte b, final boolean b2, final BufferManagerWrite bufferManagerWrite, final byte b3, final boolean b4) {
        (this.impl = OutputStreamFactory.newOutputStream(orb, giopVersion, b)).init(orb, b2, bufferManagerWrite, b3, b4);
        this.impl.setParent(this);
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.encoding");
    }
    
    public CDROutputStream(final ORB orb, final GIOPVersion giopVersion, final byte b, final boolean b2, final BufferManagerWrite bufferManagerWrite, final byte b3) {
        this(orb, giopVersion, b, b2, bufferManagerWrite, b3, true);
    }
    
    @Override
    public abstract InputStream create_input_stream();
    
    @Override
    public final void write_boolean(final boolean b) {
        this.impl.write_boolean(b);
    }
    
    @Override
    public final void write_char(final char c) {
        this.impl.write_char(c);
    }
    
    @Override
    public final void write_wchar(final char c) {
        this.impl.write_wchar(c);
    }
    
    @Override
    public final void write_octet(final byte b) {
        this.impl.write_octet(b);
    }
    
    @Override
    public final void write_short(final short n) {
        this.impl.write_short(n);
    }
    
    @Override
    public final void write_ushort(final short n) {
        this.impl.write_ushort(n);
    }
    
    @Override
    public final void write_long(final int n) {
        this.impl.write_long(n);
    }
    
    @Override
    public final void write_ulong(final int n) {
        this.impl.write_ulong(n);
    }
    
    @Override
    public final void write_longlong(final long n) {
        this.impl.write_longlong(n);
    }
    
    @Override
    public final void write_ulonglong(final long n) {
        this.impl.write_ulonglong(n);
    }
    
    @Override
    public final void write_float(final float n) {
        this.impl.write_float(n);
    }
    
    @Override
    public final void write_double(final double n) {
        this.impl.write_double(n);
    }
    
    @Override
    public final void write_string(final String s) {
        this.impl.write_string(s);
    }
    
    @Override
    public final void write_wstring(final String s) {
        this.impl.write_wstring(s);
    }
    
    @Override
    public final void write_boolean_array(final boolean[] array, final int n, final int n2) {
        this.impl.write_boolean_array(array, n, n2);
    }
    
    @Override
    public final void write_char_array(final char[] array, final int n, final int n2) {
        this.impl.write_char_array(array, n, n2);
    }
    
    @Override
    public final void write_wchar_array(final char[] array, final int n, final int n2) {
        this.impl.write_wchar_array(array, n, n2);
    }
    
    @Override
    public final void write_octet_array(final byte[] array, final int n, final int n2) {
        this.impl.write_octet_array(array, n, n2);
    }
    
    @Override
    public final void write_short_array(final short[] array, final int n, final int n2) {
        this.impl.write_short_array(array, n, n2);
    }
    
    @Override
    public final void write_ushort_array(final short[] array, final int n, final int n2) {
        this.impl.write_ushort_array(array, n, n2);
    }
    
    @Override
    public final void write_long_array(final int[] array, final int n, final int n2) {
        this.impl.write_long_array(array, n, n2);
    }
    
    @Override
    public final void write_ulong_array(final int[] array, final int n, final int n2) {
        this.impl.write_ulong_array(array, n, n2);
    }
    
    @Override
    public final void write_longlong_array(final long[] array, final int n, final int n2) {
        this.impl.write_longlong_array(array, n, n2);
    }
    
    @Override
    public final void write_ulonglong_array(final long[] array, final int n, final int n2) {
        this.impl.write_ulonglong_array(array, n, n2);
    }
    
    @Override
    public final void write_float_array(final float[] array, final int n, final int n2) {
        this.impl.write_float_array(array, n, n2);
    }
    
    @Override
    public final void write_double_array(final double[] array, final int n, final int n2) {
        this.impl.write_double_array(array, n, n2);
    }
    
    @Override
    public final void write_Object(final org.omg.CORBA.Object object) {
        this.impl.write_Object(object);
    }
    
    @Override
    public final void write_TypeCode(final TypeCode typeCode) {
        this.impl.write_TypeCode(typeCode);
    }
    
    @Override
    public final void write_any(final Any any) {
        this.impl.write_any(any);
    }
    
    @Override
    public final void write_Principal(final Principal principal) {
        this.impl.write_Principal(principal);
    }
    
    @Override
    public final void write(final int n) throws IOException {
        this.impl.write(n);
    }
    
    @Override
    public final void write_fixed(final BigDecimal bigDecimal) {
        this.impl.write_fixed(bigDecimal);
    }
    
    @Override
    public final void write_Context(final Context context, final ContextList list) {
        this.impl.write_Context(context, list);
    }
    
    @Override
    public final org.omg.CORBA.ORB orb() {
        return this.impl.orb();
    }
    
    @Override
    public final void write_value(final Serializable s) {
        this.impl.write_value(s);
    }
    
    @Override
    public final void write_value(final Serializable s, final Class clazz) {
        this.impl.write_value(s, clazz);
    }
    
    @Override
    public final void write_value(final Serializable s, final String s2) {
        this.impl.write_value(s, s2);
    }
    
    @Override
    public final void write_value(final Serializable s, final BoxedValueHelper boxedValueHelper) {
        this.impl.write_value(s, boxedValueHelper);
    }
    
    @Override
    public final void write_abstract_interface(final Object o) {
        this.impl.write_abstract_interface(o);
    }
    
    @Override
    public final void write(final byte[] array) throws IOException {
        this.impl.write(array);
    }
    
    @Override
    public final void write(final byte[] array, final int n, final int n2) throws IOException {
        this.impl.write(array, n, n2);
    }
    
    @Override
    public final void flush() throws IOException {
        this.impl.flush();
    }
    
    @Override
    public final void close() throws IOException {
        this.impl.close();
    }
    
    @Override
    public final void start_block() {
        this.impl.start_block();
    }
    
    @Override
    public final void end_block() {
        this.impl.end_block();
    }
    
    @Override
    public final void putEndian() {
        this.impl.putEndian();
    }
    
    @Override
    public void writeTo(final java.io.OutputStream outputStream) throws IOException {
        this.impl.writeTo(outputStream);
    }
    
    @Override
    public final byte[] toByteArray() {
        return this.impl.toByteArray();
    }
    
    @Override
    public final void write_Abstract(final Object o) {
        this.impl.write_Abstract(o);
    }
    
    @Override
    public final void write_Value(final Serializable s) {
        this.impl.write_Value(s);
    }
    
    @Override
    public final void write_any_array(final Any[] array, final int n, final int n2) {
        this.impl.write_any_array(array, n, n2);
    }
    
    public void setMessageMediator(final MessageMediator messageMediator) {
        this.corbaMessageMediator = (CorbaMessageMediator)messageMediator;
    }
    
    public MessageMediator getMessageMediator() {
        return this.corbaMessageMediator;
    }
    
    @Override
    public final String[] _truncatable_ids() {
        return this.impl._truncatable_ids();
    }
    
    protected final int getSize() {
        return this.impl.getSize();
    }
    
    protected final int getIndex() {
        return this.impl.getIndex();
    }
    
    protected int getRealIndex(final int n) {
        return n;
    }
    
    protected final void setIndex(final int index) {
        this.impl.setIndex(index);
    }
    
    protected final ByteBuffer getByteBuffer() {
        return this.impl.getByteBuffer();
    }
    
    protected final void setByteBuffer(final ByteBuffer byteBuffer) {
        this.impl.setByteBuffer(byteBuffer);
    }
    
    protected final boolean isSharing(final ByteBuffer byteBuffer) {
        return this.getByteBuffer() == byteBuffer;
    }
    
    public final boolean isLittleEndian() {
        return this.impl.isLittleEndian();
    }
    
    public ByteBufferWithInfo getByteBufferWithInfo() {
        return this.impl.getByteBufferWithInfo();
    }
    
    protected void setByteBufferWithInfo(final ByteBufferWithInfo byteBufferWithInfo) {
        this.impl.setByteBufferWithInfo(byteBufferWithInfo);
    }
    
    public final BufferManagerWrite getBufferManager() {
        return this.impl.getBufferManager();
    }
    
    public final void write_fixed(final BigDecimal bigDecimal, final short n, final short n2) {
        this.impl.write_fixed(bigDecimal, n, n2);
    }
    
    public final void writeOctetSequenceTo(final org.omg.CORBA.portable.OutputStream outputStream) {
        this.impl.writeOctetSequenceTo(outputStream);
    }
    
    public final GIOPVersion getGIOPVersion() {
        return this.impl.getGIOPVersion();
    }
    
    public final void writeIndirection(final int n, final int n2) {
        this.impl.writeIndirection(n, n2);
    }
    
    protected CodeSetConversion.CTBConverter createCharCTBConverter() {
        return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.ISO_8859_1);
    }
    
    protected abstract CodeSetConversion.CTBConverter createWCharCTBConverter();
    
    protected final void freeInternalCaches() {
        this.impl.freeInternalCaches();
    }
    
    void printBuffer() {
        this.impl.printBuffer();
    }
    
    public void alignOnBoundary(final int n) {
        this.impl.alignOnBoundary(n);
    }
    
    public void setHeaderPadding(final boolean headerPadding) {
        this.impl.setHeaderPadding(headerPadding);
    }
    
    @Override
    public void start_value(final String s) {
        this.impl.start_value(s);
    }
    
    @Override
    public void end_value() {
        this.impl.end_value();
    }
    
    private static class OutputStreamFactory
    {
        public static CDROutputStreamBase newOutputStream(final ORB orb, final GIOPVersion giopVersion, final byte b) {
            switch (giopVersion.intValue()) {
                case 256: {
                    return new CDROutputStream_1_0();
                }
                case 257: {
                    return new CDROutputStream_1_1();
                }
                case 258: {
                    if (b != 0) {
                        return new IDLJavaSerializationOutputStream(b);
                    }
                    return new CDROutputStream_1_2();
                }
                default: {
                    throw ORBUtilSystemException.get(orb, "rpc.encoding").unsupportedGiopVersion(giopVersion);
                }
            }
        }
    }
}
