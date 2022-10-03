package com.sun.corba.se.impl.encoding;

import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.omg.CORBA.Context;
import com.sun.corba.se.impl.orbutil.ORBUtility;
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
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.portable.IDLEntity;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.spi.ior.IORFactories;
import java.math.BigDecimal;
import com.sun.corba.se.impl.corba.PrincipalImpl;
import org.omg.CORBA.Principal;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.Any;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import org.omg.CORBA.TypeCode;
import java.io.InputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.util.LinkedList;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import com.sun.corba.se.spi.orb.ORB;

public class IDLJavaSerializationInputStream extends CDRInputStreamBase
{
    private ORB orb;
    private int bufSize;
    private ByteBuffer buffer;
    private byte encodingVersion;
    private ObjectInputStream is;
    private _ByteArrayInputStream bis;
    private BufferManagerRead bufferManager;
    private final int directReadLength = 16;
    private boolean markOn;
    private int peekIndex;
    private int peekCount;
    private LinkedList markedItemQ;
    protected ORBUtilSystemException wrapper;
    
    public IDLJavaSerializationInputStream(final byte encodingVersion) {
        this.markedItemQ = new LinkedList();
        this.encodingVersion = encodingVersion;
    }
    
    @Override
    public void init(final org.omg.CORBA.ORB orb, final ByteBuffer buffer, final int bufSize, final boolean b, final BufferManagerRead bufferManager) {
        this.orb = (ORB)orb;
        this.bufSize = bufSize;
        this.bufferManager = bufferManager;
        this.buffer = buffer;
        this.wrapper = ORBUtilSystemException.get((ORB)orb, "rpc.encoding");
        byte[] array;
        if (this.buffer.hasArray()) {
            array = this.buffer.array();
        }
        else {
            array = new byte[bufSize];
            this.buffer.get(array);
        }
        this.bis = new _ByteArrayInputStream(array);
    }
    
    private void initObjectInputStream() {
        if (this.is != null) {
            throw this.wrapper.javaStreamInitFailed();
        }
        try {
            this.is = new MarshalObjectInputStream(this.bis, this.orb);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaStreamInitFailed(ex);
        }
    }
    
    @Override
    public boolean read_boolean() {
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final boolean boolean1 = this.is.readBoolean();
            if (this.markOn) {
                this.markedItemQ.addLast(boolean1);
            }
            return boolean1;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_boolean");
        }
    }
    
    @Override
    public char read_char() {
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final char char1 = this.is.readChar();
            if (this.markOn) {
                this.markedItemQ.addLast(new Character(char1));
            }
            return char1;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_char");
        }
    }
    
    @Override
    public char read_wchar() {
        return this.read_char();
    }
    
    @Override
    public byte read_octet() {
        if (this.bis.getPosition() < 16) {
            final byte b = (byte)this.bis.read();
            if (this.bis.getPosition() == 16) {
                this.initObjectInputStream();
            }
            return b;
        }
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final byte byte1 = this.is.readByte();
            if (this.markOn) {
                this.markedItemQ.addLast(new Byte(byte1));
            }
            return byte1;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_octet");
        }
    }
    
    @Override
    public short read_short() {
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final short short1 = this.is.readShort();
            if (this.markOn) {
                this.markedItemQ.addLast(new Short(short1));
            }
            return short1;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_short");
        }
    }
    
    @Override
    public short read_ushort() {
        return this.read_short();
    }
    
    @Override
    public int read_long() {
        if (this.bis.getPosition() < 16) {
            final int n = this.bis.read() << 24 & 0xFF000000;
            final int n2 = this.bis.read() << 16 & 0xFF0000;
            final int n3 = this.bis.read() << 8 & 0xFF00;
            final int n4 = this.bis.read() << 0 & 0xFF;
            if (this.bis.getPosition() == 16) {
                this.initObjectInputStream();
            }
            else if (this.bis.getPosition() > 16) {
                this.wrapper.javaSerializationException("read_long");
            }
            return n | n2 | n3 | n4;
        }
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final int int1 = this.is.readInt();
            if (this.markOn) {
                this.markedItemQ.addLast(new Integer(int1));
            }
            return int1;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_long");
        }
    }
    
    @Override
    public int read_ulong() {
        return this.read_long();
    }
    
    @Override
    public long read_longlong() {
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final long long1 = this.is.readLong();
            if (this.markOn) {
                this.markedItemQ.addLast(new Long(long1));
            }
            return long1;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_longlong");
        }
    }
    
    @Override
    public long read_ulonglong() {
        return this.read_longlong();
    }
    
    @Override
    public float read_float() {
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final float float1 = this.is.readFloat();
            if (this.markOn) {
                this.markedItemQ.addLast(new Float(float1));
            }
            return float1;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_float");
        }
    }
    
    @Override
    public double read_double() {
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final double double1 = this.is.readDouble();
            if (this.markOn) {
                this.markedItemQ.addLast(new Double(double1));
            }
            return double1;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_double");
        }
    }
    
    @Override
    public String read_string() {
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final String utf = this.is.readUTF();
            if (this.markOn) {
                this.markedItemQ.addLast(utf);
            }
            return utf;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_string");
        }
    }
    
    @Override
    public String read_wstring() {
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final String s = (String)this.is.readObject();
            if (this.markOn) {
                this.markedItemQ.addLast(s);
            }
            return s;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_wstring");
        }
    }
    
    @Override
    public void read_boolean_array(final boolean[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_boolean();
        }
    }
    
    @Override
    public void read_char_array(final char[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_char();
        }
    }
    
    @Override
    public void read_wchar_array(final char[] array, final int n, final int n2) {
        this.read_char_array(array, n, n2);
    }
    
    @Override
    public void read_octet_array(final byte[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_octet();
        }
    }
    
    @Override
    public void read_short_array(final short[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_short();
        }
    }
    
    @Override
    public void read_ushort_array(final short[] array, final int n, final int n2) {
        this.read_short_array(array, n, n2);
    }
    
    @Override
    public void read_long_array(final int[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_long();
        }
    }
    
    @Override
    public void read_ulong_array(final int[] array, final int n, final int n2) {
        this.read_long_array(array, n, n2);
    }
    
    @Override
    public void read_longlong_array(final long[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_longlong();
        }
    }
    
    @Override
    public void read_ulonglong_array(final long[] array, final int n, final int n2) {
        this.read_longlong_array(array, n, n2);
    }
    
    @Override
    public void read_float_array(final float[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_float();
        }
    }
    
    @Override
    public void read_double_array(final double[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_double();
        }
    }
    
    @Override
    public org.omg.CORBA.Object read_Object() {
        return this.read_Object(null);
    }
    
    @Override
    public TypeCode read_TypeCode() {
        final TypeCodeImpl typeCodeImpl = new TypeCodeImpl(this.orb);
        typeCodeImpl.read_value(this.parent);
        return typeCodeImpl;
    }
    
    @Override
    public Any read_any() {
        final Any create_any = this.orb.create_any();
        final TypeCodeImpl typeCodeImpl = new TypeCodeImpl(this.orb);
        try {
            typeCodeImpl.read_value(this.parent);
        }
        catch (final MARSHAL marshal) {
            if (typeCodeImpl.kind().value() != 29) {
                throw marshal;
            }
            marshal.printStackTrace();
        }
        create_any.read_value(this.parent, typeCodeImpl);
        return create_any;
    }
    
    @Override
    public Principal read_Principal() {
        final int read_long = this.read_long();
        final byte[] array = new byte[read_long];
        this.read_octet_array(array, 0, read_long);
        final PrincipalImpl principalImpl = new PrincipalImpl();
        principalImpl.name(array);
        return principalImpl;
    }
    
    @Override
    public BigDecimal read_fixed() {
        return new BigDecimal(this.read_fixed_buffer().toString());
    }
    
    private StringBuffer read_fixed_buffer() {
        final StringBuffer sb = new StringBuffer(64);
        int n = 0;
        int i = 1;
        while (i != 0) {
            final byte read_octet = this.read_octet();
            final int n2 = (read_octet & 0xF0) >> 4;
            final int n3 = read_octet & 0xF;
            if (n != 0 || n2 != 0) {
                sb.append(Character.forDigit(n2, 10));
                n = 1;
            }
            if (n3 == 12) {
                if (n == 0) {
                    return new StringBuffer("0.0");
                }
                i = 0;
            }
            else if (n3 == 13) {
                sb.insert(0, '-');
                i = 0;
            }
            else {
                sb.append(Character.forDigit(n3, 10));
                n = 1;
            }
        }
        return sb;
    }
    
    @Override
    public org.omg.CORBA.Object read_Object(final Class clazz) {
        final IOR ior = IORFactories.makeIOR(this.parent);
        if (ior.isNil()) {
            return null;
        }
        final PresentationManager.StubFactoryFactory stubFactoryFactory = ORB.getStubFactoryFactory();
        final String codebase = ior.getProfile().getCodebase();
        PresentationManager.StubFactory stubFactory;
        if (clazz == null) {
            final RepositoryId id = RepositoryId.cache.getId(ior.getTypeId());
            final String className = id.getClassName();
            final boolean idlType = id.isIDLType();
            if (className == null || className.equals("")) {
                stubFactory = null;
            }
            else {
                try {
                    stubFactory = stubFactoryFactory.createStubFactory(className, idlType, codebase, null, null);
                }
                catch (final Exception ex) {
                    stubFactory = null;
                }
            }
        }
        else if (StubAdapter.isStubClass(clazz)) {
            stubFactory = PresentationDefaults.makeStaticStubFactory(clazz);
        }
        else {
            stubFactory = stubFactoryFactory.createStubFactory(clazz.getName(), IDLEntity.class.isAssignableFrom(clazz), codebase, clazz, clazz.getClassLoader());
        }
        return CDRInputStream_1_0.internalIORToObject(ior, stubFactory, this.orb);
    }
    
    @Override
    public org.omg.CORBA.ORB orb() {
        return this.orb;
    }
    
    @Override
    public Serializable read_value() {
        if (!this.markOn && !this.markedItemQ.isEmpty()) {
            return this.markedItemQ.removeFirst();
        }
        if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount) {
            return this.markedItemQ.get(this.peekIndex++);
        }
        try {
            final Serializable s = (Serializable)this.is.readObject();
            if (this.markOn) {
                this.markedItemQ.addLast(s);
            }
            return s;
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "read_value");
        }
    }
    
    @Override
    public Serializable read_value(final Class clazz) {
        return this.read_value();
    }
    
    @Override
    public Serializable read_value(final BoxedValueHelper boxedValueHelper) {
        return this.read_value();
    }
    
    @Override
    public Serializable read_value(final String s) {
        return this.read_value();
    }
    
    @Override
    public Serializable read_value(final Serializable s) {
        return this.read_value();
    }
    
    @Override
    public Object read_abstract_interface() {
        return this.read_abstract_interface(null);
    }
    
    @Override
    public Object read_abstract_interface(final Class clazz) {
        if (this.read_boolean()) {
            return this.read_Object(clazz);
        }
        return this.read_value();
    }
    
    @Override
    public void consumeEndian() {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public int getPosition() {
        try {
            return this.bis.getPosition();
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "getPosition");
        }
    }
    
    @Override
    public Object read_Abstract() {
        return this.read_abstract_interface();
    }
    
    @Override
    public Serializable read_Value() {
        return this.read_value();
    }
    
    @Override
    public void read_any_array(final AnySeqHolder anySeqHolder, final int n, final int n2) {
        this.read_any_array(anySeqHolder.value, n, n2);
    }
    
    private final void read_any_array(final Any[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_any();
        }
    }
    
    @Override
    public void read_boolean_array(final BooleanSeqHolder booleanSeqHolder, final int n, final int n2) {
        this.read_boolean_array(booleanSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_char_array(final CharSeqHolder charSeqHolder, final int n, final int n2) {
        this.read_char_array(charSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_wchar_array(final WCharSeqHolder wCharSeqHolder, final int n, final int n2) {
        this.read_wchar_array(wCharSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_octet_array(final OctetSeqHolder octetSeqHolder, final int n, final int n2) {
        this.read_octet_array(octetSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_short_array(final ShortSeqHolder shortSeqHolder, final int n, final int n2) {
        this.read_short_array(shortSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_ushort_array(final UShortSeqHolder uShortSeqHolder, final int n, final int n2) {
        this.read_ushort_array(uShortSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_long_array(final LongSeqHolder longSeqHolder, final int n, final int n2) {
        this.read_long_array(longSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_ulong_array(final ULongSeqHolder uLongSeqHolder, final int n, final int n2) {
        this.read_ulong_array(uLongSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_ulonglong_array(final ULongLongSeqHolder uLongLongSeqHolder, final int n, final int n2) {
        this.read_ulonglong_array(uLongLongSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_longlong_array(final LongLongSeqHolder longLongSeqHolder, final int n, final int n2) {
        this.read_longlong_array(longLongSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_float_array(final FloatSeqHolder floatSeqHolder, final int n, final int n2) {
        this.read_float_array(floatSeqHolder.value, n, n2);
    }
    
    @Override
    public void read_double_array(final DoubleSeqHolder doubleSeqHolder, final int n, final int n2) {
        this.read_double_array(doubleSeqHolder.value, n, n2);
    }
    
    @Override
    public String[] _truncatable_ids() {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public void mark(final int n) {
        if (this.markOn || this.is == null) {
            throw this.wrapper.javaSerializationException("mark");
        }
        this.markOn = true;
        if (!this.markedItemQ.isEmpty()) {
            this.peekIndex = 0;
            this.peekCount = this.markedItemQ.size();
        }
    }
    
    @Override
    public void reset() {
        this.markOn = false;
        this.peekIndex = 0;
        this.peekCount = 0;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public CDRInputStreamBase dup() {
        CDRInputStreamBase cdrInputStreamBase;
        try {
            cdrInputStreamBase = (CDRInputStreamBase)this.getClass().newInstance();
        }
        catch (final Exception ex) {
            throw this.wrapper.couldNotDuplicateCdrInputStream(ex);
        }
        cdrInputStreamBase.init(this.orb, this.buffer, this.bufSize, false, null);
        ((IDLJavaSerializationInputStream)cdrInputStreamBase).skipBytes(this.getPosition());
        ((IDLJavaSerializationInputStream)cdrInputStreamBase).setMarkData(this.markOn, this.peekIndex, this.peekCount, (LinkedList)this.markedItemQ.clone());
        return cdrInputStreamBase;
    }
    
    void skipBytes(final int n) {
        try {
            this.is.skipBytes(n);
        }
        catch (final Exception ex) {
            throw this.wrapper.javaSerializationException(ex, "skipBytes");
        }
    }
    
    void setMarkData(final boolean markOn, final int peekIndex, final int peekCount, final LinkedList markedItemQ) {
        this.markOn = markOn;
        this.peekIndex = peekIndex;
        this.peekCount = peekCount;
        this.markedItemQ = markedItemQ;
    }
    
    @Override
    public BigDecimal read_fixed(final short n, final short n2) {
        final StringBuffer read_fixed_buffer = this.read_fixed_buffer();
        if (n != read_fixed_buffer.length()) {
            throw this.wrapper.badFixed(new Integer(n), new Integer(read_fixed_buffer.length()));
        }
        read_fixed_buffer.insert(n - n2, '.');
        return new BigDecimal(read_fixed_buffer.toString());
    }
    
    @Override
    public boolean isLittleEndian() {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    void setHeaderPadding(final boolean b) {
    }
    
    @Override
    public ByteBuffer getByteBuffer() {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public void setByteBuffer(final ByteBuffer byteBuffer) {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public void setByteBufferWithInfo(final ByteBufferWithInfo byteBufferWithInfo) {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public int getBufferLength() {
        return this.bufSize;
    }
    
    @Override
    public void setBufferLength(final int n) {
    }
    
    @Override
    public int getIndex() {
        return this.bis.getPosition();
    }
    
    @Override
    public void setIndex(final int position) {
        try {
            this.bis.setPosition(position);
        }
        catch (final IndexOutOfBoundsException ex) {
            throw this.wrapper.javaSerializationException(ex, "setIndex");
        }
    }
    
    @Override
    public void orb(final org.omg.CORBA.ORB orb) {
        this.orb = (ORB)orb;
    }
    
    @Override
    public BufferManagerRead getBufferManager() {
        return this.bufferManager;
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_2;
    }
    
    @Override
    CodeBase getCodeBase() {
        return this.parent.getCodeBase();
    }
    
    @Override
    void printBuffer() {
        final byte[] array = this.buffer.array();
        System.out.println("+++++++ Input Buffer ++++++++");
        System.out.println();
        System.out.println("Current position: " + this.getPosition());
        System.out.println("Total length : " + this.bufSize);
        System.out.println();
        final char[] array2 = new char[16];
        try {
            for (int i = 0; i < array.length; i += 16) {
                int j;
                for (j = 0; j < 16 && j + i < array.length; ++j) {
                    int n = array[i + j];
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
                for (n2 = 0; n2 < 16 && n2 + i < array.length; ++n2) {
                    if (ORBUtility.isPrintable((char)array[i + n2])) {
                        array2[n2] = (char)array[i + n2];
                    }
                    else {
                        array2[n2] = '.';
                    }
                }
                System.out.println(new String(array2, 0, n2));
            }
        }
        catch (final Throwable t) {
            t.printStackTrace();
        }
        System.out.println("++++++++++++++++++++++++++++++");
    }
    
    @Override
    void alignOnBoundary(final int n) {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    void performORBVersionSpecificInit() {
    }
    
    @Override
    public void resetCodeSetConverters() {
    }
    
    @Override
    public void start_value() {
        throw this.wrapper.giopVersionError();
    }
    
    @Override
    public void end_value() {
        throw this.wrapper.giopVersionError();
    }
    
    class _ByteArrayInputStream extends ByteArrayInputStream
    {
        _ByteArrayInputStream(final byte[] array) {
            super(array);
        }
        
        int getPosition() {
            return this.pos;
        }
        
        void setPosition(final int pos) {
            if (pos < 0 || pos > this.count) {
                throw new IndexOutOfBoundsException();
            }
            this.pos = pos;
        }
    }
    
    class MarshalObjectInputStream extends ObjectInputStream
    {
        ORB orb;
        
        MarshalObjectInputStream(final InputStream inputStream, final ORB orb) throws IOException {
            super(inputStream);
            this.orb = orb;
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    ObjectInputStream.this.enableResolveObject(true);
                    return null;
                }
            });
        }
        
        @Override
        protected final Object resolveObject(final Object o) throws IOException {
            try {
                if (StubAdapter.isStub(o)) {
                    StubAdapter.connect(o, this.orb);
                }
            }
            catch (final RemoteException ex) {
                final IOException ex2 = new IOException("resolveObject failed");
                ex2.initCause(ex);
                throw ex2;
            }
            return o;
        }
    }
}
