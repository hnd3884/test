package sun.invoke.util;

import java.lang.reflect.Array;
import java.util.Arrays;

public enum Wrapper
{
    BOOLEAN((Class<?>)Boolean.class, (Class<?>)Boolean.TYPE, 'Z', (Object)false, (Object)new boolean[0], Format.unsigned(1)), 
    BYTE((Class<?>)Byte.class, (Class<?>)Byte.TYPE, 'B', (Object)0, (Object)new byte[0], Format.signed(8)), 
    SHORT((Class<?>)Short.class, (Class<?>)Short.TYPE, 'S', (Object)0, (Object)new short[0], Format.signed(16)), 
    CHAR((Class<?>)Character.class, (Class<?>)Character.TYPE, 'C', (Object)'\0', (Object)new char[0], Format.unsigned(16)), 
    INT((Class<?>)Integer.class, (Class<?>)Integer.TYPE, 'I', (Object)0, (Object)new int[0], Format.signed(32)), 
    LONG((Class<?>)Long.class, (Class<?>)Long.TYPE, 'J', (Object)0L, (Object)new long[0], Format.signed(64)), 
    FLOAT((Class<?>)Float.class, (Class<?>)Float.TYPE, 'F', (Object)0.0f, (Object)new float[0], Format.floating(32)), 
    DOUBLE((Class<?>)Double.class, (Class<?>)Double.TYPE, 'D', (Object)0.0, (Object)new double[0], Format.floating(64)), 
    OBJECT((Class<?>)Object.class, (Class<?>)Object.class, 'L', (Object)null, (Object)new Object[0], Format.other(1)), 
    VOID((Class<?>)Void.class, (Class<?>)Void.TYPE, 'V', (Object)null, (Object)null, Format.other(0));
    
    private final Class<?> wrapperType;
    private final Class<?> primitiveType;
    private final char basicTypeChar;
    private final Object zero;
    private final Object emptyArray;
    private final int format;
    private final String wrapperSimpleName;
    private final String primitiveSimpleName;
    private static final Wrapper[] FROM_PRIM;
    private static final Wrapper[] FROM_WRAP;
    private static final Wrapper[] FROM_CHAR;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    private Wrapper(final Class<?> wrapperType, final Class<?> primitiveType, final char basicTypeChar, final Object zero, final Object emptyArray, final int format) {
        this.wrapperType = wrapperType;
        this.primitiveType = primitiveType;
        this.basicTypeChar = basicTypeChar;
        this.zero = zero;
        this.emptyArray = emptyArray;
        this.format = format;
        this.wrapperSimpleName = wrapperType.getSimpleName();
        this.primitiveSimpleName = primitiveType.getSimpleName();
    }
    
    public String detailString() {
        return this.wrapperSimpleName + Arrays.asList(this.wrapperType, this.primitiveType, this.basicTypeChar, this.zero, "0x" + Integer.toHexString(this.format));
    }
    
    public int bitWidth() {
        return this.format >> 2 & 0x3FF;
    }
    
    public int stackSlots() {
        return this.format >> 0 & 0x3;
    }
    
    public boolean isSingleWord() {
        return (this.format & 0x1) != 0x0;
    }
    
    public boolean isDoubleWord() {
        return (this.format & 0x2) != 0x0;
    }
    
    public boolean isNumeric() {
        return (this.format & 0xFFFFFFFC) != 0x0;
    }
    
    public boolean isIntegral() {
        return this.isNumeric() && this.format < 4225;
    }
    
    public boolean isSubwordOrInt() {
        return this.isIntegral() && this.isSingleWord();
    }
    
    public boolean isSigned() {
        return this.format < 0;
    }
    
    public boolean isUnsigned() {
        return this.format >= 5 && this.format < 4225;
    }
    
    public boolean isFloating() {
        return this.format >= 4225;
    }
    
    public boolean isOther() {
        return (this.format & 0xFFFFFFFC) == 0x0;
    }
    
    public boolean isConvertibleFrom(final Wrapper wrapper) {
        if (this == wrapper) {
            return true;
        }
        if (this.compareTo(wrapper) < 0) {
            return false;
        }
        if ((this.format & wrapper.format & 0xFFFFF000) == 0x0) {
            return this.isOther() || wrapper.format == 65;
        }
        assert this.isFloating() || this.isSigned();
        assert wrapper.isFloating() || wrapper.isSigned();
        return true;
    }
    
    private static boolean checkConvertibleFrom() {
        for (final Wrapper wrapper : values()) {
            assert wrapper.isConvertibleFrom(wrapper);
            assert Wrapper.VOID.isConvertibleFrom(wrapper);
            if (wrapper != Wrapper.VOID) {
                assert Wrapper.OBJECT.isConvertibleFrom(wrapper);
                assert !wrapper.isConvertibleFrom(Wrapper.VOID);
            }
            if (wrapper != Wrapper.CHAR) {
                assert !Wrapper.CHAR.isConvertibleFrom(wrapper);
                if (!wrapper.isConvertibleFrom(Wrapper.INT) && !Wrapper.$assertionsDisabled && wrapper.isConvertibleFrom(Wrapper.CHAR)) {
                    throw new AssertionError();
                }
            }
            if (wrapper != Wrapper.BOOLEAN) {
                assert !Wrapper.BOOLEAN.isConvertibleFrom(wrapper);
                if (wrapper != Wrapper.VOID && wrapper != Wrapper.OBJECT && !Wrapper.$assertionsDisabled && wrapper.isConvertibleFrom(Wrapper.BOOLEAN)) {
                    throw new AssertionError();
                }
            }
            if (wrapper.isSigned()) {
                for (final Wrapper wrapper2 : values()) {
                    if (wrapper != wrapper2) {
                        if (wrapper2.isFloating()) {
                            assert !wrapper.isConvertibleFrom(wrapper2);
                        }
                        else if (wrapper2.isSigned()) {
                            if (wrapper.compareTo(wrapper2) < 0) {
                                assert !wrapper.isConvertibleFrom(wrapper2);
                            }
                            else {
                                assert wrapper.isConvertibleFrom(wrapper2);
                            }
                        }
                    }
                }
            }
            if (wrapper.isFloating()) {
                for (final Wrapper wrapper3 : values()) {
                    if (wrapper != wrapper3) {
                        if (wrapper3.isSigned()) {
                            assert wrapper.isConvertibleFrom(wrapper3);
                        }
                        else if (wrapper3.isFloating()) {
                            if (wrapper.compareTo(wrapper3) < 0) {
                                assert !wrapper.isConvertibleFrom(wrapper3);
                            }
                            else {
                                assert wrapper.isConvertibleFrom(wrapper3);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public Object zero() {
        return this.zero;
    }
    
    public <T> T zero(final Class<T> clazz) {
        return this.convert(this.zero, clazz);
    }
    
    public static Wrapper forPrimitiveType(final Class<?> clazz) {
        final Wrapper primitiveType = findPrimitiveType(clazz);
        if (primitiveType != null) {
            return primitiveType;
        }
        if (clazz.isPrimitive()) {
            throw new InternalError();
        }
        throw newIllegalArgumentException("not primitive: " + clazz);
    }
    
    static Wrapper findPrimitiveType(final Class<?> clazz) {
        final Wrapper wrapper = Wrapper.FROM_PRIM[hashPrim(clazz)];
        if (wrapper != null && wrapper.primitiveType == clazz) {
            return wrapper;
        }
        return null;
    }
    
    public static Wrapper forWrapperType(final Class<?> clazz) {
        final Wrapper wrapperType = findWrapperType(clazz);
        if (wrapperType != null) {
            return wrapperType;
        }
        final Wrapper[] values = values();
        for (int length = values.length, i = 0; i < length; ++i) {
            if (values[i].wrapperType == clazz) {
                throw new InternalError();
            }
        }
        throw newIllegalArgumentException("not wrapper: " + clazz);
    }
    
    static Wrapper findWrapperType(final Class<?> clazz) {
        final Wrapper wrapper = Wrapper.FROM_WRAP[hashWrap(clazz)];
        if (wrapper != null && wrapper.wrapperType == clazz) {
            return wrapper;
        }
        return null;
    }
    
    public static Wrapper forBasicType(final char c) {
        final Wrapper wrapper = Wrapper.FROM_CHAR[hashChar(c)];
        if (wrapper != null && wrapper.basicTypeChar == c) {
            return wrapper;
        }
        for (final Wrapper wrapper2 : values()) {
            if (wrapper.basicTypeChar == c) {
                throw new InternalError();
            }
        }
        throw newIllegalArgumentException("not basic type char: " + c);
    }
    
    public static Wrapper forBasicType(final Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return forPrimitiveType(clazz);
        }
        return Wrapper.OBJECT;
    }
    
    private static int hashPrim(final Class<?> clazz) {
        final String name = clazz.getName();
        if (name.length() < 3) {
            return 0;
        }
        return (name.charAt(0) + name.charAt(2)) % 16;
    }
    
    private static int hashWrap(final Class<?> clazz) {
        final String name = clazz.getName();
        assert 10 == "java.lang.".length();
        if (name.length() < 13) {
            return 0;
        }
        return ('\u0003' * name.charAt(11) + name.charAt(12)) % 16;
    }
    
    private static int hashChar(final char c) {
        return (c + (c >> 1)) % 16;
    }
    
    public Class<?> primitiveType() {
        return this.primitiveType;
    }
    
    public Class<?> wrapperType() {
        return this.wrapperType;
    }
    
    public <T> Class<T> wrapperType(final Class<T> clazz) {
        if (clazz == this.wrapperType) {
            return clazz;
        }
        if (clazz == this.primitiveType || this.wrapperType == Object.class || clazz.isInterface()) {
            return forceType(this.wrapperType, clazz);
        }
        throw newClassCastException(clazz, this.primitiveType);
    }
    
    private static ClassCastException newClassCastException(final Class<?> clazz, final Class<?> clazz2) {
        return new ClassCastException(clazz + " is not compatible with " + clazz2);
    }
    
    public static <T> Class<T> asWrapperType(final Class<T> clazz) {
        if (clazz.isPrimitive()) {
            return (Class<T>)forPrimitiveType(clazz).wrapperType((Class<Object>)clazz);
        }
        return clazz;
    }
    
    public static <T> Class<T> asPrimitiveType(final Class<T> clazz) {
        final Wrapper wrapperType = findWrapperType(clazz);
        if (wrapperType != null) {
            return forceType(wrapperType.primitiveType(), clazz);
        }
        return clazz;
    }
    
    public static boolean isWrapperType(final Class<?> clazz) {
        return findWrapperType(clazz) != null;
    }
    
    public static boolean isPrimitiveType(final Class<?> clazz) {
        return clazz.isPrimitive();
    }
    
    public static char basicTypeChar(final Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            return 'L';
        }
        return forPrimitiveType(clazz).basicTypeChar();
    }
    
    public char basicTypeChar() {
        return this.basicTypeChar;
    }
    
    public String wrapperSimpleName() {
        return this.wrapperSimpleName;
    }
    
    public String primitiveSimpleName() {
        return this.primitiveSimpleName;
    }
    
    public <T> T cast(final Object o, final Class<T> clazz) {
        return this.convert(o, clazz, true);
    }
    
    public <T> T convert(final Object o, final Class<T> clazz) {
        return this.convert(o, clazz, false);
    }
    
    private <T> T convert(final Object o, final Class<T> clazz, final boolean b) {
        if (this == Wrapper.OBJECT) {
            assert !clazz.isPrimitive();
            if (!clazz.isInterface()) {
                clazz.cast(o);
            }
            return (T)o;
        }
        else {
            final Class<T> wrapperType = this.wrapperType(clazz);
            if (wrapperType.isInstance(o)) {
                return wrapperType.cast(o);
            }
            if (!b) {
                final Class<?> class1 = o.getClass();
                final Wrapper wrapperType2 = findWrapperType(class1);
                if (wrapperType2 == null || !this.isConvertibleFrom(wrapperType2)) {
                    throw newClassCastException(wrapperType, class1);
                }
            }
            else if (o == null) {
                return (T)this.zero;
            }
            final Object wrap = this.wrap(o);
            assert ((wrap == null) ? Void.class : wrap.getClass()) == wrapperType;
            return (T)wrap;
        }
    }
    
    static <T> Class<T> forceType(final Class<?> clazz, final Class<T> clazz2) {
        if (clazz != clazz2 && (!clazz.isPrimitive() || forPrimitiveType(clazz) != findWrapperType(clazz2)) && (!clazz2.isPrimitive() || forPrimitiveType(clazz2) != findWrapperType(clazz)) && (clazz != Object.class || clazz2.isPrimitive())) {
            System.out.println(clazz + " <= " + clazz2);
        }
        assert clazz == Object.class && !clazz2.isPrimitive();
        return (Class<T>)clazz;
    }
    
    public Object wrap(final Object o) {
        switch (this.basicTypeChar) {
            case 'L': {
                return o;
            }
            case 'V': {
                return null;
            }
            default: {
                final Number numberValue = numberValue(o);
                switch (this.basicTypeChar) {
                    case 'I': {
                        return numberValue.intValue();
                    }
                    case 'J': {
                        return numberValue.longValue();
                    }
                    case 'F': {
                        return numberValue.floatValue();
                    }
                    case 'D': {
                        return numberValue.doubleValue();
                    }
                    case 'S': {
                        return (short)numberValue.intValue();
                    }
                    case 'B': {
                        return (byte)numberValue.intValue();
                    }
                    case 'C': {
                        return (char)numberValue.intValue();
                    }
                    case 'Z': {
                        return boolValue(numberValue.byteValue());
                    }
                    default: {
                        throw new InternalError("bad wrapper");
                    }
                }
                break;
            }
        }
    }
    
    public Object wrap(final int n) {
        if (this.basicTypeChar == 'L') {
            return n;
        }
        switch (this.basicTypeChar) {
            case 'L': {
                throw newIllegalArgumentException("cannot wrap to object type");
            }
            case 'V': {
                return null;
            }
            case 'I': {
                return n;
            }
            case 'J': {
                return n;
            }
            case 'F': {
                return n;
            }
            case 'D': {
                return n;
            }
            case 'S': {
                return (short)n;
            }
            case 'B': {
                return (byte)n;
            }
            case 'C': {
                return (char)n;
            }
            case 'Z': {
                return boolValue((byte)n);
            }
            default: {
                throw new InternalError("bad wrapper");
            }
        }
    }
    
    private static Number numberValue(final Object o) {
        if (o instanceof Number) {
            return (Number)o;
        }
        if (o instanceof Character) {
            return (int)(char)o;
        }
        if (o instanceof Boolean) {
            return ((boolean)o) ? 1 : 0;
        }
        return (Number)o;
    }
    
    private static boolean boolValue(final byte b) {
        return (byte)(b & 0x1) != 0;
    }
    
    private static RuntimeException newIllegalArgumentException(final String s, final Object o) {
        return newIllegalArgumentException(s + o);
    }
    
    private static RuntimeException newIllegalArgumentException(final String s) {
        return new IllegalArgumentException(s);
    }
    
    public Object makeArray(final int n) {
        return Array.newInstance(this.primitiveType, n);
    }
    
    public Class<?> arrayType() {
        return this.emptyArray.getClass();
    }
    
    public void copyArrayUnboxing(final Object[] array, final int n, final Object o, final int n2, final int n3) {
        if (o.getClass() != this.arrayType()) {
            this.arrayType().cast(o);
        }
        for (int i = 0; i < n3; ++i) {
            Array.set(o, i + n2, this.convert(array[i + n], this.primitiveType));
        }
    }
    
    public void copyArrayBoxing(final Object o, final int n, final Object[] array, final int n2, final int n3) {
        if (o.getClass() != this.arrayType()) {
            this.arrayType().cast(o);
        }
        for (int i = 0; i < n3; ++i) {
            final Object value = Array.get(o, i + n);
            assert value.getClass() == this.wrapperType;
            array[i + n2] = value;
        }
    }
    
    static {
        assert checkConvertibleFrom();
        FROM_PRIM = new Wrapper[16];
        FROM_WRAP = new Wrapper[16];
        FROM_CHAR = new Wrapper[16];
        for (final Wrapper wrapper : values()) {
            final int hashPrim = hashPrim(wrapper.primitiveType);
            final int hashWrap = hashWrap(wrapper.wrapperType);
            final int hashChar = hashChar(wrapper.basicTypeChar);
            assert Wrapper.FROM_PRIM[hashPrim] == null;
            assert Wrapper.FROM_WRAP[hashWrap] == null;
            assert Wrapper.FROM_CHAR[hashChar] == null;
            Wrapper.FROM_PRIM[hashPrim] = wrapper;
            Wrapper.FROM_WRAP[hashWrap] = wrapper;
            Wrapper.FROM_CHAR[hashChar] = wrapper;
        }
    }
    
    private abstract static class Format
    {
        static final int SLOT_SHIFT = 0;
        static final int SIZE_SHIFT = 2;
        static final int KIND_SHIFT = 12;
        static final int SIGNED = -4096;
        static final int UNSIGNED = 0;
        static final int FLOATING = 4096;
        static final int SLOT_MASK = 3;
        static final int SIZE_MASK = 1023;
        static final int INT = -3967;
        static final int SHORT = -4031;
        static final int BOOLEAN = 5;
        static final int CHAR = 65;
        static final int FLOAT = 4225;
        static final int VOID = 0;
        static final int NUM_MASK = -4;
        static final /* synthetic */ boolean $assertionsDisabled;
        
        static int format(final int n, final int n2, final int n3) {
            assert n >> 12 << 12 == n;
            assert (n2 & n2 - 1) == 0x0;
            Label_0108: {
                if (!Format.$assertionsDisabled) {
                    if (n == -4096) {
                        if (n2 > 0) {
                            break Label_0108;
                        }
                    }
                    else if (n == 0) {
                        if (n2 > 0) {
                            break Label_0108;
                        }
                    }
                    else if (n == 4096) {
                        if (n2 == 32) {
                            break Label_0108;
                        }
                        if (n2 == 64) {
                            break Label_0108;
                        }
                    }
                    throw new AssertionError();
                }
            }
            if (!Format.$assertionsDisabled) {
                if (n3 == 2) {
                    if (n2 == 64) {
                        return n | n2 << 2 | n3 << 0;
                    }
                }
                else if (n3 == 1 && n2 <= 32) {
                    return n | n2 << 2 | n3 << 0;
                }
                throw new AssertionError();
            }
            return n | n2 << 2 | n3 << 0;
        }
        
        static int signed(final int n) {
            return format(-4096, n, (n > 32) ? 2 : 1);
        }
        
        static int unsigned(final int n) {
            return format(0, n, (n > 32) ? 2 : 1);
        }
        
        static int floating(final int n) {
            return format(4096, n, (n > 32) ? 2 : 1);
        }
        
        static int other(final int n) {
            return n << 0;
        }
    }
}
