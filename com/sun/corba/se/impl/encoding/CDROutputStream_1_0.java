package com.sun.corba.se.impl.encoding;

import org.omg.CORBA.ContextList;
import org.omg.CORBA.Context;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.protocol.MessageMediator;
import java.math.BigDecimal;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Method;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.impl.util.Utility;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.StreamableValue;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.omg.CORBA.DataOutputStream;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.TypeCodePackage.BadKind;
import com.sun.org.omg.CORBA.portable.ValueHelper;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.ValueBase;
import javax.rmi.CORBA.ValueHandlerMultiFormat;
import javax.rmi.CORBA.Util;
import java.io.Serializable;
import org.omg.CORBA.LocalObject;
import com.sun.corba.se.spi.ior.IORFactories;
import org.omg.CORBA.Object;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.Principal;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.impl.orbutil.RepositoryIdUtility;
import javax.rmi.CORBA.ValueHandler;
import com.sun.corba.se.impl.orbutil.CacheTable;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public class CDROutputStream_1_0 extends CDROutputStreamBase
{
    private static final int INDIRECTION_TAG = -1;
    protected boolean littleEndian;
    protected BufferManagerWrite bufferManagerWrite;
    ByteBufferWithInfo bbwi;
    protected ORB orb;
    protected ORBUtilSystemException wrapper;
    protected boolean debug;
    protected int blockSizeIndex;
    protected int blockSizePosition;
    protected byte streamFormatVersion;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final String kWriteMethod = "write";
    private CacheTable codebaseCache;
    private CacheTable valueCache;
    private CacheTable repositoryIdCache;
    private int end_flag;
    private int chunkedValueNestingLevel;
    private boolean mustChunk;
    protected boolean inBlock;
    private int end_flag_position;
    private int end_flag_index;
    private ValueHandler valueHandler;
    private RepositoryIdUtility repIdUtil;
    private RepositoryIdStrings repIdStrs;
    private CodeSetConversion.CTBConverter charConverter;
    private CodeSetConversion.CTBConverter wcharConverter;
    private static final String _id = "IDL:omg.org/CORBA/DataOutputStream:1.0";
    private static final String[] _ids;
    
    public CDROutputStream_1_0() {
        this.debug = false;
        this.blockSizeIndex = -1;
        this.blockSizePosition = 0;
        this.codebaseCache = null;
        this.valueCache = null;
        this.repositoryIdCache = null;
        this.end_flag = 0;
        this.chunkedValueNestingLevel = 0;
        this.mustChunk = false;
        this.inBlock = false;
        this.end_flag_position = 0;
        this.end_flag_index = 0;
        this.valueHandler = null;
    }
    
    public void init(final org.omg.CORBA.ORB orb, final boolean littleEndian, final BufferManagerWrite bufferManagerWrite, final byte streamFormatVersion, final boolean b) {
        this.orb = (ORB)orb;
        this.wrapper = ORBUtilSystemException.get(this.orb, "rpc.encoding");
        this.debug = this.orb.transportDebugFlag;
        this.littleEndian = littleEndian;
        this.bufferManagerWrite = bufferManagerWrite;
        this.bbwi = new ByteBufferWithInfo(orb, bufferManagerWrite, b);
        this.streamFormatVersion = streamFormatVersion;
        this.createRepositoryIdHandlers();
    }
    
    public void init(final org.omg.CORBA.ORB orb, final boolean b, final BufferManagerWrite bufferManagerWrite, final byte b2) {
        this.init(orb, b, bufferManagerWrite, b2, true);
    }
    
    private final void createRepositoryIdHandlers() {
        this.repIdUtil = RepositoryIdFactory.getRepIdUtility();
        this.repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
    }
    
    @Override
    public BufferManagerWrite getBufferManager() {
        return this.bufferManagerWrite;
    }
    
    @Override
    public byte[] toByteArray() {
        final byte[] array = new byte[this.bbwi.position()];
        for (int i = 0; i < this.bbwi.position(); ++i) {
            array[i] = this.bbwi.byteBuffer.get(i);
        }
        return array;
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_0;
    }
    
    @Override
    void setHeaderPadding(final boolean b) {
        throw this.wrapper.giopVersionError();
    }
    
    protected void handleSpecialChunkBegin(final int n) {
    }
    
    protected void handleSpecialChunkEnd() {
    }
    
    protected final int computeAlignment(final int n) {
        if (n > 1) {
            final int n2 = this.bbwi.position() & n - 1;
            if (n2 != 0) {
                return n - n2;
            }
        }
        return 0;
    }
    
    protected void alignAndReserve(final int n, final int n2) {
        this.bbwi.position(this.bbwi.position() + this.computeAlignment(n));
        if (this.bbwi.position() + n2 > this.bbwi.buflen) {
            this.grow(n, n2);
        }
    }
    
    protected void grow(final int n, final int needed) {
        this.bbwi.needed = needed;
        this.bufferManagerWrite.overflow(this.bbwi);
    }
    
    @Override
    public final void putEndian() throws SystemException {
        this.write_boolean(this.littleEndian);
    }
    
    public final boolean littleEndian() {
        return this.littleEndian;
    }
    
    @Override
    void freeInternalCaches() {
        if (this.codebaseCache != null) {
            this.codebaseCache.done();
        }
        if (this.valueCache != null) {
            this.valueCache.done();
        }
        if (this.repositoryIdCache != null) {
            this.repositoryIdCache.done();
        }
    }
    
    public final void write_longdouble(final double n) {
        throw this.wrapper.longDoubleNotImplemented(CompletionStatus.COMPLETED_MAYBE);
    }
    
    @Override
    public void write_octet(final byte b) {
        this.alignAndReserve(1, 1);
        this.bbwi.byteBuffer.put(this.bbwi.position(), b);
        this.bbwi.position(this.bbwi.position() + 1);
    }
    
    @Override
    public final void write_boolean(final boolean b) {
        this.write_octet((byte)(b ? 1 : 0));
    }
    
    @Override
    public void write_char(final char c) {
        final CodeSetConversion.CTBConverter charConverter = this.getCharConverter();
        charConverter.convert(c);
        if (charConverter.getNumBytes() > 1) {
            throw this.wrapper.invalidSingleCharCtb(CompletionStatus.COMPLETED_MAYBE);
        }
        this.write_octet(charConverter.getBytes()[0]);
    }
    
    private final void writeLittleEndianWchar(final char c) {
        this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(c & '\u00ff'));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(c >>> 8 & 0xFF));
        this.bbwi.position(this.bbwi.position() + 2);
    }
    
    private final void writeBigEndianWchar(final char c) {
        this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(c >>> 8 & 0xFF));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(c & '\u00ff'));
        this.bbwi.position(this.bbwi.position() + 2);
    }
    
    private final void writeLittleEndianShort(final short n) {
        this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(n & 0xFF));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(n >>> 8 & 0xFF));
        this.bbwi.position(this.bbwi.position() + 2);
    }
    
    private final void writeBigEndianShort(final short n) {
        this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(n >>> 8 & 0xFF));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(n & 0xFF));
        this.bbwi.position(this.bbwi.position() + 2);
    }
    
    private final void writeLittleEndianLong(final int n) {
        this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(n & 0xFF));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(n >>> 8 & 0xFF));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)(n >>> 16 & 0xFF));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)(n >>> 24 & 0xFF));
        this.bbwi.position(this.bbwi.position() + 4);
    }
    
    private final void writeBigEndianLong(final int n) {
        this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(n >>> 24 & 0xFF));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(n >>> 16 & 0xFF));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)(n >>> 8 & 0xFF));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)(n & 0xFF));
        this.bbwi.position(this.bbwi.position() + 4);
    }
    
    private final void writeLittleEndianLongLong(final long n) {
        this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(n & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(n >>> 8 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)(n >>> 16 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)(n >>> 24 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 4, (byte)(n >>> 32 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 5, (byte)(n >>> 40 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 6, (byte)(n >>> 48 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 7, (byte)(n >>> 56 & 0xFFL));
        this.bbwi.position(this.bbwi.position() + 8);
    }
    
    private final void writeBigEndianLongLong(final long n) {
        this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(n >>> 56 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(n >>> 48 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)(n >>> 40 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)(n >>> 32 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 4, (byte)(n >>> 24 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 5, (byte)(n >>> 16 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 6, (byte)(n >>> 8 & 0xFFL));
        this.bbwi.byteBuffer.put(this.bbwi.position() + 7, (byte)(n & 0xFFL));
        this.bbwi.position(this.bbwi.position() + 8);
    }
    
    @Override
    public void write_wchar(final char c) {
        if (ORBUtility.isForeignORB(this.orb)) {
            throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
        }
        this.alignAndReserve(2, 2);
        if (this.littleEndian) {
            this.writeLittleEndianWchar(c);
        }
        else {
            this.writeBigEndianWchar(c);
        }
    }
    
    @Override
    public void write_short(final short n) {
        this.alignAndReserve(2, 2);
        if (this.littleEndian) {
            this.writeLittleEndianShort(n);
        }
        else {
            this.writeBigEndianShort(n);
        }
    }
    
    @Override
    public final void write_ushort(final short n) {
        this.write_short(n);
    }
    
    @Override
    public void write_long(final int n) {
        this.alignAndReserve(4, 4);
        if (this.littleEndian) {
            this.writeLittleEndianLong(n);
        }
        else {
            this.writeBigEndianLong(n);
        }
    }
    
    @Override
    public final void write_ulong(final int n) {
        this.write_long(n);
    }
    
    @Override
    public void write_longlong(final long n) {
        this.alignAndReserve(8, 8);
        if (this.littleEndian) {
            this.writeLittleEndianLongLong(n);
        }
        else {
            this.writeBigEndianLongLong(n);
        }
    }
    
    @Override
    public final void write_ulonglong(final long n) {
        this.write_longlong(n);
    }
    
    @Override
    public final void write_float(final float n) {
        this.write_long(Float.floatToIntBits(n));
    }
    
    @Override
    public final void write_double(final double n) {
        this.write_longlong(Double.doubleToLongBits(n));
    }
    
    @Override
    public void write_string(final String s) {
        this.writeString(s);
    }
    
    protected int writeString(final String s) {
        if (s == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        final CodeSetConversion.CTBConverter charConverter = this.getCharConverter();
        charConverter.convert(s);
        final int n = charConverter.getNumBytes() + 1;
        this.handleSpecialChunkBegin(this.computeAlignment(4) + 4 + n);
        this.write_long(n);
        final int n2 = this.get_offset() - 4;
        this.internalWriteOctetArray(charConverter.getBytes(), 0, charConverter.getNumBytes());
        this.write_octet((byte)0);
        this.handleSpecialChunkEnd();
        return n2;
    }
    
    @Override
    public void write_wstring(final String s) {
        if (s == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        if (ORBUtility.isForeignORB(this.orb)) {
            throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
        }
        final int n = s.length() + 1;
        this.handleSpecialChunkBegin(4 + n * 2 + this.computeAlignment(4));
        this.write_long(n);
        for (int i = 0; i < n - 1; ++i) {
            this.write_wchar(s.charAt(i));
        }
        this.write_short((short)0);
        this.handleSpecialChunkEnd();
    }
    
    void internalWriteOctetArray(final byte[] array, final int n, final int n2) {
        int i = n;
        int n3 = 1;
        while (i < n2 + n) {
            if (this.bbwi.position() + 1 > this.bbwi.buflen || n3 != 0) {
                n3 = 0;
                this.alignAndReserve(1, 1);
            }
            final int n4 = this.bbwi.buflen - this.bbwi.position();
            final int n5 = n2 + n - i;
            final int n6 = (n5 < n4) ? n5 : n4;
            for (int j = 0; j < n6; ++j) {
                this.bbwi.byteBuffer.put(this.bbwi.position() + j, array[i + j]);
            }
            this.bbwi.position(this.bbwi.position() + n6);
            i += n6;
        }
    }
    
    @Override
    public final void write_octet_array(final byte[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.handleSpecialChunkBegin(n2);
        this.internalWriteOctetArray(array, n, n2);
        this.handleSpecialChunkEnd();
    }
    
    @Override
    public void write_Principal(final Principal principal) {
        this.write_long(principal.name().length);
        this.write_octet_array(principal.name(), 0, principal.name().length);
    }
    
    @Override
    public void write_any(final Any any) {
        if (any == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.write_TypeCode(any.type());
        any.write_value(this.parent);
    }
    
    @Override
    public void write_TypeCode(final TypeCode typeCode) {
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
    public void write_Object(final org.omg.CORBA.Object object) {
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
    public void write_abstract_interface(final Object o) {
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
    public void write_value(final Serializable s, final Class clazz) {
        this.write_value(s);
    }
    
    private void writeWStringValue(final String s) {
        final int writeValueTag = this.writeValueTag(this.mustChunk, true, null);
        this.write_repositoryId(this.repIdStrs.getWStringValueRepId());
        this.updateIndirectionTable(writeValueTag, s, s);
        if (this.mustChunk) {
            this.start_block();
            --this.end_flag;
            --this.chunkedValueNestingLevel;
        }
        else {
            --this.end_flag;
        }
        this.write_wstring(s);
        if (this.mustChunk) {
            this.end_block();
        }
        this.writeEndTag(this.mustChunk);
    }
    
    private void writeArray(final Serializable s, final Class clazz) {
        if (this.valueHandler == null) {
            this.valueHandler = ORBUtility.createValueHandler();
        }
        final int writeValueTag = this.writeValueTag(this.mustChunk, true, Util.getCodebase(clazz));
        this.write_repositoryId(this.repIdStrs.createSequenceRepID(clazz));
        this.updateIndirectionTable(writeValueTag, s, s);
        if (this.mustChunk) {
            this.start_block();
            --this.end_flag;
            --this.chunkedValueNestingLevel;
        }
        else {
            --this.end_flag;
        }
        if (this.valueHandler instanceof ValueHandlerMultiFormat) {
            ((ValueHandlerMultiFormat)this.valueHandler).writeValue(this.parent, s, this.streamFormatVersion);
        }
        else {
            this.valueHandler.writeValue(this.parent, s);
        }
        if (this.mustChunk) {
            this.end_block();
        }
        this.writeEndTag(this.mustChunk);
    }
    
    private void writeValueBase(final ValueBase valueBase, final Class clazz) {
        this.mustChunk = true;
        final int writeValueTag = this.writeValueTag(true, true, Util.getCodebase(clazz));
        final String s = valueBase._truncatable_ids()[0];
        this.write_repositoryId(s);
        this.updateIndirectionTable(writeValueTag, valueBase, valueBase);
        this.start_block();
        --this.end_flag;
        --this.chunkedValueNestingLevel;
        this.writeIDLValue(valueBase, s);
        this.end_block();
        this.writeEndTag(true);
    }
    
    private void writeRMIIIOPValueType(Serializable writeReplace, Class class1) {
        if (this.valueHandler == null) {
            this.valueHandler = ORBUtility.createValueHandler();
        }
        final Serializable s = writeReplace;
        writeReplace = this.valueHandler.writeReplace(s);
        if (writeReplace == null) {
            this.write_long(0);
            return;
        }
        if (writeReplace != s) {
            if (this.valueCache != null && this.valueCache.containsKey(writeReplace)) {
                this.writeIndirection(-1, this.valueCache.getVal(writeReplace));
                return;
            }
            class1 = writeReplace.getClass();
        }
        if (this.mustChunk || this.valueHandler.isCustomMarshaled(class1)) {
            this.mustChunk = true;
        }
        final int writeValueTag = this.writeValueTag(this.mustChunk, true, Util.getCodebase(class1));
        this.write_repositoryId(this.repIdStrs.createForJavaType(class1));
        this.updateIndirectionTable(writeValueTag, writeReplace, s);
        if (this.mustChunk) {
            --this.end_flag;
            --this.chunkedValueNestingLevel;
            this.start_block();
        }
        else {
            --this.end_flag;
        }
        if (this.valueHandler instanceof ValueHandlerMultiFormat) {
            ((ValueHandlerMultiFormat)this.valueHandler).writeValue(this.parent, writeReplace, this.streamFormatVersion);
        }
        else {
            this.valueHandler.writeValue(this.parent, writeReplace);
        }
        if (this.mustChunk) {
            this.end_block();
        }
        this.writeEndTag(this.mustChunk);
    }
    
    @Override
    public void write_value(final Serializable s, final String s2) {
        if (s == null) {
            this.write_long(0);
            return;
        }
        if (this.valueCache != null && this.valueCache.containsKey(s)) {
            this.writeIndirection(-1, this.valueCache.getVal(s));
            return;
        }
        final Class<? extends Serializable> class1 = s.getClass();
        final boolean mustChunk = this.mustChunk;
        if (this.mustChunk) {
            this.mustChunk = true;
        }
        if (this.inBlock) {
            this.end_block();
        }
        if (class1.isArray()) {
            this.writeArray(s, class1);
        }
        else if (s instanceof ValueBase) {
            this.writeValueBase((ValueBase)s, class1);
        }
        else if (this.shouldWriteAsIDLEntity(s)) {
            this.writeIDLEntity((IDLEntity)s);
        }
        else if (s instanceof String) {
            this.writeWStringValue((String)s);
        }
        else if (s instanceof Class) {
            this.writeClass(s2, (Class)s);
        }
        else {
            this.writeRMIIIOPValueType(s, class1);
        }
        this.mustChunk = mustChunk;
        if (this.mustChunk) {
            this.start_block();
        }
    }
    
    @Override
    public void write_value(final Serializable s) {
        this.write_value(s, (String)null);
    }
    
    @Override
    public void write_value(final Serializable s, final BoxedValueHelper boxedValueHelper) {
        if (s == null) {
            this.write_long(0);
            return;
        }
        if (this.valueCache != null && this.valueCache.containsKey(s)) {
            this.writeIndirection(-1, this.valueCache.getVal(s));
            return;
        }
        final boolean mustChunk = this.mustChunk;
        boolean b = false;
        if (boxedValueHelper instanceof ValueHelper) {
            short type_modifier;
            try {
                type_modifier = ((ValueHelper)boxedValueHelper).get_type().type_modifier();
            }
            catch (final BadKind badKind) {
                type_modifier = 0;
            }
            if (s instanceof CustomMarshal && type_modifier == 1) {
                b = true;
                this.mustChunk = true;
            }
            if (type_modifier == 3) {
                this.mustChunk = true;
            }
        }
        if (this.mustChunk) {
            if (this.inBlock) {
                this.end_block();
            }
            final int writeValueTag = this.writeValueTag(true, this.orb.getORBData().useRepId(), Util.getCodebase(s.getClass()));
            if (this.orb.getORBData().useRepId()) {
                this.write_repositoryId(boxedValueHelper.get_id());
            }
            this.updateIndirectionTable(writeValueTag, s, s);
            this.start_block();
            --this.end_flag;
            --this.chunkedValueNestingLevel;
            if (b) {
                ((CustomMarshal)s).marshal(this.parent);
            }
            else {
                boxedValueHelper.write_value(this.parent, s);
            }
            this.end_block();
            this.writeEndTag(true);
        }
        else {
            final int writeValueTag2 = this.writeValueTag(false, this.orb.getORBData().useRepId(), Util.getCodebase(s.getClass()));
            if (this.orb.getORBData().useRepId()) {
                this.write_repositoryId(boxedValueHelper.get_id());
            }
            this.updateIndirectionTable(writeValueTag2, s, s);
            --this.end_flag;
            boxedValueHelper.write_value(this.parent, s);
            this.writeEndTag(false);
        }
        this.mustChunk = mustChunk;
        if (this.mustChunk) {
            this.start_block();
        }
    }
    
    public int get_offset() {
        return this.bbwi.position();
    }
    
    @Override
    public void start_block() {
        if (this.debug) {
            this.dprint("CDROutputStream_1_0 start_block, position" + this.bbwi.position());
        }
        this.write_long(0);
        this.inBlock = true;
        this.blockSizePosition = this.get_offset();
        this.blockSizeIndex = this.bbwi.position();
        if (this.debug) {
            this.dprint("CDROutputStream_1_0 start_block, blockSizeIndex " + this.blockSizeIndex);
        }
    }
    
    protected void writeLongWithoutAlign(final int n) {
        if (this.littleEndian) {
            this.writeLittleEndianLong(n);
        }
        else {
            this.writeBigEndianLong(n);
        }
    }
    
    @Override
    public void end_block() {
        if (this.debug) {
            this.dprint("CDROutputStream_1_0.java end_block");
        }
        if (!this.inBlock) {
            return;
        }
        if (this.debug) {
            this.dprint("CDROutputStream_1_0.java end_block, in a block");
        }
        this.inBlock = false;
        if (this.get_offset() == this.blockSizePosition) {
            this.bbwi.position(this.bbwi.position() - 4);
            this.blockSizeIndex = -1;
            this.blockSizePosition = -1;
            return;
        }
        final int position = this.bbwi.position();
        this.bbwi.position(this.blockSizeIndex - 4);
        this.writeLongWithoutAlign(position - this.blockSizeIndex);
        this.bbwi.position(position);
        this.blockSizeIndex = -1;
        this.blockSizePosition = -1;
    }
    
    @Override
    public org.omg.CORBA.ORB orb() {
        return this.orb;
    }
    
    @Override
    public final void write_boolean_array(final boolean[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.handleSpecialChunkBegin(n2);
        for (int i = 0; i < n2; ++i) {
            this.write_boolean(array[n + i]);
        }
        this.handleSpecialChunkEnd();
    }
    
    @Override
    public final void write_char_array(final char[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.handleSpecialChunkBegin(n2);
        for (int i = 0; i < n2; ++i) {
            this.write_char(array[n + i]);
        }
        this.handleSpecialChunkEnd();
    }
    
    @Override
    public void write_wchar_array(final char[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.handleSpecialChunkBegin(this.computeAlignment(2) + n2 * 2);
        for (int i = 0; i < n2; ++i) {
            this.write_wchar(array[n + i]);
        }
        this.handleSpecialChunkEnd();
    }
    
    @Override
    public final void write_short_array(final short[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.handleSpecialChunkBegin(this.computeAlignment(2) + n2 * 2);
        for (int i = 0; i < n2; ++i) {
            this.write_short(array[n + i]);
        }
        this.handleSpecialChunkEnd();
    }
    
    @Override
    public final void write_ushort_array(final short[] array, final int n, final int n2) {
        this.write_short_array(array, n, n2);
    }
    
    @Override
    public final void write_long_array(final int[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.handleSpecialChunkBegin(this.computeAlignment(4) + n2 * 4);
        for (int i = 0; i < n2; ++i) {
            this.write_long(array[n + i]);
        }
        this.handleSpecialChunkEnd();
    }
    
    @Override
    public final void write_ulong_array(final int[] array, final int n, final int n2) {
        this.write_long_array(array, n, n2);
    }
    
    @Override
    public final void write_longlong_array(final long[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.handleSpecialChunkBegin(this.computeAlignment(8) + n2 * 8);
        for (int i = 0; i < n2; ++i) {
            this.write_longlong(array[n + i]);
        }
        this.handleSpecialChunkEnd();
    }
    
    @Override
    public final void write_ulonglong_array(final long[] array, final int n, final int n2) {
        this.write_longlong_array(array, n, n2);
    }
    
    @Override
    public final void write_float_array(final float[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.handleSpecialChunkBegin(this.computeAlignment(4) + n2 * 4);
        for (int i = 0; i < n2; ++i) {
            this.write_float(array[n + i]);
        }
        this.handleSpecialChunkEnd();
    }
    
    @Override
    public final void write_double_array(final double[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        this.handleSpecialChunkBegin(this.computeAlignment(8) + n2 * 8);
        for (int i = 0; i < n2; ++i) {
            this.write_double(array[n + i]);
        }
        this.handleSpecialChunkEnd();
    }
    
    public void write_string_array(final String[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        for (int i = 0; i < n2; ++i) {
            this.write_string(array[n + i]);
        }
    }
    
    public void write_wstring_array(final String[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
        }
        for (int i = 0; i < n2; ++i) {
            this.write_wstring(array[n + i]);
        }
    }
    
    @Override
    public final void write_any_array(final Any[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.write_any(array[n + i]);
        }
    }
    
    @Override
    public void writeTo(final OutputStream outputStream) throws IOException {
        byte[] array;
        if (this.bbwi.byteBuffer.hasArray()) {
            array = this.bbwi.byteBuffer.array();
        }
        else {
            final int position = this.bbwi.position();
            array = new byte[position];
            for (int i = 0; i < position; ++i) {
                array[i] = this.bbwi.byteBuffer.get(i);
            }
        }
        outputStream.write(array, 0, this.bbwi.position());
    }
    
    @Override
    public void writeOctetSequenceTo(final org.omg.CORBA.portable.OutputStream outputStream) {
        byte[] array;
        if (this.bbwi.byteBuffer.hasArray()) {
            array = this.bbwi.byteBuffer.array();
        }
        else {
            final int position = this.bbwi.position();
            array = new byte[position];
            for (int i = 0; i < position; ++i) {
                array[i] = this.bbwi.byteBuffer.get(i);
            }
        }
        outputStream.write_long(this.bbwi.position());
        outputStream.write_octet_array(array, 0, this.bbwi.position());
    }
    
    @Override
    public final int getSize() {
        return this.bbwi.position();
    }
    
    @Override
    public int getIndex() {
        return this.bbwi.position();
    }
    
    @Override
    public boolean isLittleEndian() {
        return this.littleEndian;
    }
    
    @Override
    public void setIndex(final int n) {
        this.bbwi.position(n);
    }
    
    @Override
    public ByteBufferWithInfo getByteBufferWithInfo() {
        return this.bbwi;
    }
    
    @Override
    public void setByteBufferWithInfo(final ByteBufferWithInfo bbwi) {
        this.bbwi = bbwi;
    }
    
    @Override
    public ByteBuffer getByteBuffer() {
        ByteBuffer byteBuffer = null;
        if (this.bbwi != null) {
            byteBuffer = this.bbwi.byteBuffer;
        }
        return byteBuffer;
    }
    
    @Override
    public void setByteBuffer(final ByteBuffer byteBuffer) {
        this.bbwi.byteBuffer = byteBuffer;
    }
    
    private final void updateIndirectionTable(final int n, final Object o, final Object o2) {
        if (this.valueCache == null) {
            this.valueCache = new CacheTable(this.orb, true);
        }
        this.valueCache.put(o, n);
        if (o2 != o) {
            this.valueCache.put(o2, n);
        }
    }
    
    private final void write_repositoryId(final String s) {
        if (this.repositoryIdCache != null && this.repositoryIdCache.containsKey(s)) {
            this.writeIndirection(-1, this.repositoryIdCache.getVal(s));
            return;
        }
        final int writeString = this.writeString(s);
        if (this.repositoryIdCache == null) {
            this.repositoryIdCache = new CacheTable(this.orb, true);
        }
        this.repositoryIdCache.put(s, writeString);
    }
    
    private void write_codebase(final String s, final int n) {
        if (this.codebaseCache != null && this.codebaseCache.containsKey(s)) {
            this.writeIndirection(-1, this.codebaseCache.getVal(s));
        }
        else {
            this.write_string(s);
            if (this.codebaseCache == null) {
                this.codebaseCache = new CacheTable(this.orb, true);
            }
            this.codebaseCache.put(s, n);
        }
    }
    
    private final int writeValueTag(final boolean b, final boolean b2, final String s) {
        int n = 0;
        if (b && !b2) {
            if (s == null) {
                this.write_long(this.repIdUtil.getStandardRMIChunkedNoRepStrId());
                n = this.get_offset() - 4;
            }
            else {
                this.write_long(this.repIdUtil.getCodeBaseRMIChunkedNoRepStrId());
                n = this.get_offset() - 4;
                this.write_codebase(s, this.get_offset());
            }
        }
        else if (b && b2) {
            if (s == null) {
                this.write_long(this.repIdUtil.getStandardRMIChunkedId());
                n = this.get_offset() - 4;
            }
            else {
                this.write_long(this.repIdUtil.getCodeBaseRMIChunkedId());
                n = this.get_offset() - 4;
                this.write_codebase(s, this.get_offset());
            }
        }
        else if (!b && !b2) {
            if (s == null) {
                this.write_long(this.repIdUtil.getStandardRMIUnchunkedNoRepStrId());
                n = this.get_offset() - 4;
            }
            else {
                this.write_long(this.repIdUtil.getCodeBaseRMIUnchunkedNoRepStrId());
                n = this.get_offset() - 4;
                this.write_codebase(s, this.get_offset());
            }
        }
        else if (!b && b2) {
            if (s == null) {
                this.write_long(this.repIdUtil.getStandardRMIUnchunkedId());
                n = this.get_offset() - 4;
            }
            else {
                this.write_long(this.repIdUtil.getCodeBaseRMIUnchunkedId());
                n = this.get_offset() - 4;
                this.write_codebase(s, this.get_offset());
            }
        }
        return n;
    }
    
    private void writeIDLValue(final Serializable s, final String s2) {
        if (s instanceof StreamableValue) {
            ((StreamableValue)s)._write(this.parent);
        }
        else if (s instanceof CustomValue) {
            ((CustomValue)s).marshal(this.parent);
        }
        else {
            final BoxedValueHelper helper = Utility.getHelper(s.getClass(), null, s2);
            boolean b = false;
            if (helper instanceof ValueHelper && s instanceof CustomMarshal) {
                try {
                    if (((ValueHelper)helper).get_type().type_modifier() == 1) {
                        b = true;
                    }
                }
                catch (final BadKind badKind) {
                    throw this.wrapper.badTypecodeForCustomValue(CompletionStatus.COMPLETED_MAYBE, badKind);
                }
            }
            if (b) {
                ((CustomMarshal)s).marshal(this.parent);
            }
            else {
                helper.write_value(this.parent, s);
            }
        }
    }
    
    private void writeEndTag(final boolean b) {
        if (b) {
            if (this.get_offset() == this.end_flag_position && this.bbwi.position() == this.end_flag_index) {
                this.bbwi.position(this.bbwi.position() - 4);
            }
            this.writeNestingLevel();
            this.end_flag_index = this.bbwi.position();
            this.end_flag_position = this.get_offset();
            ++this.chunkedValueNestingLevel;
        }
        ++this.end_flag;
    }
    
    private void writeNestingLevel() {
        if (this.orb == null || ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) || ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) <= 0) {
            this.write_long(this.chunkedValueNestingLevel);
        }
        else {
            this.write_long(this.end_flag);
        }
    }
    
    private void writeClass(String classDescValueRepId, final Class clazz) {
        if (classDescValueRepId == null) {
            classDescValueRepId = this.repIdStrs.getClassDescValueRepId();
        }
        this.updateIndirectionTable(this.writeValueTag(this.mustChunk, true, null), clazz, clazz);
        this.write_repositoryId(classDescValueRepId);
        if (this.mustChunk) {
            this.start_block();
            --this.end_flag;
            --this.chunkedValueNestingLevel;
        }
        else {
            --this.end_flag;
        }
        this.writeClassBody(clazz);
        if (this.mustChunk) {
            this.end_block();
        }
        this.writeEndTag(this.mustChunk);
    }
    
    private void writeClassBody(final Class clazz) {
        if (this.orb == null || ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) || ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) <= 0) {
            this.write_value(Util.getCodebase(clazz));
            this.write_value(this.repIdStrs.createForAnyType(clazz));
        }
        else {
            this.write_value(this.repIdStrs.createForAnyType(clazz));
            this.write_value(Util.getCodebase(clazz));
        }
    }
    
    private boolean shouldWriteAsIDLEntity(final Serializable s) {
        return s instanceof IDLEntity && !(s instanceof ValueBase) && !(s instanceof org.omg.CORBA.Object);
    }
    
    private void writeIDLEntity(final IDLEntity idlEntity) {
        this.mustChunk = true;
        final String forJavaType = this.repIdStrs.createForJavaType(idlEntity);
        final Class<? extends IDLEntity> class1 = idlEntity.getClass();
        final String codebase = Util.getCodebase(class1);
        this.updateIndirectionTable(this.writeValueTag(true, true, codebase), idlEntity, idlEntity);
        this.write_repositoryId(forJavaType);
        --this.end_flag;
        --this.chunkedValueNestingLevel;
        this.start_block();
        try {
            final ClassLoader classLoader = (class1 == null) ? null : class1.getClassLoader();
            final Class loadClassForClass = Utility.loadClassForClass(class1.getName() + "Helper", codebase, classLoader, class1, classLoader);
            final Class[] array = { org.omg.CORBA.portable.OutputStream.class, class1 };
            Method method;
            try {
                method = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction() {
                    @Override
                    public Object run() throws NoSuchMethodException {
                        return loadClassForClass.getDeclaredMethod("write", (Class[])array);
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                throw (NoSuchMethodException)ex.getException();
            }
            method.invoke(null, this.parent, idlEntity);
        }
        catch (final ClassNotFoundException ex2) {
            throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, ex2);
        }
        catch (final NoSuchMethodException ex3) {
            throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, ex3);
        }
        catch (final IllegalAccessException ex4) {
            throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, ex4);
        }
        catch (final InvocationTargetException ex5) {
            throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, ex5);
        }
        this.end_block();
        this.writeEndTag(true);
    }
    
    @Override
    public void write_Abstract(final Object o) {
        this.write_abstract_interface(o);
    }
    
    @Override
    public void write_Value(final Serializable s) {
        this.write_value(s);
    }
    
    @Override
    public void write_fixed(final BigDecimal bigDecimal, final short n, final short n2) {
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
    public void write_fixed(final BigDecimal bigDecimal) {
        this.write_fixed(bigDecimal.toString(), bigDecimal.signum());
    }
    
    public void write_fixed(final String s, final int n) {
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
    public String[] _truncatable_ids() {
        if (CDROutputStream_1_0._ids == null) {
            return null;
        }
        return CDROutputStream_1_0._ids.clone();
    }
    
    public void printBuffer() {
        printBuffer(this.bbwi);
    }
    
    public static void printBuffer(final ByteBufferWithInfo byteBufferWithInfo) {
        System.out.println("+++++++ Output Buffer ++++++++");
        System.out.println();
        System.out.println("Current position: " + byteBufferWithInfo.position());
        System.out.println("Total length : " + byteBufferWithInfo.buflen);
        System.out.println();
        final char[] array = new char[16];
        try {
            for (int i = 0; i < byteBufferWithInfo.position(); i += 16) {
                int j;
                for (j = 0; j < 16 && j + i < byteBufferWithInfo.position(); ++j) {
                    int value = byteBufferWithInfo.byteBuffer.get(i + j);
                    if (value < 0) {
                        value += 256;
                    }
                    String s = Integer.toHexString(value);
                    if (s.length() == 1) {
                        s = "0" + s;
                    }
                    System.out.print(s + " ");
                }
                while (j < 16) {
                    System.out.print("   ");
                    ++j;
                }
                int n;
                for (n = 0; n < 16 && n + i < byteBufferWithInfo.position(); ++n) {
                    if (ORBUtility.isPrintable((char)byteBufferWithInfo.byteBuffer.get(i + n))) {
                        array[n] = (char)byteBufferWithInfo.byteBuffer.get(i + n);
                    }
                    else {
                        array[n] = '.';
                    }
                }
                System.out.println(new String(array, 0, n));
            }
        }
        catch (final Throwable t) {
            t.printStackTrace();
        }
        System.out.println("++++++++++++++++++++++++++++++");
    }
    
    @Override
    public void writeIndirection(final int n, final int n2) {
        this.handleSpecialChunkBegin(this.computeAlignment(4) + 8);
        this.write_long(n);
        this.write_long(n2 - this.parent.getRealIndex(this.get_offset()));
        this.handleSpecialChunkEnd();
    }
    
    protected CodeSetConversion.CTBConverter getCharConverter() {
        if (this.charConverter == null) {
            this.charConverter = this.parent.createCharCTBConverter();
        }
        return this.charConverter;
    }
    
    protected CodeSetConversion.CTBConverter getWCharConverter() {
        if (this.wcharConverter == null) {
            this.wcharConverter = this.parent.createWCharCTBConverter();
        }
        return this.wcharConverter;
    }
    
    protected void dprint(final String s) {
        if (this.debug) {
            ORBUtility.dprint(this, s);
        }
    }
    
    @Override
    void alignOnBoundary(final int n) {
        this.alignAndReserve(n, 0);
    }
    
    @Override
    public void start_value(final String s) {
        if (this.debug) {
            this.dprint("start_value w/ rep id " + s + " called at pos " + this.get_offset() + " position " + this.bbwi.position());
        }
        if (this.inBlock) {
            this.end_block();
        }
        this.writeValueTag(true, true, null);
        this.write_repositoryId(s);
        --this.end_flag;
        --this.chunkedValueNestingLevel;
        this.start_block();
    }
    
    @Override
    public void end_value() {
        if (this.debug) {
            this.dprint("end_value called at pos " + this.get_offset() + " position " + this.bbwi.position());
        }
        this.end_block();
        this.writeEndTag(true);
        if (this.debug) {
            this.dprint("mustChunk is " + this.mustChunk);
        }
        if (this.mustChunk) {
            this.start_block();
        }
    }
    
    @Override
    public void close() throws IOException {
        this.getBufferManager().close();
        if (this.getByteBufferWithInfo() != null && this.getByteBuffer() != null) {
            final MessageMediator messageMediator = this.parent.getMessageMediator();
            if (messageMediator != null) {
                final CDRInputObject cdrInputObject = (CDRInputObject)messageMediator.getInputObject();
                if (cdrInputObject != null && cdrInputObject.isSharing(this.getByteBuffer())) {
                    cdrInputObject.setByteBuffer(null);
                    cdrInputObject.setByteBufferWithInfo(null);
                }
            }
            final ByteBufferPool byteBufferPool = this.orb.getByteBufferPool();
            if (this.debug) {
                final int identityHashCode = System.identityHashCode(this.bbwi.byteBuffer);
                final StringBuffer sb = new StringBuffer(80);
                sb.append(".close - releasing ByteBuffer id (");
                sb.append(identityHashCode).append(") to ByteBufferPool.");
                this.dprint(sb.toString());
            }
            byteBufferPool.releaseByteBuffer(this.getByteBuffer());
            this.bbwi.byteBuffer = null;
            this.bbwi = null;
        }
    }
    
    static {
        _ids = new String[] { "IDL:omg.org/CORBA/DataOutputStream:1.0" };
    }
}
