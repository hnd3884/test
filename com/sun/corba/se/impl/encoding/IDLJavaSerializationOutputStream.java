package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.ByteArrayOutputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.omg.CORBA.portable.BoxedValueHelper;
import java.io.Serializable;
import java.math.BigDecimal;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import com.sun.corba.se.spi.ior.IORFactories;
import java.io.OutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.io.ObjectOutputStream;
import com.sun.corba.se.spi.orb.ORB;

final class IDLJavaSerializationOutputStream extends CDROutputStreamBase
{
    private ORB orb;
    private byte encodingVersion;
    private ObjectOutputStream os;
    private _ByteArrayOutputStream bos;
    private BufferManagerWrite bufferManager;
    private final int directWriteLength = 16;
    protected ORBUtilSystemException wrapper;
    
    public IDLJavaSerializationOutputStream(final byte encodingVersion) {
        this.encodingVersion = encodingVersion;
    }
    
    public void init(final org.omg.CORBA.ORB orb, final boolean b, final BufferManagerWrite bufferManager, final byte b2, final boolean b3) {
        this.orb = (ORB)orb;
        this.bufferManager = bufferManager;
        this.wrapper = ORBUtilSystemException.get((ORB)orb, "rpc.encoding");
        this.bos = new _ByteArrayOutputStream(1024);
    }
    
    private void initObjectOutputStream() {
        if (this.os != null) {
            throw this.wrapper.javaStreamInitFailed();
        }
        try {
            this.os = new MarshalObjectOutputStream(this.bos, this.orb);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaStreamInitFailed(ex);
        }
    }
    
    @Override
    public final void write_boolean(final boolean b) {
        try {
            this.os.writeBoolean(b);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_boolean");
        }
    }
    
    @Override
    public final void write_char(final char c) {
        try {
            this.os.writeChar(c);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_char");
        }
    }
    
    @Override
    public final void write_wchar(final char c) {
        this.write_char(c);
    }
    
    @Override
    public final void write_octet(final byte b) {
        if (this.bos.size() < 16) {
            this.bos.write(b);
            if (this.bos.size() == 16) {
                this.initObjectOutputStream();
            }
            return;
        }
        try {
            this.os.writeByte(b);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_octet");
        }
    }
    
    @Override
    public final void write_short(final short n) {
        try {
            this.os.writeShort(n);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_short");
        }
    }
    
    @Override
    public final void write_ushort(final short n) {
        this.write_short(n);
    }
    
    @Override
    public final void write_long(final int n) {
        if (this.bos.size() < 16) {
            this.bos.write((byte)(n >>> 24 & 0xFF));
            this.bos.write((byte)(n >>> 16 & 0xFF));
            this.bos.write((byte)(n >>> 8 & 0xFF));
            this.bos.write((byte)(n >>> 0 & 0xFF));
            if (this.bos.size() == 16) {
                this.initObjectOutputStream();
            }
            else if (this.bos.size() > 16) {
                this.wrapper.javaSerializationException("write_long");
            }
            return;
        }
        try {
            this.os.writeInt(n);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_long");
        }
    }
    
    @Override
    public final void write_ulong(final int n) {
        this.write_long(n);
    }
    
    @Override
    public final void write_longlong(final long n) {
        try {
            this.os.writeLong(n);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_longlong");
        }
    }
    
    @Override
    public final void write_ulonglong(final long n) {
        this.write_longlong(n);
    }
    
    @Override
    public final void write_float(final float n) {
        try {
            this.os.writeFloat(n);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_float");
        }
    }
    
    @Override
    public final void write_double(final double n) {
        try {
            this.os.writeDouble(n);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_double");
        }
    }
    
    @Override
    public final void write_string(final String s) {
        try {
            this.os.writeUTF(s);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_string");
        }
    }
    
    @Override
    public final void write_wstring(final String s) {
        try {
            this.os.writeObject(s);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_wstring");
        }
    }
    
    @Override
    public final void write_boolean_array(final boolean[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.write_boolean(array[n + i]);
        }
    }
    
    @Override
    public final void write_char_array(final char[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.write_char(array[n + i]);
        }
    }
    
    @Override
    public final void write_wchar_array(final char[] array, final int n, final int n2) {
        this.write_char_array(array, n, n2);
    }
    
    @Override
    public final void write_octet_array(final byte[] array, final int n, final int n2) {
        try {
            this.os.write(array, n, n2);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_octet_array");
        }
    }
    
    @Override
    public final void write_short_array(final short[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.write_short(array[n + i]);
        }
    }
    
    @Override
    public final void write_ushort_array(final short[] array, final int n, final int n2) {
        this.write_short_array(array, n, n2);
    }
    
    @Override
    public final void write_long_array(final int[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.write_long(array[n + i]);
        }
    }
    
    @Override
    public final void write_ulong_array(final int[] array, final int n, final int n2) {
        this.write_long_array(array, n, n2);
    }
    
    @Override
    public final void write_longlong_array(final long[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.write_longlong(array[n + i]);
        }
    }
    
    @Override
    public final void write_ulonglong_array(final long[] array, final int n, final int n2) {
        this.write_longlong_array(array, n, n2);
    }
    
    @Override
    public final void write_float_array(final float[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.write_float(array[n + i]);
        }
    }
    
    @Override
    public final void write_double_array(final double[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.write_double(array[n + i]);
        }
    }
    
    @Override
    public final void write_Object(final org.omg.CORBA.Object object) {
        if (object == null) {
            IORFactories.makeIOR(this.orb).write(this.parent);
            return;
        }
        if (object instanceof LocalObject) {
            throw this.wrapper.writeLocalObject(CompletionStatus.COMPLETED_MAYBE);
        }
        ORBUtility.connectAndGetIOR(this.orb, object).write(this.parent);
    }
    
    @Override
    public final void write_TypeCode(final TypeCode typeCode) {
        if (typeCode == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        TypeCodeImpl typeCodeImpl;
        if (typeCode instanceof TypeCodeImpl) {
            typeCodeImpl = (TypeCodeImpl)typeCode;
        }
        else {
            typeCodeImpl = new TypeCodeImpl(this.orb, typeCode);
        }
        typeCodeImpl.write_value(this.parent);
    }
    
    @Override
    public final void write_any(final Any any) {
        if (any == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.write_TypeCode(any.type());
        any.write_value(this.parent);
    }
    
    @Override
    public final void write_Principal(final Principal principal) {
        this.write_long(principal.name().length);
        this.write_octet_array(principal.name(), 0, principal.name().length);
    }
    
    @Override
    public final void write_fixed(final BigDecimal bigDecimal) {
        this.write_fixed(bigDecimal.toString(), bigDecimal.signum());
    }
    
    private void write_fixed(final String s, final int n) {
        final int length = s.length();
        byte b = 0;
        int n2 = 0;
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 != '-' && char1 != '+') {
                if (char1 != '.') {
                    ++n2;
                }
            }
        }
        for (int j = 0; j < length; ++j) {
            final char char2 = s.charAt(j);
            if (char2 != '-' && char2 != '+') {
                if (char2 != '.') {
                    final byte b2 = (byte)Character.digit(char2, 10);
                    if (b2 == -1) {
                        throw this.wrapper.badDigitInFixed(CompletionStatus.COMPLETED_MAYBE);
                    }
                    if (n2 % 2 == 0) {
                        this.write_octet((byte)(b | b2));
                        b = 0;
                    }
                    else {
                        b |= (byte)(b2 << 4);
                    }
                    --n2;
                }
            }
        }
        byte b3;
        if (n == -1) {
            b3 = (byte)(b | 0xD);
        }
        else {
            b3 = (byte)(b | 0xC);
        }
        this.write_octet(b3);
    }
    
    @Override
    public final org.omg.CORBA.ORB orb() {
        return this.orb;
    }
    
    @Override
    public final void write_value(final Serializable s) {
        this.write_value(s, (String)null);
    }
    
    @Override
    public final void write_value(final Serializable s, final Class clazz) {
        this.write_value(s);
    }
    
    @Override
    public final void write_value(final Serializable s, final String s2) {
        try {
            this.os.writeObject(s);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_value");
        }
    }
    
    @Override
    public final void write_value(final Serializable s, final BoxedValueHelper boxedValueHelper) {
        this.write_value(s, (String)null);
    }
    
    @Override
    public final void write_abstract_interface(final Object o) {
        boolean b = false;
        org.omg.CORBA.Object object = null;
        if (o != null && o instanceof org.omg.CORBA.Object) {
            object = (org.omg.CORBA.Object)o;
            b = true;
        }
        this.write_boolean(b);
        if (b) {
            this.write_Object(object);
        }
        else {
            try {
                this.write_value((Serializable)o);
            }
            catch (final ClassCastException ex) {
                if (o instanceof Serializable) {
                    throw ex;
                }
                ORBUtility.throwNotSerializableForCorba(o.getClass().getName());
            }
        }
    }
    
    @Override
    public final void start_block() {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public final void end_block() {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public final void putEndian() {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public void writeTo(final OutputStream outputStream) throws IOException {
        try {
            this.os.flush();
            this.bos.writeTo(outputStream);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "writeTo");
        }
    }
    
    @Override
    public final byte[] toByteArray() {
        try {
            this.os.flush();
            return this.bos.toByteArray();
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "toByteArray");
        }
    }
    
    @Override
    public final void write_Abstract(final Object o) {
        this.write_abstract_interface(o);
    }
    
    @Override
    public final void write_Value(final Serializable s) {
        this.write_value(s);
    }
    
    @Override
    public final void write_any_array(final Any[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.write_any(array[n + i]);
        }
    }
    
    @Override
    public final String[] _truncatable_ids() {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public final int getSize() {
        try {
            this.os.flush();
            return this.bos.size();
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "write_boolean");
        }
    }
    
    @Override
    public final int getIndex() {
        return this.getSize();
    }
    
    protected int getRealIndex(final int n) {
        return this.getSize();
    }
    
    @Override
    public final void setIndex(final int n) {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public final ByteBuffer getByteBuffer() {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public final void setByteBuffer(final ByteBuffer byteBuffer) {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public final boolean isLittleEndian() {
        return false;
    }
    
    @Override
    public ByteBufferWithInfo getByteBufferWithInfo() {
        try {
            this.os.flush();
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "getByteBufferWithInfo");
        }
        final ByteBuffer wrap = ByteBuffer.wrap(this.bos.getByteArray());
        wrap.limit(this.bos.size());
        return new ByteBufferWithInfo(this.orb, wrap, this.bos.size());
    }
    
    @Override
    public void setByteBufferWithInfo(final ByteBufferWithInfo byteBufferWithInfo) {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public final BufferManagerWrite getBufferManager() {
        return this.bufferManager;
    }
    
    @Override
    public final void write_fixed(final BigDecimal bigDecimal, final short n, final short n2) {
        String s = bigDecimal.toString();
        if (s.charAt(0) == '-' || s.charAt(0) == '+') {
            s = s.substring(1);
        }
        final int index = s.indexOf(46);
        String substring;
        String substring2;
        if (index == -1) {
            substring = s;
            substring2 = null;
        }
        else if (index == 0) {
            substring = null;
            substring2 = s;
        }
        else {
            substring = s.substring(0, index);
            substring2 = s.substring(index + 1);
        }
        final StringBuffer sb = new StringBuffer(n);
        if (substring2 != null) {
            sb.append(substring2);
        }
        while (sb.length() < n2) {
            sb.append('0');
        }
        if (substring != null) {
            sb.insert(0, substring);
        }
        while (sb.length() < n) {
            sb.insert(0, '0');
        }
        this.write_fixed(sb.toString(), bigDecimal.signum());
    }
    
    @Override
    public final void writeOctetSequenceTo(final org.omg.CORBA.portable.OutputStream outputStream) {
        final byte[] byteArray = this.toByteArray();
        outputStream.write_long(byteArray.length);
        outputStream.write_octet_array(byteArray, 0, byteArray.length);
    }
    
    @Override
    public final GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_2;
    }
    
    @Override
    public final void writeIndirection(final int n, final int n2) {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    void freeInternalCaches() {
    }
    
    @Override
    void printBuffer() {
        final byte[] byteArray = this.toByteArray();
        System.out.println("+++++++ Output Buffer ++++++++");
        System.out.println();
        System.out.println("Current position: " + byteArray.length);
        System.out.println();
        final char[] array = new char[16];
        try {
            for (int i = 0; i < byteArray.length; i += 16) {
                int j;
                for (j = 0; j < 16 && j + i < byteArray.length; ++j) {
                    int n = byteArray[i + j];
                    if (n < 0) {
                        n += 256;
                    }
                    String s = Integer.toHexString(n);
                    if (s.length() == 1) {
                        s = "0" + s;
                    }
                    System.out.print(s + " ");
                }
                while (j < 16) {
                    System.out.print("   ");
                    ++j;
                }
                int n2;
                for (n2 = 0; n2 < 16 && n2 + i < byteArray.length; ++n2) {
                    if (ORBUtility.isPrintable((char)byteArray[i + n2])) {
                        array[n2] = (char)byteArray[i + n2];
                    }
                    else {
                        array[n2] = '.';
                    }
                }
                System.out.println(new String(array, 0, n2));
            }
        }
        catch (final Throwable t) {
            t.printStackTrace();
        }
        System.out.println("++++++++++++++++++++++++++++++");
    }
    
    public void alignOnBoundary(final int n) {
        throw this.wrapper.giopVersionError();
    }
    
    public void setHeaderPadding(final boolean b) {
    }
    
    @Override
    public void start_value(final String s) {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public void end_value() {
        throw this.wrapper.giopVersionError();
    }
    
    class _ByteArrayOutputStream extends ByteArrayOutputStream
    {
        _ByteArrayOutputStream(final int n) {
            super(n);
        }
        
        byte[] getByteArray() {
            return this.buf;
        }
    }
    
    class MarshalObjectOutputStream extends ObjectOutputStream
    {
        ORB orb;
        
        MarshalObjectOutputStream(final OutputStream outputStream, final ORB orb) throws IOException {
            super(outputStream);
            this.orb = orb;
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    ObjectOutputStream.this.enableReplaceObject(true);
                    return null;
                }
            });
        }
        
        @Override
        protected final Object replaceObject(final Object o) throws IOException {
            try {
                if (o instanceof Remote && !StubAdapter.isStub(o)) {
                    return Utility.autoConnect(o, this.orb, true);
                }
            }
            catch (final Exception ex) {
                final IOException ex2 = new IOException("replaceObject failed");
                ex2.initCause(ex);
                throw ex2;
            }
            return o;
        }
    }
}
