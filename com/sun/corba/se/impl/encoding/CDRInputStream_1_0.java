package com.sun.corba.se.impl.encoding;

import org.omg.CORBA.Context;
import java.io.IOException;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.org.omg.SendingContext.CodeBase;
import java.math.BigDecimal;
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
import org.omg.CORBA.portable.ValueBase;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.omg.CORBA.CustomMarshal;
import com.sun.corba.se.impl.orbutil.RepositoryIdInterface;
import java.net.MalformedURLException;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.CORBA.DataInputStream;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.TypeCodePackage.BadKind;
import com.sun.org.omg.CORBA.portable.ValueHelper;
import org.omg.CORBA.SystemException;
import org.omg.SendingContext.RunTime;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.IndirectionException;
import java.io.Serializable;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import org.omg.CORBA.portable.Delegate;
import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import org.omg.CORBA.portable.InvokeHandler;
import com.sun.corba.se.impl.util.Utility;
import javax.rmi.CORBA.Tie;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.portable.IDLEntity;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.spi.ior.IORFactories;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.Any;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.impl.corba.PrincipalImpl;
import org.omg.CORBA.Principal;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import java.nio.ByteBuffer;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.impl.orbutil.RepositoryIdUtility;
import com.sun.corba.se.impl.orbutil.CacheTable;
import javax.rmi.CORBA.ValueHandler;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public class CDRInputStream_1_0 extends CDRInputStreamBase implements RestorableInputStream
{
    private static final String kReadMethod = "read";
    private static final int maxBlockLength = 2147483392;
    protected BufferManagerRead bufferManagerRead;
    protected ByteBufferWithInfo bbwi;
    private boolean debug;
    protected boolean littleEndian;
    protected ORB orb;
    protected ORBUtilSystemException wrapper;
    protected OMGSystemException omgWrapper;
    protected ValueHandler valueHandler;
    private CacheTable valueCache;
    private CacheTable repositoryIdCache;
    private CacheTable codebaseCache;
    protected int blockLength;
    protected int end_flag;
    private int chunkedValueNestingLevel;
    protected int valueIndirection;
    protected int stringIndirection;
    protected boolean isChunked;
    private RepositoryIdUtility repIdUtil;
    private RepositoryIdStrings repIdStrs;
    private CodeSetConversion.BTCConverter charConverter;
    private CodeSetConversion.BTCConverter wcharConverter;
    private boolean specialNoOptionalDataState;
    private static final String _id = "IDL:omg.org/CORBA/DataInputStream:1.0";
    private static final String[] _ids;
    protected MarkAndResetHandler markAndResetHandler;
    
    public CDRInputStream_1_0() {
        this.debug = false;
        this.valueHandler = null;
        this.valueCache = null;
        this.repositoryIdCache = null;
        this.codebaseCache = null;
        this.blockLength = 2147483392;
        this.end_flag = 0;
        this.chunkedValueNestingLevel = 0;
        this.valueIndirection = 0;
        this.stringIndirection = 0;
        this.isChunked = false;
        this.specialNoOptionalDataState = false;
        this.markAndResetHandler = null;
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
        cdrInputStreamBase.init(this.orb, this.bbwi.byteBuffer, this.bbwi.buflen, this.littleEndian, this.bufferManagerRead);
        ((CDRInputStream_1_0)cdrInputStreamBase).bbwi.position(this.bbwi.position());
        ((CDRInputStream_1_0)cdrInputStreamBase).bbwi.byteBuffer.limit(this.bbwi.buflen);
        return cdrInputStreamBase;
    }
    
    @Override
    public void init(final org.omg.CORBA.ORB orb, final ByteBuffer byteBuffer, final int buflen, final boolean littleEndian, final BufferManagerRead bufferManagerRead) {
        this.orb = (ORB)orb;
        this.wrapper = ORBUtilSystemException.get((ORB)orb, "rpc.encoding");
        this.omgWrapper = OMGSystemException.get((ORB)orb, "rpc.encoding");
        this.littleEndian = littleEndian;
        this.bufferManagerRead = bufferManagerRead;
        this.bbwi = new ByteBufferWithInfo(orb, byteBuffer, 0);
        this.bbwi.buflen = buflen;
        this.bbwi.byteBuffer.limit(this.bbwi.buflen);
        this.markAndResetHandler = this.bufferManagerRead.getMarkAndResetHandler();
        this.debug = ((ORB)orb).transportDebugFlag;
    }
    
    @Override
    void performORBVersionSpecificInit() {
        this.createRepositoryIdHandlers();
    }
    
    private final void createRepositoryIdHandlers() {
        this.repIdUtil = RepositoryIdFactory.getRepIdUtility();
        this.repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
    }
    
    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_0;
    }
    
    @Override
    void setHeaderPadding(final boolean b) {
        throw this.wrapper.giopVersionError();
    }
    
    protected final int computeAlignment(final int n, final int n2) {
        if (n2 > 1) {
            final int n3 = n & n2 - 1;
            if (n3 != 0) {
                return n2 - n3;
            }
        }
        return 0;
    }
    
    public int getSize() {
        return this.bbwi.position();
    }
    
    protected void checkBlockLength(final int n, final int n2) {
        if (!this.isChunked) {
            return;
        }
        if (this.specialNoOptionalDataState) {
            throw this.omgWrapper.rmiiiopOptionalDataIncompatible1();
        }
        boolean b = false;
        if (this.blockLength == this.get_offset()) {
            this.blockLength = 2147483392;
            this.start_block();
            if (this.blockLength == 2147483392) {
                b = true;
            }
        }
        else if (this.blockLength < this.get_offset()) {
            throw this.wrapper.chunkOverflow();
        }
        final int n3 = this.computeAlignment(this.bbwi.position(), n) + n2;
        if (this.blockLength != 2147483392 && this.blockLength < this.get_offset() + n3) {
            throw this.omgWrapper.rmiiiopOptionalDataIncompatible2();
        }
        if (b) {
            final int read_long = this.read_long();
            this.bbwi.position(this.bbwi.position() - 4);
            if (read_long < 0) {
                throw this.omgWrapper.rmiiiopOptionalDataIncompatible3();
            }
        }
    }
    
    protected void alignAndCheck(final int n, final int n2) {
        this.checkBlockLength(n, n2);
        this.bbwi.position(this.bbwi.position() + this.computeAlignment(this.bbwi.position(), n));
        if (this.bbwi.position() + n2 > this.bbwi.buflen) {
            this.grow(n, n2);
        }
    }
    
    protected void grow(final int n, final int needed) {
        this.bbwi.needed = needed;
        this.bbwi = this.bufferManagerRead.underflow(this.bbwi);
    }
    
    @Override
    public final void consumeEndian() {
        this.littleEndian = this.read_boolean();
    }
    
    public final double read_longdouble() {
        throw this.wrapper.longDoubleNotImplemented(CompletionStatus.COMPLETED_MAYBE);
    }
    
    @Override
    public final boolean read_boolean() {
        return this.read_octet() != 0;
    }
    
    @Override
    public final char read_char() {
        this.alignAndCheck(1, 1);
        return this.getConvertedChars(1, this.getCharConverter())[0];
    }
    
    @Override
    public char read_wchar() {
        if (ORBUtility.isForeignORB(this.orb)) {
            throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
        }
        this.alignAndCheck(2, 2);
        int n;
        int n2;
        if (this.littleEndian) {
            n = (this.bbwi.byteBuffer.get(this.bbwi.position()) & 0xFF);
            this.bbwi.position(this.bbwi.position() + 1);
            n2 = (this.bbwi.byteBuffer.get(this.bbwi.position()) & 0xFF);
            this.bbwi.position(this.bbwi.position() + 1);
        }
        else {
            n2 = (this.bbwi.byteBuffer.get(this.bbwi.position()) & 0xFF);
            this.bbwi.position(this.bbwi.position() + 1);
            n = (this.bbwi.byteBuffer.get(this.bbwi.position()) & 0xFF);
            this.bbwi.position(this.bbwi.position() + 1);
        }
        return (char)((n2 << 8) + (n << 0));
    }
    
    @Override
    public final byte read_octet() {
        this.alignAndCheck(1, 1);
        final byte value = this.bbwi.byteBuffer.get(this.bbwi.position());
        this.bbwi.position(this.bbwi.position() + 1);
        return value;
    }
    
    @Override
    public final short read_short() {
        this.alignAndCheck(2, 2);
        int n;
        int n2;
        if (this.littleEndian) {
            n = (this.bbwi.byteBuffer.get(this.bbwi.position()) << 0 & 0xFF);
            this.bbwi.position(this.bbwi.position() + 1);
            n2 = (this.bbwi.byteBuffer.get(this.bbwi.position()) << 8 & 0xFF00);
            this.bbwi.position(this.bbwi.position() + 1);
        }
        else {
            n2 = (this.bbwi.byteBuffer.get(this.bbwi.position()) << 8 & 0xFF00);
            this.bbwi.position(this.bbwi.position() + 1);
            n = (this.bbwi.byteBuffer.get(this.bbwi.position()) << 0 & 0xFF);
            this.bbwi.position(this.bbwi.position() + 1);
        }
        return (short)(n2 | n);
    }
    
    @Override
    public final short read_ushort() {
        return this.read_short();
    }
    
    @Override
    public final int read_long() {
        this.alignAndCheck(4, 4);
        int position = this.bbwi.position();
        int n;
        int n2;
        int n3;
        int n4;
        if (this.littleEndian) {
            n = (this.bbwi.byteBuffer.get(position++) & 0xFF);
            n2 = (this.bbwi.byteBuffer.get(position++) & 0xFF);
            n3 = (this.bbwi.byteBuffer.get(position++) & 0xFF);
            n4 = (this.bbwi.byteBuffer.get(position++) & 0xFF);
        }
        else {
            n4 = (this.bbwi.byteBuffer.get(position++) & 0xFF);
            n3 = (this.bbwi.byteBuffer.get(position++) & 0xFF);
            n2 = (this.bbwi.byteBuffer.get(position++) & 0xFF);
            n = (this.bbwi.byteBuffer.get(position++) & 0xFF);
        }
        this.bbwi.position(position);
        return n4 << 24 | n3 << 16 | n2 << 8 | n;
    }
    
    @Override
    public final int read_ulong() {
        return this.read_long();
    }
    
    @Override
    public final long read_longlong() {
        this.alignAndCheck(8, 8);
        long n;
        long n2;
        if (this.littleEndian) {
            n = ((long)this.read_long() & 0xFFFFFFFFL);
            n2 = (long)this.read_long() << 32;
        }
        else {
            n2 = (long)this.read_long() << 32;
            n = ((long)this.read_long() & 0xFFFFFFFFL);
        }
        return n2 | n;
    }
    
    @Override
    public final long read_ulonglong() {
        return this.read_longlong();
    }
    
    @Override
    public final float read_float() {
        return Float.intBitsToFloat(this.read_long());
    }
    
    @Override
    public final double read_double() {
        return Double.longBitsToDouble(this.read_longlong());
    }
    
    protected final void checkForNegativeLength(final int n) {
        if (n < 0) {
            throw this.wrapper.negativeStringLength(CompletionStatus.COMPLETED_MAYBE, new Integer(n));
        }
    }
    
    protected final String readStringOrIndirection(final boolean b) {
        final int read_long = this.read_long();
        if (b) {
            if (read_long == -1) {
                return null;
            }
            this.stringIndirection = this.get_offset() - 4;
        }
        this.checkForNegativeLength(read_long);
        return this.internalReadString(read_long);
    }
    
    private final String internalReadString(final int n) {
        if (n == 0) {
            return new String("");
        }
        final char[] convertedChars = this.getConvertedChars(n - 1, this.getCharConverter());
        this.read_octet();
        return new String(convertedChars, 0, this.getCharConverter().getNumChars());
    }
    
    @Override
    public final String read_string() {
        return this.readStringOrIndirection(false);
    }
    
    @Override
    public String read_wstring() {
        if (ORBUtility.isForeignORB(this.orb)) {
            throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
        }
        int read_long = this.read_long();
        if (read_long == 0) {
            return new String("");
        }
        this.checkForNegativeLength(read_long);
        final char[] array = new char[--read_long];
        for (int i = 0; i < read_long; ++i) {
            array[i] = this.read_wchar();
        }
        this.read_wchar();
        return new String(array);
    }
    
    @Override
    public final void read_octet_array(final byte[] array, final int n, final int n2) {
        if (array == null) {
            throw this.wrapper.nullParam();
        }
        if (n2 == 0) {
            return;
        }
        this.alignAndCheck(1, 1);
        int n5;
        for (int i = n; i < n2 + n; i += n5) {
            int n3 = this.bbwi.buflen - this.bbwi.position();
            if (n3 <= 0) {
                this.grow(1, 1);
                n3 = this.bbwi.buflen - this.bbwi.position();
            }
            final int n4 = n2 + n - i;
            n5 = ((n4 < n3) ? n4 : n3);
            for (int j = 0; j < n5; ++j) {
                array[i + j] = this.bbwi.byteBuffer.get(this.bbwi.position() + j);
            }
            this.bbwi.position(this.bbwi.position() + n5);
        }
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
            this.dprintThrowable(marshal);
        }
        create_any.read_value(this.parent, typeCodeImpl);
        return create_any;
    }
    
    @Override
    public org.omg.CORBA.Object read_Object() {
        return this.read_Object(null);
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
            this.orb.validateIORClass(className);
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
        return internalIORToObject(ior, stubFactory, this.orb);
    }
    
    public static org.omg.CORBA.Object internalIORToObject(final IOR ior, final PresentationManager.StubFactory stubFactory, final ORB orb) {
        final ORBUtilSystemException value = ORBUtilSystemException.get(orb, "rpc.encoding");
        final Object servant = ior.getProfile().getServant();
        if (servant != null) {
            if (servant instanceof Tie) {
                final org.omg.CORBA.Object object = (org.omg.CORBA.Object)Utility.loadStub((Tie)servant, stubFactory, ior.getProfile().getCodebase(), false);
                if (object != null) {
                    return object;
                }
                throw value.readObjectException();
            }
            else {
                if (!(servant instanceof org.omg.CORBA.Object)) {
                    throw value.badServantReadObject();
                }
                if (!(servant instanceof InvokeHandler)) {
                    return (org.omg.CORBA.Object)servant;
                }
            }
        }
        final CorbaClientDelegate clientDelegate = ORBUtility.makeClientDelegate(ior);
        org.omg.CORBA.Object stub;
        try {
            stub = stubFactory.makeStub();
        }
        catch (final Throwable t) {
            value.stubCreateError(t);
            if (t instanceof ThreadDeath) {
                throw (ThreadDeath)t;
            }
            stub = new CORBAObjectImpl();
        }
        StubAdapter.setDelegate(stub, clientDelegate);
        return stub;
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
    public Serializable read_value() {
        return this.read_value((Class)null);
    }
    
    private Serializable handleIndirection() {
        final int n = this.read_long() + this.get_offset() - 4;
        if (this.valueCache != null && this.valueCache.containsVal(n)) {
            return (Serializable)this.valueCache.getKey(n);
        }
        throw new IndirectionException(n);
    }
    
    private String readRepositoryIds(final int n, final Class clazz, final String s) {
        return this.readRepositoryIds(n, clazz, s, null);
    }
    
    private String readRepositoryIds(final int n, final Class clazz, final String s, final BoxedValueHelper boxedValueHelper) {
        switch (this.repIdUtil.getTypeInfo(n)) {
            case 0: {
                if (clazz != null) {
                    return this.repIdStrs.createForAnyType(clazz);
                }
                if (s != null) {
                    return s;
                }
                if (boxedValueHelper != null) {
                    return boxedValueHelper.get_id();
                }
                throw this.wrapper.expectedTypeNullAndNoRepId(CompletionStatus.COMPLETED_MAYBE);
            }
            case 2: {
                return this.read_repositoryId();
            }
            case 6: {
                return this.read_repositoryIds();
            }
            default: {
                throw this.wrapper.badValueTag(CompletionStatus.COMPLETED_MAYBE, Integer.toHexString(n));
            }
        }
    }
    
    @Override
    public Serializable read_value(final Class clazz) {
        final int valueTag = this.readValueTag();
        if (valueTag == 0) {
            return null;
        }
        if (valueTag == -1) {
            return this.handleIndirection();
        }
        final int n = this.get_offset() - 4;
        final boolean isChunked = this.isChunked;
        this.isChunked = this.repIdUtil.isChunkedEncoding(valueTag);
        String read_codebase_URL = null;
        if (this.repIdUtil.isCodeBasePresent(valueTag)) {
            read_codebase_URL = this.read_codebase_URL();
        }
        final String repositoryIds = this.readRepositoryIds(valueTag, clazz, null);
        this.start_block();
        --this.end_flag;
        if (this.isChunked) {
            --this.chunkedValueNestingLevel;
        }
        Object o;
        if (repositoryIds.equals(this.repIdStrs.getWStringValueRepId())) {
            o = this.read_wstring();
        }
        else if (repositoryIds.equals(this.repIdStrs.getClassDescValueRepId())) {
            o = this.readClass();
        }
        else {
            Class classFromString = clazz;
            if (clazz == null || !repositoryIds.equals(this.repIdStrs.createForAnyType(clazz))) {
                classFromString = this.getClassFromString(repositoryIds, read_codebase_URL, clazz);
            }
            if (classFromString == null) {
                throw this.wrapper.couldNotFindClass(CompletionStatus.COMPLETED_MAYBE, new ClassNotFoundException());
            }
            if (classFromString != null && IDLEntity.class.isAssignableFrom(classFromString)) {
                o = this.readIDLValue(n, repositoryIds, classFromString, read_codebase_URL);
            }
            else {
                try {
                    if (this.valueHandler == null) {
                        this.valueHandler = ORBUtility.createValueHandler();
                    }
                    o = this.valueHandler.readValue(this.parent, n, classFromString, repositoryIds, this.getCodeBase());
                }
                catch (final SystemException ex) {
                    throw ex;
                }
                catch (final Exception ex2) {
                    throw this.wrapper.valuehandlerReadException(CompletionStatus.COMPLETED_MAYBE, ex2);
                }
                catch (final Error error) {
                    throw this.wrapper.valuehandlerReadError(CompletionStatus.COMPLETED_MAYBE, error);
                }
            }
        }
        this.handleEndOfValue();
        this.readEndTag();
        if (this.valueCache == null) {
            this.valueCache = new CacheTable(this.orb, false);
        }
        this.valueCache.put(o, n);
        this.isChunked = isChunked;
        this.start_block();
        return (Serializable)o;
    }
    
    @Override
    public Serializable read_value(BoxedValueHelper helper) {
        final int valueTag = this.readValueTag();
        if (valueTag == 0) {
            return null;
        }
        if (valueTag != -1) {
            final int valueIndirection = this.get_offset() - 4;
            final boolean isChunked = this.isChunked;
            this.isChunked = this.repIdUtil.isChunkedEncoding(valueTag);
            String read_codebase_URL = null;
            if (this.repIdUtil.isCodeBasePresent(valueTag)) {
                read_codebase_URL = this.read_codebase_URL();
            }
            final String repositoryIds = this.readRepositoryIds(valueTag, null, null, helper);
            if (!repositoryIds.equals(helper.get_id())) {
                helper = Utility.getHelper(null, read_codebase_URL, repositoryIds);
            }
            this.start_block();
            --this.end_flag;
            if (this.isChunked) {
                --this.chunkedValueNestingLevel;
            }
            Object o;
            if (helper instanceof ValueHelper) {
                o = this.readIDLValueWithHelper((ValueHelper)helper, valueIndirection);
            }
            else {
                this.valueIndirection = valueIndirection;
                o = helper.read_value(this.parent);
            }
            this.handleEndOfValue();
            this.readEndTag();
            if (this.valueCache == null) {
                this.valueCache = new CacheTable(this.orb, false);
            }
            this.valueCache.put(o, valueIndirection);
            this.isChunked = isChunked;
            this.start_block();
            return (Serializable)o;
        }
        final int n = this.read_long() + this.get_offset() - 4;
        if (this.valueCache != null && this.valueCache.containsVal(n)) {
            return (Serializable)this.valueCache.getKey(n);
        }
        throw new IndirectionException(n);
    }
    
    private boolean isCustomType(final ValueHelper valueHelper) {
        try {
            final TypeCode get_type = valueHelper.get_type();
            if (get_type.kind().value() == 29) {
                return get_type.type_modifier() == 1;
            }
        }
        catch (final BadKind badKind) {
            throw this.wrapper.badKind(badKind);
        }
        return false;
    }
    
    @Override
    public Serializable read_value(final Serializable s) {
        if (this.valueCache == null) {
            this.valueCache = new CacheTable(this.orb, false);
        }
        this.valueCache.put(s, this.valueIndirection);
        if (s instanceof StreamableValue) {
            ((StreamableValue)s)._read(this.parent);
        }
        else if (s instanceof CustomValue) {
            ((CustomValue)s).unmarshal(this.parent);
        }
        return s;
    }
    
    @Override
    public Serializable read_value(final String s) {
        final int valueTag = this.readValueTag();
        if (valueTag == 0) {
            return null;
        }
        if (valueTag != -1) {
            final int valueIndirection = this.get_offset() - 4;
            final boolean isChunked = this.isChunked;
            this.isChunked = this.repIdUtil.isChunkedEncoding(valueTag);
            String read_codebase_URL = null;
            if (this.repIdUtil.isCodeBasePresent(valueTag)) {
                read_codebase_URL = this.read_codebase_URL();
            }
            final ValueFactory factory = Utility.getFactory(null, read_codebase_URL, this.orb, this.readRepositoryIds(valueTag, null, s));
            this.start_block();
            --this.end_flag;
            if (this.isChunked) {
                --this.chunkedValueNestingLevel;
            }
            this.valueIndirection = valueIndirection;
            final Serializable read_value = factory.read_value(this.parent);
            this.handleEndOfValue();
            this.readEndTag();
            if (this.valueCache == null) {
                this.valueCache = new CacheTable(this.orb, false);
            }
            this.valueCache.put(read_value, valueIndirection);
            this.isChunked = isChunked;
            this.start_block();
            return read_value;
        }
        final int n = this.read_long() + this.get_offset() - 4;
        if (this.valueCache != null && this.valueCache.containsVal(n)) {
            return (Serializable)this.valueCache.getKey(n);
        }
        throw new IndirectionException(n);
    }
    
    private Class readClass() {
        String s;
        String s2;
        if (this.orb == null || ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) || ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) <= 0) {
            s = (String)this.read_value(String.class);
            s2 = (String)this.read_value(String.class);
        }
        else {
            s2 = (String)this.read_value(String.class);
            s = (String)this.read_value(String.class);
        }
        if (this.debug) {
            this.dprint("readClass codebases: " + s + " rep Id: " + s2);
        }
        final RepositoryIdInterface fromString = this.repIdStrs.getFromString(s2);
        Class classFromType;
        try {
            classFromType = fromString.getClassFromType(s);
        }
        catch (final ClassNotFoundException ex) {
            throw this.wrapper.cnfeReadClass(CompletionStatus.COMPLETED_MAYBE, ex, fromString.getClassName());
        }
        catch (final MalformedURLException ex2) {
            throw this.wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, ex2, fromString.getClassName(), s);
        }
        return classFromType;
    }
    
    private Object readIDLValueWithHelper(final ValueHelper valueHelper, final int n) {
        Method declaredMethod;
        try {
            declaredMethod = valueHelper.getClass().getDeclaredMethod("read", org.omg.CORBA.portable.InputStream.class, valueHelper.get_class());
        }
        catch (final NoSuchMethodException ex) {
            return valueHelper.read_value(this.parent);
        }
        CustomMarshal instance;
        try {
            instance = valueHelper.get_class().newInstance();
        }
        catch (final InstantiationException ex2) {
            throw this.wrapper.couldNotInstantiateHelper(ex2, valueHelper.get_class());
        }
        catch (final IllegalAccessException ex3) {
            return valueHelper.read_value(this.parent);
        }
        if (this.valueCache == null) {
            this.valueCache = new CacheTable(this.orb, false);
        }
        this.valueCache.put(instance, n);
        if (instance instanceof CustomMarshal && this.isCustomType(valueHelper)) {
            instance.unmarshal(this.parent);
            return instance;
        }
        try {
            declaredMethod.invoke(valueHelper, this.parent, instance);
            return instance;
        }
        catch (final IllegalAccessException ex4) {
            throw this.wrapper.couldNotInvokeHelperReadMethod(ex4, valueHelper.get_class());
        }
        catch (final InvocationTargetException ex5) {
            throw this.wrapper.couldNotInvokeHelperReadMethod(ex5, valueHelper.get_class());
        }
    }
    
    private Object readBoxedIDLEntity(final Class clazz, final String s) {
        Object o = null;
        try {
            final ClassLoader classLoader = (clazz == null) ? null : clazz.getClassLoader();
            final Class loadClassForClass;
            o = (loadClassForClass = Utility.loadClassForClass(clazz.getName() + "Helper", s, classLoader, clazz, classLoader));
            final Class[] array = { org.omg.CORBA.portable.InputStream.class };
            Method method;
            try {
                method = AccessController.doPrivileged((PrivilegedExceptionAction<Method>)new PrivilegedExceptionAction() {
                    @Override
                    public Object run() throws NoSuchMethodException {
                        return loadClassForClass.getDeclaredMethod("read", (Class[])array);
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                throw (NoSuchMethodException)ex.getException();
            }
            return method.invoke(null, this.parent);
        }
        catch (final ClassNotFoundException ex2) {
            throw this.wrapper.couldNotInvokeHelperReadMethod(ex2, o);
        }
        catch (final NoSuchMethodException ex3) {
            throw this.wrapper.couldNotInvokeHelperReadMethod(ex3, o);
        }
        catch (final IllegalAccessException ex4) {
            throw this.wrapper.couldNotInvokeHelperReadMethod(ex4, o);
        }
        catch (final InvocationTargetException ex5) {
            throw this.wrapper.couldNotInvokeHelperReadMethod(ex5, o);
        }
    }
    
    private Object readIDLValue(final int valueIndirection, final String s, final Class clazz, final String s2) {
        ValueFactory factory;
        try {
            factory = Utility.getFactory(clazz, s2, this.orb, s);
        }
        catch (final MARSHAL marshal) {
            if (StreamableValue.class.isAssignableFrom(clazz) || CustomValue.class.isAssignableFrom(clazz) || !ValueBase.class.isAssignableFrom(clazz)) {
                return this.readBoxedIDLEntity(clazz, s2);
            }
            final BoxedValueHelper helper = Utility.getHelper(clazz, s2, s);
            if (helper instanceof ValueHelper) {
                return this.readIDLValueWithHelper((ValueHelper)helper, valueIndirection);
            }
            return helper.read_value(this.parent);
        }
        this.valueIndirection = valueIndirection;
        return factory.read_value(this.parent);
    }
    
    private void readEndTag() {
        if (this.isChunked) {
            final int read_long = this.read_long();
            if (read_long >= 0) {
                throw this.wrapper.positiveEndTag(CompletionStatus.COMPLETED_MAYBE, new Integer(read_long), new Integer(this.get_offset() - 4));
            }
            if (this.orb == null || ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) || ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) <= 0) {
                if (read_long < this.chunkedValueNestingLevel) {
                    throw this.wrapper.unexpectedEnclosingValuetype(CompletionStatus.COMPLETED_MAYBE, new Integer(read_long), new Integer(this.chunkedValueNestingLevel));
                }
                if (read_long != this.chunkedValueNestingLevel) {
                    this.bbwi.position(this.bbwi.position() - 4);
                }
            }
            else if (read_long != this.end_flag) {
                this.bbwi.position(this.bbwi.position() - 4);
            }
            ++this.chunkedValueNestingLevel;
        }
        ++this.end_flag;
    }
    
    protected int get_offset() {
        return this.bbwi.position();
    }
    
    private void start_block() {
        if (!this.isChunked) {
            return;
        }
        this.blockLength = 2147483392;
        this.blockLength = this.read_long();
        if (this.blockLength > 0 && this.blockLength < 2147483392) {
            this.blockLength += this.get_offset();
        }
        else {
            this.blockLength = 2147483392;
            this.bbwi.position(this.bbwi.position() - 4);
        }
    }
    
    private void handleEndOfValue() {
        if (!this.isChunked) {
            return;
        }
        while (this.blockLength != 2147483392) {
            this.end_block();
            this.start_block();
        }
        final int read_long = this.read_long();
        this.bbwi.position(this.bbwi.position() - 4);
        if (read_long < 0) {
            return;
        }
        if (read_long == 0 || read_long >= 2147483392) {
            this.read_value();
            this.handleEndOfValue();
            return;
        }
        throw this.wrapper.couldNotSkipBytes(CompletionStatus.COMPLETED_MAYBE, new Integer(read_long), new Integer(this.get_offset()));
    }
    
    private void end_block() {
        if (this.blockLength != 2147483392) {
            if (this.blockLength == this.get_offset()) {
                this.blockLength = 2147483392;
            }
            else {
                if (this.blockLength <= this.get_offset()) {
                    throw this.wrapper.badChunkLength(new Integer(this.blockLength), new Integer(this.get_offset()));
                }
                this.skipToOffset(this.blockLength);
            }
        }
    }
    
    private int readValueTag() {
        return this.read_long();
    }
    
    @Override
    public org.omg.CORBA.ORB orb() {
        return this.orb;
    }
    
    @Override
    public final void read_boolean_array(final boolean[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_boolean();
        }
    }
    
    @Override
    public final void read_char_array(final char[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_char();
        }
    }
    
    @Override
    public final void read_wchar_array(final char[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_wchar();
        }
    }
    
    @Override
    public final void read_short_array(final short[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_short();
        }
    }
    
    @Override
    public final void read_ushort_array(final short[] array, final int n, final int n2) {
        this.read_short_array(array, n, n2);
    }
    
    @Override
    public final void read_long_array(final int[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_long();
        }
    }
    
    @Override
    public final void read_ulong_array(final int[] array, final int n, final int n2) {
        this.read_long_array(array, n, n2);
    }
    
    @Override
    public final void read_longlong_array(final long[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_longlong();
        }
    }
    
    @Override
    public final void read_ulonglong_array(final long[] array, final int n, final int n2) {
        this.read_longlong_array(array, n, n2);
    }
    
    @Override
    public final void read_float_array(final float[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_float();
        }
    }
    
    @Override
    public final void read_double_array(final double[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_double();
        }
    }
    
    public final void read_any_array(final Any[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            array[i + n] = this.read_any();
        }
    }
    
    private String read_repositoryIds() {
        final int read_long = this.read_long();
        if (read_long != -1) {
            final int get_offset = this.get_offset();
            final String read_repositoryId = this.read_repositoryId();
            if (this.repositoryIdCache == null) {
                this.repositoryIdCache = new CacheTable(this.orb, false);
            }
            this.repositoryIdCache.put(read_repositoryId, get_offset);
            for (int i = 1; i < read_long; ++i) {
                this.read_repositoryId();
            }
            return read_repositoryId;
        }
        final int n = this.read_long() + this.get_offset() - 4;
        if (this.repositoryIdCache != null && this.repositoryIdCache.containsOrderedVal(n)) {
            return (String)this.repositoryIdCache.getKey(n);
        }
        throw this.wrapper.unableToLocateRepIdArray(new Integer(n));
    }
    
    private final String read_repositoryId() {
        final String stringOrIndirection = this.readStringOrIndirection(true);
        if (stringOrIndirection != null) {
            if (this.repositoryIdCache == null) {
                this.repositoryIdCache = new CacheTable(this.orb, false);
            }
            this.repositoryIdCache.put(stringOrIndirection, this.stringIndirection);
            return stringOrIndirection;
        }
        final int n = this.read_long() + this.get_offset() - 4;
        if (this.repositoryIdCache != null && this.repositoryIdCache.containsOrderedVal(n)) {
            return (String)this.repositoryIdCache.getKey(n);
        }
        throw this.wrapper.badRepIdIndirection(CompletionStatus.COMPLETED_MAYBE, new Integer(this.bbwi.position()));
    }
    
    private final String read_codebase_URL() {
        final String stringOrIndirection = this.readStringOrIndirection(true);
        if (stringOrIndirection != null) {
            if (this.codebaseCache == null) {
                this.codebaseCache = new CacheTable(this.orb, false);
            }
            this.codebaseCache.put(stringOrIndirection, this.stringIndirection);
            return stringOrIndirection;
        }
        final int n = this.read_long() + this.get_offset() - 4;
        if (this.codebaseCache != null && this.codebaseCache.containsVal(n)) {
            return (String)this.codebaseCache.getKey(n);
        }
        throw this.wrapper.badCodebaseIndirection(CompletionStatus.COMPLETED_MAYBE, new Integer(this.bbwi.position()));
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
    public BigDecimal read_fixed(final short n, final short n2) {
        final StringBuffer read_fixed_buffer = this.read_fixed_buffer();
        if (n != read_fixed_buffer.length()) {
            throw this.wrapper.badFixed(new Integer(n), new Integer(read_fixed_buffer.length()));
        }
        read_fixed_buffer.insert(n - n2, '.');
        return new BigDecimal(read_fixed_buffer.toString());
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
    public String[] _truncatable_ids() {
        if (CDRInputStream_1_0._ids == null) {
            return null;
        }
        return CDRInputStream_1_0._ids.clone();
    }
    
    public void printBuffer() {
        printBuffer(this.bbwi);
    }
    
    public static void printBuffer(final ByteBufferWithInfo byteBufferWithInfo) {
        System.out.println("----- Input Buffer -----");
        System.out.println();
        System.out.println("Current position: " + byteBufferWithInfo.position());
        System.out.println("Total length : " + byteBufferWithInfo.buflen);
        System.out.println();
        try {
            final char[] array = new char[16];
            for (int i = 0; i < byteBufferWithInfo.buflen; i += 16) {
                int j;
                for (j = 0; j < 16 && j + i < byteBufferWithInfo.buflen; ++j) {
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
                for (n = 0; n < 16 && n + i < byteBufferWithInfo.buflen; ++n) {
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
        System.out.println("------------------------");
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
    public int getBufferLength() {
        return this.bbwi.buflen;
    }
    
    @Override
    public void setBufferLength(final int buflen) {
        this.bbwi.buflen = buflen;
        this.bbwi.byteBuffer.limit(this.bbwi.buflen);
    }
    
    @Override
    public void setByteBufferWithInfo(final ByteBufferWithInfo bbwi) {
        this.bbwi = bbwi;
    }
    
    @Override
    public void setByteBuffer(final ByteBuffer byteBuffer) {
        this.bbwi.byteBuffer = byteBuffer;
    }
    
    @Override
    public int getIndex() {
        return this.bbwi.position();
    }
    
    @Override
    public void setIndex(final int n) {
        this.bbwi.position(n);
    }
    
    @Override
    public boolean isLittleEndian() {
        return this.littleEndian;
    }
    
    @Override
    public void orb(final org.omg.CORBA.ORB orb) {
        this.orb = (ORB)orb;
    }
    
    @Override
    public BufferManagerRead getBufferManager() {
        return this.bufferManagerRead;
    }
    
    private void skipToOffset(final int n) {
        int n5;
        for (int n2 = n - this.get_offset(), i = 0; i < n2; i += n5) {
            int n3 = this.bbwi.buflen - this.bbwi.position();
            if (n3 <= 0) {
                this.grow(1, 1);
                n3 = this.bbwi.buflen - this.bbwi.position();
            }
            final int n4 = n2 - i;
            n5 = ((n4 < n3) ? n4 : n3);
            this.bbwi.position(this.bbwi.position() + n5);
        }
    }
    
    @Override
    public Object createStreamMemento() {
        return new StreamMemento();
    }
    
    @Override
    public void restoreInternalState(final Object o) {
        final StreamMemento streamMemento = (StreamMemento)o;
        this.blockLength = streamMemento.blockLength_;
        this.end_flag = streamMemento.end_flag_;
        this.chunkedValueNestingLevel = streamMemento.chunkedValueNestingLevel_;
        this.valueIndirection = streamMemento.valueIndirection_;
        this.stringIndirection = streamMemento.stringIndirection_;
        this.isChunked = streamMemento.isChunked_;
        this.valueHandler = streamMemento.valueHandler_;
        this.specialNoOptionalDataState = streamMemento.specialNoOptionalDataState_;
        this.bbwi = streamMemento.bbwi_;
    }
    
    @Override
    public int getPosition() {
        return this.get_offset();
    }
    
    @Override
    public void mark(final int n) {
        this.markAndResetHandler.mark(this);
    }
    
    @Override
    public void reset() {
        this.markAndResetHandler.reset();
    }
    
    @Override
    CodeBase getCodeBase() {
        return this.parent.getCodeBase();
    }
    
    private Class getClassFromString(final String s, String implementation, final Class clazz) {
        final RepositoryIdInterface fromString = this.repIdStrs.getFromString(s);
        try {
            try {
                return fromString.getClassFromType(clazz, implementation);
            }
            catch (final ClassNotFoundException ex) {
                try {
                    if (this.getCodeBase() == null) {
                        return null;
                    }
                    implementation = this.getCodeBase().implementation(s);
                    if (implementation == null) {
                        return null;
                    }
                    return fromString.getClassFromType(clazz, implementation);
                }
                catch (final ClassNotFoundException ex2) {
                    this.dprintThrowable(ex2);
                    return null;
                }
            }
        }
        catch (final MalformedURLException ex3) {
            throw this.wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, ex3, s, implementation);
        }
    }
    
    private Class getClassFromString(final String s, String implementation) {
        final RepositoryIdInterface fromString = this.repIdStrs.getFromString(s);
        for (int i = 0; i < 3; ++i) {
            try {
                switch (i) {
                    case 0: {
                        return fromString.getClassFromType();
                    }
                    case 2: {
                        implementation = this.getCodeBase().implementation(s);
                        break;
                    }
                }
                if (implementation != null) {
                    return fromString.getClassFromType(implementation);
                }
            }
            catch (final ClassNotFoundException ex) {}
            catch (final MalformedURLException ex2) {
                throw this.wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, ex2, s, implementation);
            }
        }
        this.dprint("getClassFromString failed with rep id " + s + " and codebase " + implementation);
        return null;
    }
    
    char[] getConvertedChars(final int n, final CodeSetConversion.BTCConverter btcConverter) {
        if (this.bbwi.buflen - this.bbwi.position() >= n) {
            byte[] array;
            if (this.bbwi.byteBuffer.hasArray()) {
                array = this.bbwi.byteBuffer.array();
            }
            else {
                array = new byte[this.bbwi.buflen];
                for (int i = 0; i < this.bbwi.buflen; ++i) {
                    array[i] = this.bbwi.byteBuffer.get(i);
                }
            }
            final char[] chars = btcConverter.getChars(array, this.bbwi.position(), n);
            this.bbwi.position(this.bbwi.position() + n);
            return chars;
        }
        final byte[] array2 = new byte[n];
        this.read_octet_array(array2, 0, array2.length);
        return btcConverter.getChars(array2, 0, n);
    }
    
    protected CodeSetConversion.BTCConverter getCharConverter() {
        if (this.charConverter == null) {
            this.charConverter = this.parent.createCharBTCConverter();
        }
        return this.charConverter;
    }
    
    protected CodeSetConversion.BTCConverter getWCharConverter() {
        if (this.wcharConverter == null) {
            this.wcharConverter = this.parent.createWCharBTCConverter();
        }
        return this.wcharConverter;
    }
    
    protected void dprintThrowable(final Throwable t) {
        if (this.debug && t != null) {
            t.printStackTrace();
        }
    }
    
    protected void dprint(final String s) {
        if (this.debug) {
            ORBUtility.dprint(this, s);
        }
    }
    
    @Override
    void alignOnBoundary(final int n) {
        final int computeAlignment = this.computeAlignment(this.bbwi.position(), n);
        if (this.bbwi.position() + computeAlignment <= this.bbwi.buflen) {
            this.bbwi.position(this.bbwi.position() + computeAlignment);
        }
    }
    
    @Override
    public void resetCodeSetConverters() {
        this.charConverter = null;
        this.wcharConverter = null;
    }
    
    @Override
    public void start_value() {
        final int valueTag = this.readValueTag();
        if (valueTag == 0) {
            this.specialNoOptionalDataState = true;
            return;
        }
        if (valueTag == -1) {
            throw this.wrapper.customWrapperIndirection(CompletionStatus.COMPLETED_MAYBE);
        }
        if (this.repIdUtil.isCodeBasePresent(valueTag)) {
            throw this.wrapper.customWrapperWithCodebase(CompletionStatus.COMPLETED_MAYBE);
        }
        if (this.repIdUtil.getTypeInfo(valueTag) != 2) {
            throw this.wrapper.customWrapperNotSingleRepid(CompletionStatus.COMPLETED_MAYBE);
        }
        this.read_repositoryId();
        this.start_block();
        --this.end_flag;
        --this.chunkedValueNestingLevel;
    }
    
    @Override
    public void end_value() {
        if (this.specialNoOptionalDataState) {
            this.specialNoOptionalDataState = false;
            return;
        }
        this.handleEndOfValue();
        this.readEndTag();
        this.start_block();
    }
    
    @Override
    public void close() throws IOException {
        this.getBufferManager().close(this.bbwi);
        if (this.bbwi != null && this.getByteBuffer() != null) {
            final MessageMediator messageMediator = this.parent.getMessageMediator();
            if (messageMediator != null) {
                final CDROutputObject cdrOutputObject = (CDROutputObject)messageMediator.getOutputObject();
                if (cdrOutputObject != null && cdrOutputObject.isSharing(this.getByteBuffer())) {
                    cdrOutputObject.setByteBuffer(null);
                    cdrOutputObject.setByteBufferWithInfo(null);
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
            byteBufferPool.releaseByteBuffer(this.bbwi.byteBuffer);
            this.bbwi.byteBuffer = null;
            this.bbwi = null;
        }
    }
    
    static {
        _ids = new String[] { "IDL:omg.org/CORBA/DataInputStream:1.0" };
    }
    
    protected class StreamMemento
    {
        private int blockLength_;
        private int end_flag_;
        private int chunkedValueNestingLevel_;
        private int valueIndirection_;
        private int stringIndirection_;
        private boolean isChunked_;
        private ValueHandler valueHandler_;
        private ByteBufferWithInfo bbwi_;
        private boolean specialNoOptionalDataState_;
        
        public StreamMemento() {
            this.blockLength_ = CDRInputStream_1_0.this.blockLength;
            this.end_flag_ = CDRInputStream_1_0.this.end_flag;
            this.chunkedValueNestingLevel_ = CDRInputStream_1_0.this.chunkedValueNestingLevel;
            this.valueIndirection_ = CDRInputStream_1_0.this.valueIndirection;
            this.stringIndirection_ = CDRInputStream_1_0.this.stringIndirection;
            this.isChunked_ = CDRInputStream_1_0.this.isChunked;
            this.valueHandler_ = CDRInputStream_1_0.this.valueHandler;
            this.specialNoOptionalDataState_ = CDRInputStream_1_0.this.specialNoOptionalDataState;
            this.bbwi_ = new ByteBufferWithInfo(CDRInputStream_1_0.this.bbwi);
        }
    }
}
