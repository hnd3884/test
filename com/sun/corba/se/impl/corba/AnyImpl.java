package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.impl.io.ValueUtility;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import java.math.BigDecimal;
import java.io.Serializable;
import org.omg.CORBA.Principal;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.CORBA.TCKind;
import java.util.ArrayList;
import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA.CompletionStatus;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;

public class AnyImpl extends Any
{
    private TypeCodeImpl typeCode;
    protected ORB orb;
    private ORBUtilSystemException wrapper;
    private CDRInputStream stream;
    private long value;
    private Object object;
    private boolean isInitialized;
    private static final int DEFAULT_BUFFER_SIZE = 32;
    static boolean[] isStreamed;
    
    static AnyImpl convertToNative(final ORB orb, final Any any) {
        if (any instanceof AnyImpl) {
            return (AnyImpl)any;
        }
        final AnyImpl anyImpl = new AnyImpl(orb, any);
        anyImpl.typeCode = TypeCodeImpl.convertToNative(orb, anyImpl.typeCode);
        return anyImpl;
    }
    
    public AnyImpl(final ORB orb) {
        this.isInitialized = false;
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.presentation");
        this.typeCode = orb.get_primitive_tc(0);
        this.stream = null;
        this.object = null;
        this.value = 0L;
        this.isInitialized = true;
    }
    
    public AnyImpl(final ORB orb, final Any any) {
        this(orb);
        if (any instanceof AnyImpl) {
            final AnyImpl anyImpl = (AnyImpl)any;
            this.typeCode = anyImpl.typeCode;
            this.value = anyImpl.value;
            this.object = anyImpl.object;
            this.isInitialized = anyImpl.isInitialized;
            if (anyImpl.stream != null) {
                this.stream = anyImpl.stream.dup();
            }
        }
        else {
            this.read_value(any.create_input_stream(), any.type());
        }
    }
    
    @Override
    public TypeCode type() {
        return this.typeCode;
    }
    
    private TypeCode realType() {
        return this.realType(this.typeCode);
    }
    
    private TypeCode realType(final TypeCode typeCode) {
        TypeCode content_type = typeCode;
        try {
            while (content_type.kind().value() == 21) {
                content_type = content_type.content_type();
            }
        }
        catch (final BadKind badKind) {
            throw this.wrapper.badkindCannotOccur(badKind);
        }
        return content_type;
    }
    
    @Override
    public void type(final TypeCode typeCode) {
        this.typeCode = TypeCodeImpl.convertToNative(this.orb, typeCode);
        this.stream = null;
        this.value = 0L;
        this.object = null;
        this.isInitialized = (typeCode.kind().value() == 0);
    }
    
    @Override
    public boolean equal(final Any any) {
        if (any == this) {
            return true;
        }
        if (!this.typeCode.equal(any.type())) {
            return false;
        }
        final TypeCode realType = this.realType();
        switch (realType.kind().value()) {
            case 0:
            case 1: {
                return true;
            }
            case 2: {
                return this.extract_short() == any.extract_short();
            }
            case 3: {
                return this.extract_long() == any.extract_long();
            }
            case 4: {
                return this.extract_ushort() == any.extract_ushort();
            }
            case 5: {
                return this.extract_ulong() == any.extract_ulong();
            }
            case 6: {
                return this.extract_float() == any.extract_float();
            }
            case 7: {
                return this.extract_double() == any.extract_double();
            }
            case 8: {
                return this.extract_boolean() == any.extract_boolean();
            }
            case 9: {
                return this.extract_char() == any.extract_char();
            }
            case 26: {
                return this.extract_wchar() == any.extract_wchar();
            }
            case 10: {
                return this.extract_octet() == any.extract_octet();
            }
            case 11: {
                return this.extract_any().equal(any.extract_any());
            }
            case 12: {
                return this.extract_TypeCode().equal(any.extract_TypeCode());
            }
            case 18: {
                return this.extract_string().equals(any.extract_string());
            }
            case 27: {
                return this.extract_wstring().equals(any.extract_wstring());
            }
            case 23: {
                return this.extract_longlong() == any.extract_longlong();
            }
            case 24: {
                return this.extract_ulonglong() == any.extract_ulonglong();
            }
            case 14: {
                return this.extract_Object().equals(any.extract_Object());
            }
            case 13: {
                return this.extract_Principal().equals(any.extract_Principal());
            }
            case 17: {
                return this.extract_long() == any.extract_long();
            }
            case 28: {
                return this.extract_fixed().compareTo(any.extract_fixed()) == 0;
            }
            case 15:
            case 16:
            case 19:
            case 20:
            case 22: {
                return this.equalMember(realType, this.create_input_stream(), any.create_input_stream());
            }
            case 29:
            case 30: {
                return this.extract_Value().equals(any.extract_Value());
            }
            case 21: {
                throw this.wrapper.errorResolvingAlias();
            }
            case 25: {
                throw this.wrapper.tkLongDoubleNotSupported();
            }
            default: {
                throw this.wrapper.typecodeNotSupported();
            }
        }
    }
    
    private boolean equalMember(final TypeCode typeCode, final InputStream inputStream, final InputStream inputStream2) {
        final TypeCode realType = this.realType(typeCode);
        try {
            switch (realType.kind().value()) {
                case 0:
                case 1: {
                    return true;
                }
                case 2: {
                    return inputStream.read_short() == inputStream2.read_short();
                }
                case 3: {
                    return inputStream.read_long() == inputStream2.read_long();
                }
                case 4: {
                    return inputStream.read_ushort() == inputStream2.read_ushort();
                }
                case 5: {
                    return inputStream.read_ulong() == inputStream2.read_ulong();
                }
                case 6: {
                    return inputStream.read_float() == inputStream2.read_float();
                }
                case 7: {
                    return inputStream.read_double() == inputStream2.read_double();
                }
                case 8: {
                    return inputStream.read_boolean() == inputStream2.read_boolean();
                }
                case 9: {
                    return inputStream.read_char() == inputStream2.read_char();
                }
                case 26: {
                    return inputStream.read_wchar() == inputStream2.read_wchar();
                }
                case 10: {
                    return inputStream.read_octet() == inputStream2.read_octet();
                }
                case 11: {
                    return inputStream.read_any().equal(inputStream2.read_any());
                }
                case 12: {
                    return inputStream.read_TypeCode().equal(inputStream2.read_TypeCode());
                }
                case 18: {
                    return inputStream.read_string().equals(inputStream2.read_string());
                }
                case 27: {
                    return inputStream.read_wstring().equals(inputStream2.read_wstring());
                }
                case 23: {
                    return inputStream.read_longlong() == inputStream2.read_longlong();
                }
                case 24: {
                    return inputStream.read_ulonglong() == inputStream2.read_ulonglong();
                }
                case 14: {
                    return inputStream.read_Object().equals(inputStream2.read_Object());
                }
                case 13: {
                    return inputStream.read_Principal().equals(inputStream2.read_Principal());
                }
                case 17: {
                    return inputStream.read_long() == inputStream2.read_long();
                }
                case 28: {
                    return inputStream.read_fixed().compareTo(inputStream2.read_fixed()) == 0;
                }
                case 15:
                case 22: {
                    for (int member_count = realType.member_count(), i = 0; i < member_count; ++i) {
                        if (!this.equalMember(realType.member_type(i), inputStream, inputStream2)) {
                            return false;
                        }
                    }
                    return true;
                }
                case 16: {
                    final Any create_any = this.orb.create_any();
                    final Any create_any2 = this.orb.create_any();
                    create_any.read_value(inputStream, realType.discriminator_type());
                    create_any2.read_value(inputStream2, realType.discriminator_type());
                    if (!create_any.equal(create_any2)) {
                        return false;
                    }
                    final int currentUnionMemberIndex = TypeCodeImpl.convertToNative(this.orb, realType).currentUnionMemberIndex(create_any);
                    if (currentUnionMemberIndex == -1) {
                        throw this.wrapper.unionDiscriminatorError();
                    }
                    return this.equalMember(realType.member_type(currentUnionMemberIndex), inputStream, inputStream2);
                }
                case 19: {
                    final int read_long = inputStream.read_long();
                    inputStream2.read_long();
                    for (int j = 0; j < read_long; ++j) {
                        if (!this.equalMember(realType.content_type(), inputStream, inputStream2)) {
                            return false;
                        }
                    }
                    return true;
                }
                case 20: {
                    for (int member_count2 = realType.member_count(), k = 0; k < member_count2; ++k) {
                        if (!this.equalMember(realType.content_type(), inputStream, inputStream2)) {
                            return false;
                        }
                    }
                    return true;
                }
                case 29:
                case 30: {
                    return ((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value().equals(((org.omg.CORBA_2_3.portable.InputStream)inputStream2).read_value());
                }
                case 21: {
                    throw this.wrapper.errorResolvingAlias();
                }
                case 25: {
                    throw this.wrapper.tkLongDoubleNotSupported();
                }
                default: {
                    throw this.wrapper.typecodeNotSupported();
                }
            }
        }
        catch (final BadKind badKind) {
            throw this.wrapper.badkindCannotOccur();
        }
        catch (final Bounds bounds) {
            throw this.wrapper.boundsCannotOccur();
        }
    }
    
    @Override
    public OutputStream create_output_stream() {
        return AccessController.doPrivileged((PrivilegedAction<OutputStream>)new PrivilegedAction<AnyOutputStream>() {
            final /* synthetic */ ORB val$finalorb = AnyImpl.this.orb;
            
            @Override
            public AnyOutputStream run() {
                return new AnyOutputStream(this.val$finalorb);
            }
        });
    }
    
    @Override
    public InputStream create_input_stream() {
        if (AnyImpl.isStreamed[this.realType().kind().value()]) {
            return this.stream.dup();
        }
        final OutputStream create_output_stream = this.orb.create_output_stream();
        TCUtility.marshalIn(create_output_stream, this.realType(), this.value, this.object);
        return create_output_stream.create_input_stream();
    }
    
    @Override
    public void read_value(final InputStream inputStream, final TypeCode typeCode) {
        this.typeCode = TypeCodeImpl.convertToNative(this.orb, typeCode);
        final int value = this.realType().kind().value();
        if (value >= AnyImpl.isStreamed.length) {
            throw this.wrapper.invalidIsstreamedTckind(CompletionStatus.COMPLETED_MAYBE, new Integer(value));
        }
        if (AnyImpl.isStreamed[value]) {
            if (inputStream instanceof AnyInputStream) {
                this.stream = (CDRInputStream)inputStream;
            }
            else {
                final org.omg.CORBA_2_3.portable.OutputStream outputStream = (org.omg.CORBA_2_3.portable.OutputStream)this.orb.create_output_stream();
                this.typeCode.copy(inputStream, outputStream);
                this.stream = (CDRInputStream)outputStream.create_input_stream();
            }
        }
        else {
            final Object[] array = { this.object };
            final long[] array2 = { 0L };
            TCUtility.unmarshalIn(inputStream, this.realType(), array2, array);
            this.value = array2[0];
            this.object = array[0];
            this.stream = null;
        }
        this.isInitialized = true;
    }
    
    @Override
    public void write_value(final OutputStream outputStream) {
        if (AnyImpl.isStreamed[this.realType().kind().value()]) {
            this.typeCode.copy(this.stream.dup(), outputStream);
        }
        else {
            TCUtility.marshalIn(outputStream, this.realType(), this.value, this.object);
        }
    }
    
    @Override
    public void insert_Streamable(final Streamable object) {
        this.typeCode = TypeCodeImpl.convertToNative(this.orb, object._type());
        this.object = object;
        this.isInitialized = true;
    }
    
    @Override
    public Streamable extract_Streamable() {
        return (Streamable)this.object;
    }
    
    @Override
    public void insert_short(final short n) {
        this.typeCode = this.orb.get_primitive_tc(2);
        this.value = n;
        this.isInitialized = true;
    }
    
    private String getTCKindName(final int n) {
        if (n >= 0 && n < TypeCodeImpl.kindNames.length) {
            return TypeCodeImpl.kindNames[n];
        }
        return "UNKNOWN(" + n + ")";
    }
    
    private void checkExtractBadOperation(final int n) {
        if (!this.isInitialized) {
            throw this.wrapper.extractNotInitialized();
        }
        final int value = this.realType().kind().value();
        if (value != n) {
            throw this.wrapper.extractWrongType(this.getTCKindName(n), this.getTCKindName(value));
        }
    }
    
    private void checkExtractBadOperationList(final int[] array) {
        if (!this.isInitialized) {
            throw this.wrapper.extractNotInitialized();
        }
        final int value = this.realType().kind().value();
        for (int i = 0; i < array.length; ++i) {
            if (value == array[i]) {
                return;
            }
        }
        final ArrayList list = new ArrayList();
        for (int j = 0; j < array.length; ++j) {
            list.add(this.getTCKindName(array[j]));
        }
        throw this.wrapper.extractWrongTypeList(list, this.getTCKindName(value));
    }
    
    @Override
    public short extract_short() {
        this.checkExtractBadOperation(2);
        return (short)this.value;
    }
    
    @Override
    public void insert_long(final int n) {
        final int value = this.realType().kind().value();
        if (value != 3 && value != 17) {
            this.typeCode = this.orb.get_primitive_tc(3);
        }
        this.value = n;
        this.isInitialized = true;
    }
    
    @Override
    public int extract_long() {
        this.checkExtractBadOperationList(new int[] { 3, 17 });
        return (int)this.value;
    }
    
    @Override
    public void insert_ushort(final short n) {
        this.typeCode = this.orb.get_primitive_tc(4);
        this.value = n;
        this.isInitialized = true;
    }
    
    @Override
    public short extract_ushort() {
        this.checkExtractBadOperation(4);
        return (short)this.value;
    }
    
    @Override
    public void insert_ulong(final int n) {
        this.typeCode = this.orb.get_primitive_tc(5);
        this.value = n;
        this.isInitialized = true;
    }
    
    @Override
    public int extract_ulong() {
        this.checkExtractBadOperation(5);
        return (int)this.value;
    }
    
    @Override
    public void insert_float(final float n) {
        this.typeCode = this.orb.get_primitive_tc(6);
        this.value = Float.floatToIntBits(n);
        this.isInitialized = true;
    }
    
    @Override
    public float extract_float() {
        this.checkExtractBadOperation(6);
        return Float.intBitsToFloat((int)this.value);
    }
    
    @Override
    public void insert_double(final double n) {
        this.typeCode = this.orb.get_primitive_tc(7);
        this.value = Double.doubleToLongBits(n);
        this.isInitialized = true;
    }
    
    @Override
    public double extract_double() {
        this.checkExtractBadOperation(7);
        return Double.longBitsToDouble(this.value);
    }
    
    @Override
    public void insert_longlong(final long value) {
        this.typeCode = this.orb.get_primitive_tc(23);
        this.value = value;
        this.isInitialized = true;
    }
    
    @Override
    public long extract_longlong() {
        this.checkExtractBadOperation(23);
        return this.value;
    }
    
    @Override
    public void insert_ulonglong(final long value) {
        this.typeCode = this.orb.get_primitive_tc(24);
        this.value = value;
        this.isInitialized = true;
    }
    
    @Override
    public long extract_ulonglong() {
        this.checkExtractBadOperation(24);
        return this.value;
    }
    
    @Override
    public void insert_boolean(final boolean value) {
        this.typeCode = this.orb.get_primitive_tc(8);
        this.value = (value ? 1 : 0);
        this.isInitialized = true;
    }
    
    @Override
    public boolean extract_boolean() {
        this.checkExtractBadOperation(8);
        return this.value != 0L;
    }
    
    @Override
    public void insert_char(final char c) {
        this.typeCode = this.orb.get_primitive_tc(9);
        this.value = c;
        this.isInitialized = true;
    }
    
    @Override
    public char extract_char() {
        this.checkExtractBadOperation(9);
        return (char)this.value;
    }
    
    @Override
    public void insert_wchar(final char c) {
        this.typeCode = this.orb.get_primitive_tc(26);
        this.value = c;
        this.isInitialized = true;
    }
    
    @Override
    public char extract_wchar() {
        this.checkExtractBadOperation(26);
        return (char)this.value;
    }
    
    @Override
    public void insert_octet(final byte b) {
        this.typeCode = this.orb.get_primitive_tc(10);
        this.value = b;
        this.isInitialized = true;
    }
    
    @Override
    public byte extract_octet() {
        this.checkExtractBadOperation(10);
        return (byte)this.value;
    }
    
    @Override
    public void insert_string(final String object) {
        if (this.typeCode.kind() == TCKind.tk_string) {
            int length;
            try {
                length = this.typeCode.length();
            }
            catch (final BadKind badKind) {
                throw this.wrapper.badkindCannotOccur();
            }
            if (length != 0 && object != null && object.length() > length) {
                throw this.wrapper.badStringBounds(new Integer(object.length()), new Integer(length));
            }
        }
        else {
            this.typeCode = this.orb.get_primitive_tc(18);
        }
        this.object = object;
        this.isInitialized = true;
    }
    
    @Override
    public String extract_string() {
        this.checkExtractBadOperation(18);
        return (String)this.object;
    }
    
    @Override
    public void insert_wstring(final String object) {
        if (this.typeCode.kind() == TCKind.tk_wstring) {
            int length;
            try {
                length = this.typeCode.length();
            }
            catch (final BadKind badKind) {
                throw this.wrapper.badkindCannotOccur();
            }
            if (length != 0 && object != null && object.length() > length) {
                throw this.wrapper.badStringBounds(new Integer(object.length()), new Integer(length));
            }
        }
        else {
            this.typeCode = this.orb.get_primitive_tc(27);
        }
        this.object = object;
        this.isInitialized = true;
    }
    
    @Override
    public String extract_wstring() {
        this.checkExtractBadOperation(27);
        return (String)this.object;
    }
    
    @Override
    public void insert_any(final Any object) {
        this.typeCode = this.orb.get_primitive_tc(11);
        this.object = object;
        this.stream = null;
        this.isInitialized = true;
    }
    
    @Override
    public Any extract_any() {
        this.checkExtractBadOperation(11);
        return (Any)this.object;
    }
    
    @Override
    public void insert_Object(final org.omg.CORBA.Object object) {
        if (object == null) {
            this.typeCode = this.orb.get_primitive_tc(14);
        }
        else {
            if (!StubAdapter.isStub(object)) {
                throw this.wrapper.badInsertobjParam(CompletionStatus.COMPLETED_MAYBE, object.getClass().getName());
            }
            this.typeCode = new TypeCodeImpl(this.orb, 14, StubAdapter.getTypeIds(object)[0], "");
        }
        this.object = object;
        this.isInitialized = true;
    }
    
    @Override
    public void insert_Object(final org.omg.CORBA.Object object, final TypeCode typeCode) {
        try {
            if (!typeCode.id().equals("IDL:omg.org/CORBA/Object:1.0") && !object._is_a(typeCode.id())) {
                throw this.wrapper.insertObjectIncompatible();
            }
            this.typeCode = TypeCodeImpl.convertToNative(this.orb, typeCode);
            this.object = object;
        }
        catch (final Exception ex) {
            throw this.wrapper.insertObjectFailed(ex);
        }
        this.isInitialized = true;
    }
    
    @Override
    public org.omg.CORBA.Object extract_Object() {
        if (!this.isInitialized) {
            throw this.wrapper.extractNotInitialized();
        }
        try {
            final org.omg.CORBA.Object object = (org.omg.CORBA.Object)this.object;
            if (this.typeCode.id().equals("IDL:omg.org/CORBA/Object:1.0") || object._is_a(this.typeCode.id())) {
                return object;
            }
            throw this.wrapper.extractObjectIncompatible();
        }
        catch (final Exception ex) {
            throw this.wrapper.extractObjectFailed(ex);
        }
    }
    
    @Override
    public void insert_TypeCode(final TypeCode object) {
        this.typeCode = this.orb.get_primitive_tc(12);
        this.object = object;
        this.isInitialized = true;
    }
    
    @Override
    public TypeCode extract_TypeCode() {
        this.checkExtractBadOperation(12);
        return (TypeCode)this.object;
    }
    
    @Deprecated
    @Override
    public void insert_Principal(final Principal object) {
        this.typeCode = this.orb.get_primitive_tc(13);
        this.object = object;
        this.isInitialized = true;
    }
    
    @Deprecated
    @Override
    public Principal extract_Principal() {
        this.checkExtractBadOperation(13);
        return (Principal)this.object;
    }
    
    @Override
    public Serializable extract_Value() {
        this.checkExtractBadOperationList(new int[] { 29, 30, 32 });
        return (Serializable)this.object;
    }
    
    @Override
    public void insert_Value(final Serializable object) {
        this.object = object;
        TypeCode typeCode;
        if (object == null) {
            typeCode = this.orb.get_primitive_tc(TCKind.tk_value);
        }
        else {
            typeCode = this.createTypeCodeForClass(object.getClass(), (ORB)org.omg.CORBA.ORB.init());
        }
        this.typeCode = TypeCodeImpl.convertToNative(this.orb, typeCode);
        this.isInitialized = true;
    }
    
    @Override
    public void insert_Value(final Serializable object, final TypeCode typeCode) {
        this.object = object;
        this.typeCode = TypeCodeImpl.convertToNative(this.orb, typeCode);
        this.isInitialized = true;
    }
    
    @Override
    public void insert_fixed(final BigDecimal object) {
        this.typeCode = TypeCodeImpl.convertToNative(this.orb, this.orb.create_fixed_tc(TypeCodeImpl.digits(object), TypeCodeImpl.scale(object)));
        this.object = object;
        this.isInitialized = true;
    }
    
    @Override
    public void insert_fixed(final BigDecimal object, final TypeCode typeCode) {
        try {
            if (TypeCodeImpl.digits(object) > typeCode.fixed_digits() || TypeCodeImpl.scale(object) > typeCode.fixed_scale()) {
                throw this.wrapper.fixedNotMatch();
            }
        }
        catch (final BadKind badKind) {
            throw this.wrapper.fixedBadTypecode(badKind);
        }
        this.typeCode = TypeCodeImpl.convertToNative(this.orb, typeCode);
        this.object = object;
        this.isInitialized = true;
    }
    
    @Override
    public BigDecimal extract_fixed() {
        this.checkExtractBadOperation(28);
        return (BigDecimal)this.object;
    }
    
    public TypeCode createTypeCodeForClass(final Class clazz, final ORB orb) {
        final TypeCodeImpl typeCodeForClass = orb.getTypeCodeForClass(clazz);
        if (typeCodeForClass != null) {
            return typeCodeForClass;
        }
        final RepositoryIdStrings repIdStringsFactory = RepositoryIdFactory.getRepIdStringsFactory();
        if (clazz.isArray()) {
            final Class componentType = clazz.getComponentType();
            TypeCode typeCode;
            if (componentType.isPrimitive()) {
                typeCode = this.getPrimitiveTypeCodeForClass(componentType, orb);
            }
            else {
                typeCode = this.createTypeCodeForClass(componentType, orb);
            }
            return orb.create_value_box_tc(repIdStringsFactory.createForJavaType(clazz), "Sequence", orb.create_sequence_tc(0, typeCode));
        }
        if (clazz == String.class) {
            return orb.create_value_box_tc(repIdStringsFactory.createForJavaType(clazz), "StringValue", orb.create_string_tc(0));
        }
        final TypeCodeImpl typeCodeImpl = (TypeCodeImpl)ValueUtility.createTypeCodeForClass(orb, clazz, ORBUtility.createValueHandler());
        typeCodeImpl.setCaching(true);
        orb.setTypeCodeForClass(clazz, typeCodeImpl);
        return typeCodeImpl;
    }
    
    private TypeCode getPrimitiveTypeCodeForClass(final Class clazz, final ORB orb) {
        if (clazz == Integer.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_long);
        }
        if (clazz == Byte.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_octet);
        }
        if (clazz == Long.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_longlong);
        }
        if (clazz == Float.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_float);
        }
        if (clazz == Double.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_double);
        }
        if (clazz == Short.TYPE) {
            return orb.get_primitive_tc(TCKind.tk_short);
        }
        if (clazz == Character.TYPE) {
            if (ORBVersionFactory.getFOREIGN().compareTo(orb.getORBVersion()) == 0 || ORBVersionFactory.getNEWER().compareTo(orb.getORBVersion()) <= 0) {
                return orb.get_primitive_tc(TCKind.tk_wchar);
            }
            return orb.get_primitive_tc(TCKind.tk_char);
        }
        else {
            if (clazz == Boolean.TYPE) {
                return orb.get_primitive_tc(TCKind.tk_boolean);
            }
            return orb.get_primitive_tc(TCKind.tk_any);
        }
    }
    
    public Any extractAny(final TypeCode typeCode, final ORB orb) {
        final Any create_any = orb.create_any();
        final OutputStream create_output_stream = create_any.create_output_stream();
        TypeCodeImpl.convertToNative(orb, typeCode).copy(this.stream, create_output_stream);
        create_any.read_value(create_output_stream.create_input_stream(), typeCode);
        return create_any;
    }
    
    public static Any extractAnyFromStream(final TypeCode typeCode, final InputStream inputStream, final ORB orb) {
        final Any create_any = orb.create_any();
        final OutputStream create_output_stream = create_any.create_output_stream();
        TypeCodeImpl.convertToNative(orb, typeCode).copy(inputStream, create_output_stream);
        create_any.read_value(create_output_stream.create_input_stream(), typeCode);
        return create_any;
    }
    
    public boolean isInitialized() {
        return this.isInitialized;
    }
    
    static {
        AnyImpl.isStreamed = new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, true, false, false, true, true, true, true, false, false, false, false, false, false, false, false, false, false };
    }
    
    private static final class AnyInputStream extends EncapsInputStream
    {
        public AnyInputStream(final EncapsInputStream encapsInputStream) {
            super(encapsInputStream);
        }
    }
    
    private static final class AnyOutputStream extends EncapsOutputStream
    {
        public AnyOutputStream(final ORB orb) {
            super(orb);
        }
        
        @Override
        public InputStream create_input_stream() {
            return AccessController.doPrivileged((PrivilegedAction<AnyInputStream>)new PrivilegedAction<AnyInputStream>() {
                final /* synthetic */ InputStream val$is = super.create_input_stream();
                
                @Override
                public AnyInputStream run() {
                    return new AnyInputStream((EncapsInputStream)this.val$is);
                }
            });
        }
    }
}
