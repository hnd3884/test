package sun.invoke.util;

import java.util.EnumMap;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class ValueConversions
{
    private static final Class<?> THIS_CLASS;
    private static final MethodHandles.Lookup IMPL_LOOKUP;
    private static final WrapperCache[] UNBOX_CONVERSIONS;
    private static final Integer ZERO_INT;
    private static final Integer ONE_INT;
    private static final WrapperCache[] BOX_CONVERSIONS;
    private static final WrapperCache[] CONSTANT_FUNCTIONS;
    private static final MethodHandle CAST_REFERENCE;
    private static final MethodHandle IGNORE;
    private static final MethodHandle EMPTY;
    private static final WrapperCache[] CONVERT_PRIMITIVE_FUNCTIONS;
    
    private static WrapperCache[] newWrapperCaches(final int n) {
        final WrapperCache[] array = new WrapperCache[n];
        for (int i = 0; i < n; ++i) {
            array[i] = new WrapperCache();
        }
        return array;
    }
    
    static int unboxInteger(final Integer n) {
        return n;
    }
    
    static int unboxInteger(final Object o, final boolean b) {
        if (o instanceof Integer) {
            return (int)o;
        }
        return primitiveConversion(Wrapper.INT, o, b).intValue();
    }
    
    static byte unboxByte(final Byte b) {
        return b;
    }
    
    static byte unboxByte(final Object o, final boolean b) {
        if (o instanceof Byte) {
            return (byte)o;
        }
        return primitiveConversion(Wrapper.BYTE, o, b).byteValue();
    }
    
    static short unboxShort(final Short n) {
        return n;
    }
    
    static short unboxShort(final Object o, final boolean b) {
        if (o instanceof Short) {
            return (short)o;
        }
        return primitiveConversion(Wrapper.SHORT, o, b).shortValue();
    }
    
    static boolean unboxBoolean(final Boolean b) {
        return b;
    }
    
    static boolean unboxBoolean(final Object o, final boolean b) {
        if (o instanceof Boolean) {
            return (boolean)o;
        }
        return (primitiveConversion(Wrapper.BOOLEAN, o, b).intValue() & 0x1) != 0x0;
    }
    
    static char unboxCharacter(final Character c) {
        return c;
    }
    
    static char unboxCharacter(final Object o, final boolean b) {
        if (o instanceof Character) {
            return (char)o;
        }
        return (char)primitiveConversion(Wrapper.CHAR, o, b).intValue();
    }
    
    static long unboxLong(final Long n) {
        return n;
    }
    
    static long unboxLong(final Object o, final boolean b) {
        if (o instanceof Long) {
            return (long)o;
        }
        return primitiveConversion(Wrapper.LONG, o, b).longValue();
    }
    
    static float unboxFloat(final Float n) {
        return n;
    }
    
    static float unboxFloat(final Object o, final boolean b) {
        if (o instanceof Float) {
            return (float)o;
        }
        return primitiveConversion(Wrapper.FLOAT, o, b).floatValue();
    }
    
    static double unboxDouble(final Double n) {
        return n;
    }
    
    static double unboxDouble(final Object o, final boolean b) {
        if (o instanceof Double) {
            return (double)o;
        }
        return primitiveConversion(Wrapper.DOUBLE, o, b).doubleValue();
    }
    
    private static MethodType unboxType(final Wrapper wrapper, final int n) {
        if (n == 0) {
            return MethodType.methodType(wrapper.primitiveType(), wrapper.wrapperType());
        }
        return MethodType.methodType(wrapper.primitiveType(), Object.class, Boolean.TYPE);
    }
    
    private static MethodHandle unbox(final Wrapper wrapper, final int n) {
        final WrapperCache wrapperCache = ValueConversions.UNBOX_CONVERSIONS[n];
        final MethodHandle value = wrapperCache.get(wrapper);
        if (value != null) {
            return value;
        }
        switch (wrapper) {
            case OBJECT:
            case VOID: {
                throw new IllegalArgumentException("unbox " + wrapper);
            }
            default: {
                final String string = "unbox" + wrapper.wrapperSimpleName();
                final MethodType unboxType = unboxType(wrapper, n);
                MethodHandle methodHandle;
                try {
                    methodHandle = ValueConversions.IMPL_LOOKUP.findStatic(ValueConversions.THIS_CLASS, string, unboxType);
                }
                catch (final ReflectiveOperationException ex) {
                    methodHandle = null;
                }
                if (methodHandle != null) {
                    if (n > 0) {
                        methodHandle = MethodHandles.insertArguments(methodHandle, 1, n != 2);
                    }
                    if (n == 1) {
                        methodHandle = methodHandle.asType(unboxType(wrapper, 0));
                    }
                    return wrapperCache.put(wrapper, methodHandle);
                }
                throw new IllegalArgumentException("cannot find unbox adapter for " + wrapper + ((n <= 1) ? " (exact)" : ((n == 3) ? " (cast)" : "")));
            }
        }
    }
    
    public static MethodHandle unboxExact(final Wrapper wrapper) {
        return unbox(wrapper, 0);
    }
    
    public static MethodHandle unboxExact(final Wrapper wrapper, final boolean b) {
        return unbox(wrapper, b ? 0 : 1);
    }
    
    public static MethodHandle unboxWiden(final Wrapper wrapper) {
        return unbox(wrapper, 2);
    }
    
    public static MethodHandle unboxCast(final Wrapper wrapper) {
        return unbox(wrapper, 3);
    }
    
    public static Number primitiveConversion(final Wrapper wrapper, final Object o, final boolean b) {
        if (o == null) {
            if (!b) {
                return null;
            }
            return ValueConversions.ZERO_INT;
        }
        else {
            Number value;
            if (o instanceof Number) {
                value = (Number)o;
            }
            else if (o instanceof Boolean) {
                value = (o ? ValueConversions.ONE_INT : ValueConversions.ZERO_INT);
            }
            else if (o instanceof Character) {
                value = (int)(char)o;
            }
            else {
                value = (Number)o;
            }
            final Wrapper wrapperType = Wrapper.findWrapperType(o.getClass());
            if (wrapperType == null || (!b && !wrapper.isConvertibleFrom(wrapperType))) {
                return (Number)wrapper.wrapperType().cast(o);
            }
            return value;
        }
    }
    
    public static int widenSubword(final Object o) {
        if (o instanceof Integer) {
            return (int)o;
        }
        if (o instanceof Boolean) {
            return fromBoolean((boolean)o);
        }
        if (o instanceof Character) {
            return (char)o;
        }
        if (o instanceof Short) {
            return (short)o;
        }
        if (o instanceof Byte) {
            return (byte)o;
        }
        return (int)o;
    }
    
    static Integer boxInteger(final int n) {
        return n;
    }
    
    static Byte boxByte(final byte b) {
        return b;
    }
    
    static Short boxShort(final short n) {
        return n;
    }
    
    static Boolean boxBoolean(final boolean b) {
        return b;
    }
    
    static Character boxCharacter(final char c) {
        return c;
    }
    
    static Long boxLong(final long n) {
        return n;
    }
    
    static Float boxFloat(final float n) {
        return n;
    }
    
    static Double boxDouble(final double n) {
        return n;
    }
    
    private static MethodType boxType(final Wrapper wrapper) {
        return MethodType.methodType(wrapper.wrapperType(), wrapper.primitiveType());
    }
    
    public static MethodHandle boxExact(final Wrapper wrapper) {
        final WrapperCache wrapperCache = ValueConversions.BOX_CONVERSIONS[0];
        final MethodHandle value = wrapperCache.get(wrapper);
        if (value != null) {
            return value;
        }
        final String string = "box" + wrapper.wrapperSimpleName();
        final MethodType boxType = boxType(wrapper);
        MethodHandle static1;
        try {
            static1 = ValueConversions.IMPL_LOOKUP.findStatic(ValueConversions.THIS_CLASS, string, boxType);
        }
        catch (final ReflectiveOperationException ex) {
            static1 = null;
        }
        if (static1 != null) {
            return wrapperCache.put(wrapper, static1);
        }
        throw new IllegalArgumentException("cannot find box adapter for " + wrapper);
    }
    
    static void ignore(final Object o) {
    }
    
    static void empty() {
    }
    
    static Object zeroObject() {
        return null;
    }
    
    static int zeroInteger() {
        return 0;
    }
    
    static long zeroLong() {
        return 0L;
    }
    
    static float zeroFloat() {
        return 0.0f;
    }
    
    static double zeroDouble() {
        return 0.0;
    }
    
    public static MethodHandle zeroConstantFunction(final Wrapper wrapper) {
        final WrapperCache wrapperCache = ValueConversions.CONSTANT_FUNCTIONS[0];
        MethodHandle methodHandle = wrapperCache.get(wrapper);
        if (methodHandle != null) {
            return methodHandle;
        }
        final MethodType methodType = MethodType.methodType(wrapper.primitiveType());
        switch (wrapper) {
            case VOID: {
                methodHandle = ValueConversions.EMPTY;
                break;
            }
            case OBJECT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE: {
                try {
                    methodHandle = ValueConversions.IMPL_LOOKUP.findStatic(ValueConversions.THIS_CLASS, "zero" + wrapper.wrapperSimpleName(), methodType);
                }
                catch (final ReflectiveOperationException ex) {
                    methodHandle = null;
                }
                break;
            }
        }
        if (methodHandle != null) {
            return wrapperCache.put(wrapper, methodHandle);
        }
        if (wrapper.isSubwordOrInt() && wrapper != Wrapper.INT) {
            return wrapperCache.put(wrapper, MethodHandles.explicitCastArguments(zeroConstantFunction(Wrapper.INT), methodType));
        }
        throw new IllegalArgumentException("cannot find zero constant for " + wrapper);
    }
    
    public static MethodHandle ignore() {
        return ValueConversions.IGNORE;
    }
    
    public static MethodHandle cast() {
        return ValueConversions.CAST_REFERENCE;
    }
    
    static float doubleToFloat(final double n) {
        return (float)n;
    }
    
    static long doubleToLong(final double n) {
        return (long)n;
    }
    
    static int doubleToInt(final double n) {
        return (int)n;
    }
    
    static short doubleToShort(final double n) {
        return (short)n;
    }
    
    static char doubleToChar(final double n) {
        return (char)n;
    }
    
    static byte doubleToByte(final double n) {
        return (byte)n;
    }
    
    static boolean doubleToBoolean(final double n) {
        return toBoolean((byte)n);
    }
    
    static double floatToDouble(final float n) {
        return n;
    }
    
    static long floatToLong(final float n) {
        return (long)n;
    }
    
    static int floatToInt(final float n) {
        return (int)n;
    }
    
    static short floatToShort(final float n) {
        return (short)n;
    }
    
    static char floatToChar(final float n) {
        return (char)n;
    }
    
    static byte floatToByte(final float n) {
        return (byte)n;
    }
    
    static boolean floatToBoolean(final float n) {
        return toBoolean((byte)n);
    }
    
    static double longToDouble(final long n) {
        return (double)n;
    }
    
    static float longToFloat(final long n) {
        return (float)n;
    }
    
    static int longToInt(final long n) {
        return (int)n;
    }
    
    static short longToShort(final long n) {
        return (short)n;
    }
    
    static char longToChar(final long n) {
        return (char)n;
    }
    
    static byte longToByte(final long n) {
        return (byte)n;
    }
    
    static boolean longToBoolean(final long n) {
        return toBoolean((byte)n);
    }
    
    static double intToDouble(final int n) {
        return n;
    }
    
    static float intToFloat(final int n) {
        return (float)n;
    }
    
    static long intToLong(final int n) {
        return n;
    }
    
    static short intToShort(final int n) {
        return (short)n;
    }
    
    static char intToChar(final int n) {
        return (char)n;
    }
    
    static byte intToByte(final int n) {
        return (byte)n;
    }
    
    static boolean intToBoolean(final int n) {
        return toBoolean((byte)n);
    }
    
    static double shortToDouble(final short n) {
        return n;
    }
    
    static float shortToFloat(final short n) {
        return n;
    }
    
    static long shortToLong(final short n) {
        return n;
    }
    
    static int shortToInt(final short n) {
        return n;
    }
    
    static char shortToChar(final short n) {
        return (char)n;
    }
    
    static byte shortToByte(final short n) {
        return (byte)n;
    }
    
    static boolean shortToBoolean(final short n) {
        return toBoolean((byte)n);
    }
    
    static double charToDouble(final char c) {
        return c;
    }
    
    static float charToFloat(final char c) {
        return c;
    }
    
    static long charToLong(final char c) {
        return c;
    }
    
    static int charToInt(final char c) {
        return c;
    }
    
    static short charToShort(final char c) {
        return (short)c;
    }
    
    static byte charToByte(final char c) {
        return (byte)c;
    }
    
    static boolean charToBoolean(final char c) {
        return toBoolean((byte)c);
    }
    
    static double byteToDouble(final byte b) {
        return b;
    }
    
    static float byteToFloat(final byte b) {
        return b;
    }
    
    static long byteToLong(final byte b) {
        return b;
    }
    
    static int byteToInt(final byte b) {
        return b;
    }
    
    static short byteToShort(final byte b) {
        return b;
    }
    
    static char byteToChar(final byte b) {
        return (char)b;
    }
    
    static boolean byteToBoolean(final byte b) {
        return toBoolean(b);
    }
    
    static double booleanToDouble(final boolean b) {
        return fromBoolean(b);
    }
    
    static float booleanToFloat(final boolean b) {
        return fromBoolean(b);
    }
    
    static long booleanToLong(final boolean b) {
        return fromBoolean(b);
    }
    
    static int booleanToInt(final boolean b) {
        return fromBoolean(b);
    }
    
    static short booleanToShort(final boolean b) {
        return fromBoolean(b);
    }
    
    static char booleanToChar(final boolean b) {
        return (char)fromBoolean(b);
    }
    
    static byte booleanToByte(final boolean b) {
        return fromBoolean(b);
    }
    
    static boolean toBoolean(final byte b) {
        return (b & 0x1) != 0x0;
    }
    
    static byte fromBoolean(final boolean b) {
        return (byte)(b ? 1 : 0);
    }
    
    public static MethodHandle convertPrimitive(final Wrapper wrapper, final Wrapper wrapper2) {
        final WrapperCache wrapperCache = ValueConversions.CONVERT_PRIMITIVE_FUNCTIONS[wrapper.ordinal()];
        final MethodHandle value = wrapperCache.get(wrapper2);
        if (value != null) {
            return value;
        }
        final Class<?> primitiveType = wrapper.primitiveType();
        final Class<?> primitiveType2 = wrapper2.primitiveType();
        final MethodType methodType = MethodType.methodType(primitiveType2, primitiveType);
        MethodHandle methodHandle;
        if (wrapper == wrapper2) {
            methodHandle = MethodHandles.identity(primitiveType);
        }
        else {
            assert primitiveType.isPrimitive() && primitiveType2.isPrimitive();
            try {
                methodHandle = ValueConversions.IMPL_LOOKUP.findStatic(ValueConversions.THIS_CLASS, primitiveType.getSimpleName() + "To" + capitalize(primitiveType2.getSimpleName()), methodType);
            }
            catch (final ReflectiveOperationException ex) {
                methodHandle = null;
            }
        }
        if (methodHandle == null) {
            throw new IllegalArgumentException("cannot find primitive conversion function for " + primitiveType.getSimpleName() + " -> " + primitiveType2.getSimpleName());
        }
        assert methodHandle.type() == methodType : methodHandle;
        return wrapperCache.put(wrapper2, methodHandle);
    }
    
    public static MethodHandle convertPrimitive(final Class<?> clazz, final Class<?> clazz2) {
        return convertPrimitive(Wrapper.forPrimitiveType(clazz), Wrapper.forPrimitiveType(clazz2));
    }
    
    private static String capitalize(final String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    private static InternalError newInternalError(final String s, final Throwable t) {
        return new InternalError(s, t);
    }
    
    private static InternalError newInternalError(final Throwable t) {
        return new InternalError(t);
    }
    
    static {
        THIS_CLASS = ValueConversions.class;
        IMPL_LOOKUP = MethodHandles.lookup();
        UNBOX_CONVERSIONS = newWrapperCaches(4);
        ZERO_INT = 0;
        ONE_INT = 1;
        BOX_CONVERSIONS = newWrapperCaches(1);
        CONSTANT_FUNCTIONS = newWrapperCaches(2);
        try {
            final MethodType genericMethodType = MethodType.genericMethodType(1);
            final MethodType changeReturnType = genericMethodType.changeReturnType(Void.TYPE);
            CAST_REFERENCE = ValueConversions.IMPL_LOOKUP.findVirtual(Class.class, "cast", genericMethodType);
            IGNORE = ValueConversions.IMPL_LOOKUP.findStatic(ValueConversions.THIS_CLASS, "ignore", changeReturnType);
            EMPTY = ValueConversions.IMPL_LOOKUP.findStatic(ValueConversions.THIS_CLASS, "empty", changeReturnType.dropParameterTypes(0, 1));
        }
        catch (final NoSuchMethodException | IllegalAccessException ex) {
            throw newInternalError("uncaught exception", (Throwable)ex);
        }
        CONVERT_PRIMITIVE_FUNCTIONS = newWrapperCaches(Wrapper.values().length);
    }
    
    private static class WrapperCache
    {
        private final EnumMap<Wrapper, MethodHandle> map;
        
        private WrapperCache() {
            this.map = new EnumMap<Wrapper, MethodHandle>(Wrapper.class);
        }
        
        public MethodHandle get(final Wrapper wrapper) {
            return this.map.get(wrapper);
        }
        
        public synchronized MethodHandle put(final Wrapper wrapper, final MethodHandle methodHandle) {
            final MethodHandle methodHandle2 = this.map.putIfAbsent(wrapper, methodHandle);
            if (methodHandle2 != null) {
                return methodHandle2;
            }
            return methodHandle;
        }
    }
}
