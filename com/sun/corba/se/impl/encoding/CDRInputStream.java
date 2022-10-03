package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.org.omg.SendingContext.CodeBase;
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
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import org.omg.CORBA.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA.DataInputStream;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class CDRInputStream extends InputStream implements MarshalInputStream, DataInputStream, ValueInputStream
{
    protected CorbaMessageMediator messageMediator;
    private CDRInputStreamBase impl;
    
    public CDRInputStream() {
    }
    
    public CDRInputStream(final CDRInputStream cdrInputStream) {
        (this.impl = cdrInputStream.impl.dup()).setParent(this);
    }
    
    public CDRInputStream(final ORB orb, final ByteBuffer byteBuffer, final int n, final boolean b, final GIOPVersion giopVersion, final byte b2, final BufferManagerRead bufferManagerRead) {
        (this.impl = InputStreamFactory.newInputStream((com.sun.corba.se.spi.orb.ORB)orb, giopVersion, b2)).init(orb, byteBuffer, n, b, bufferManagerRead);
        this.impl.setParent(this);
    }
    
    @Override
    public final boolean read_boolean() {
        return this.impl.read_boolean();
    }
    
    @Override
    public final char read_char() {
        return this.impl.read_char();
    }
    
    @Override
    public final char read_wchar() {
        return this.impl.read_wchar();
    }
    
    @Override
    public final byte read_octet() {
        return this.impl.read_octet();
    }
    
    @Override
    public final short read_short() {
        return this.impl.read_short();
    }
    
    @Override
    public final short read_ushort() {
        return this.impl.read_ushort();
    }
    
    @Override
    public final int read_long() {
        return this.impl.read_long();
    }
    
    @Override
    public final int read_ulong() {
        return this.impl.read_ulong();
    }
    
    @Override
    public final long read_longlong() {
        return this.impl.read_longlong();
    }
    
    @Override
    public final long read_ulonglong() {
        return this.impl.read_ulonglong();
    }
    
    @Override
    public final float read_float() {
        return this.impl.read_float();
    }
    
    @Override
    public final double read_double() {
        return this.impl.read_double();
    }
    
    @Override
    public final String read_string() {
        return this.impl.read_string();
    }
    
    @Override
    public final String read_wstring() {
        return this.impl.read_wstring();
    }
    
    @Override
    public final void read_boolean_array(final boolean[] array, final int n, final int n2) {
        this.impl.read_boolean_array(array, n, n2);
    }
    
    @Override
    public final void read_char_array(final char[] array, final int n, final int n2) {
        this.impl.read_char_array(array, n, n2);
    }
    
    @Override
    public final void read_wchar_array(final char[] array, final int n, final int n2) {
        this.impl.read_wchar_array(array, n, n2);
    }
    
    @Override
    public final void read_octet_array(final byte[] array, final int n, final int n2) {
        this.impl.read_octet_array(array, n, n2);
    }
    
    @Override
    public final void read_short_array(final short[] array, final int n, final int n2) {
        this.impl.read_short_array(array, n, n2);
    }
    
    @Override
    public final void read_ushort_array(final short[] array, final int n, final int n2) {
        this.impl.read_ushort_array(array, n, n2);
    }
    
    @Override
    public final void read_long_array(final int[] array, final int n, final int n2) {
        this.impl.read_long_array(array, n, n2);
    }
    
    @Override
    public final void read_ulong_array(final int[] array, final int n, final int n2) {
        this.impl.read_ulong_array(array, n, n2);
    }
    
    @Override
    public final void read_longlong_array(final long[] array, final int n, final int n2) {
        this.impl.read_longlong_array(array, n, n2);
    }
    
    @Override
    public final void read_ulonglong_array(final long[] array, final int n, final int n2) {
        this.impl.read_ulonglong_array(array, n, n2);
    }
    
    @Override
    public final void read_float_array(final float[] array, final int n, final int n2) {
        this.impl.read_float_array(array, n, n2);
    }
    
    @Override
    public final void read_double_array(final double[] array, final int n, final int n2) {
        this.impl.read_double_array(array, n, n2);
    }
    
    @Override
    public final org.omg.CORBA.Object read_Object() {
        return this.impl.read_Object();
    }
    
    @Override
    public final TypeCode read_TypeCode() {
        return this.impl.read_TypeCode();
    }
    
    @Override
    public final Any read_any() {
        return this.impl.read_any();
    }
    
    @Override
    public final Principal read_Principal() {
        return this.impl.read_Principal();
    }
    
    @Override
    public final int read() throws IOException {
        return this.impl.read();
    }
    
    @Override
    public final BigDecimal read_fixed() {
        return this.impl.read_fixed();
    }
    
    @Override
    public final Context read_Context() {
        return this.impl.read_Context();
    }
    
    @Override
    public final org.omg.CORBA.Object read_Object(final Class clazz) {
        return this.impl.read_Object(clazz);
    }
    
    @Override
    public final ORB orb() {
        return this.impl.orb();
    }
    
    @Override
    public final Serializable read_value() {
        return this.impl.read_value();
    }
    
    @Override
    public final Serializable read_value(final Class clazz) {
        return this.impl.read_value(clazz);
    }
    
    @Override
    public final Serializable read_value(final BoxedValueHelper boxedValueHelper) {
        return this.impl.read_value(boxedValueHelper);
    }
    
    @Override
    public final Serializable read_value(final String s) {
        return this.impl.read_value(s);
    }
    
    @Override
    public final Serializable read_value(final Serializable s) {
        return this.impl.read_value(s);
    }
    
    @Override
    public final Object read_abstract_interface() {
        return this.impl.read_abstract_interface();
    }
    
    @Override
    public final Object read_abstract_interface(final Class clazz) {
        return this.impl.read_abstract_interface(clazz);
    }
    
    @Override
    public final void consumeEndian() {
        this.impl.consumeEndian();
    }
    
    @Override
    public final int getPosition() {
        return this.impl.getPosition();
    }
    
    @Override
    public final Object read_Abstract() {
        return this.impl.read_Abstract();
    }
    
    @Override
    public final Serializable read_Value() {
        return this.impl.read_Value();
    }
    
    @Override
    public final void read_any_array(final AnySeqHolder anySeqHolder, final int n, final int n2) {
        this.impl.read_any_array(anySeqHolder, n, n2);
    }
    
    @Override
    public final void read_boolean_array(final BooleanSeqHolder booleanSeqHolder, final int n, final int n2) {
        this.impl.read_boolean_array(booleanSeqHolder, n, n2);
    }
    
    @Override
    public final void read_char_array(final CharSeqHolder charSeqHolder, final int n, final int n2) {
        this.impl.read_char_array(charSeqHolder, n, n2);
    }
    
    @Override
    public final void read_wchar_array(final WCharSeqHolder wCharSeqHolder, final int n, final int n2) {
        this.impl.read_wchar_array(wCharSeqHolder, n, n2);
    }
    
    @Override
    public final void read_octet_array(final OctetSeqHolder octetSeqHolder, final int n, final int n2) {
        this.impl.read_octet_array(octetSeqHolder, n, n2);
    }
    
    @Override
    public final void read_short_array(final ShortSeqHolder shortSeqHolder, final int n, final int n2) {
        this.impl.read_short_array(shortSeqHolder, n, n2);
    }
    
    @Override
    public final void read_ushort_array(final UShortSeqHolder uShortSeqHolder, final int n, final int n2) {
        this.impl.read_ushort_array(uShortSeqHolder, n, n2);
    }
    
    @Override
    public final void read_long_array(final LongSeqHolder longSeqHolder, final int n, final int n2) {
        this.impl.read_long_array(longSeqHolder, n, n2);
    }
    
    @Override
    public final void read_ulong_array(final ULongSeqHolder uLongSeqHolder, final int n, final int n2) {
        this.impl.read_ulong_array(uLongSeqHolder, n, n2);
    }
    
    @Override
    public final void read_ulonglong_array(final ULongLongSeqHolder uLongLongSeqHolder, final int n, final int n2) {
        this.impl.read_ulonglong_array(uLongLongSeqHolder, n, n2);
    }
    
    @Override
    public final void read_longlong_array(final LongLongSeqHolder longLongSeqHolder, final int n, final int n2) {
        this.impl.read_longlong_array(longLongSeqHolder, n, n2);
    }
    
    @Override
    public final void read_float_array(final FloatSeqHolder floatSeqHolder, final int n, final int n2) {
        this.impl.read_float_array(floatSeqHolder, n, n2);
    }
    
    @Override
    public final void read_double_array(final DoubleSeqHolder doubleSeqHolder, final int n, final int n2) {
        this.impl.read_double_array(doubleSeqHolder, n, n2);
    }
    
    @Override
    public final String[] _truncatable_ids() {
        return this.impl._truncatable_ids();
    }
    
    @Override
    public final int read(final byte[] array) throws IOException {
        return this.impl.read(array);
    }
    
    @Override
    public final int read(final byte[] array, final int n, final int n2) throws IOException {
        return this.impl.read(array, n, n2);
    }
    
    @Override
    public final long skip(final long n) throws IOException {
        return this.impl.skip(n);
    }
    
    @Override
    public final int available() throws IOException {
        return this.impl.available();
    }
    
    @Override
    public final void close() throws IOException {
        this.impl.close();
    }
    
    @Override
    public final void mark(final int n) {
        this.impl.mark(n);
    }
    
    @Override
    public final void reset() {
        this.impl.reset();
    }
    
    @Override
    public final boolean markSupported() {
        return this.impl.markSupported();
    }
    
    public abstract CDRInputStream dup();
    
    public final BigDecimal read_fixed(final short n, final short n2) {
        return this.impl.read_fixed(n, n2);
    }
    
    public final boolean isLittleEndian() {
        return this.impl.isLittleEndian();
    }
    
    protected final ByteBuffer getByteBuffer() {
        return this.impl.getByteBuffer();
    }
    
    protected final void setByteBuffer(final ByteBuffer byteBuffer) {
        this.impl.setByteBuffer(byteBuffer);
    }
    
    protected final void setByteBufferWithInfo(final ByteBufferWithInfo byteBufferWithInfo) {
        this.impl.setByteBufferWithInfo(byteBufferWithInfo);
    }
    
    protected final boolean isSharing(final ByteBuffer byteBuffer) {
        return this.getByteBuffer() == byteBuffer;
    }
    
    public final int getBufferLength() {
        return this.impl.getBufferLength();
    }
    
    protected final void setBufferLength(final int bufferLength) {
        this.impl.setBufferLength(bufferLength);
    }
    
    protected final int getIndex() {
        return this.impl.getIndex();
    }
    
    protected final void setIndex(final int index) {
        this.impl.setIndex(index);
    }
    
    public final void orb(final ORB orb) {
        this.impl.orb(orb);
    }
    
    public final GIOPVersion getGIOPVersion() {
        return this.impl.getGIOPVersion();
    }
    
    public final BufferManagerRead getBufferManager() {
        return this.impl.getBufferManager();
    }
    
    public CodeBase getCodeBase() {
        return null;
    }
    
    protected CodeSetConversion.BTCConverter createCharBTCConverter() {
        return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1, this.impl.isLittleEndian());
    }
    
    protected abstract CodeSetConversion.BTCConverter createWCharBTCConverter();
    
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
    public void performORBVersionSpecificInit() {
        if (this.impl != null) {
            this.impl.performORBVersionSpecificInit();
        }
    }
    
    @Override
    public void resetCodeSetConverters() {
        this.impl.resetCodeSetConverters();
    }
    
    public void setMessageMediator(final MessageMediator messageMediator) {
        this.messageMediator = (CorbaMessageMediator)messageMediator;
    }
    
    public MessageMediator getMessageMediator() {
        return this.messageMediator;
    }
    
    @Override
    public void start_value() {
        this.impl.start_value();
    }
    
    @Override
    public void end_value() {
        this.impl.end_value();
    }
    
    private static class InputStreamFactory
    {
        public static CDRInputStreamBase newInputStream(final com.sun.corba.se.spi.orb.ORB orb, final GIOPVersion giopVersion, final byte b) {
            switch (giopVersion.intValue()) {
                case 256: {
                    return new CDRInputStream_1_0();
                }
                case 257: {
                    return new CDRInputStream_1_1();
                }
                case 258: {
                    if (b != 0) {
                        return new IDLJavaSerializationInputStream(b);
                    }
                    return new CDRInputStream_1_2();
                }
                default: {
                    throw ORBUtilSystemException.get(orb, "rpc.encoding").unsupportedGiopVersion(giopVersion);
                }
            }
        }
    }
}
