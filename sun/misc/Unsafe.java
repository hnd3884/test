package sun.misc;

import java.security.ProtectionDomain;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public final class Unsafe
{
    private static final Unsafe theUnsafe;
    public static final int INVALID_FIELD_OFFSET = -1;
    public static final int ARRAY_BOOLEAN_BASE_OFFSET;
    public static final int ARRAY_BYTE_BASE_OFFSET;
    public static final int ARRAY_SHORT_BASE_OFFSET;
    public static final int ARRAY_CHAR_BASE_OFFSET;
    public static final int ARRAY_INT_BASE_OFFSET;
    public static final int ARRAY_LONG_BASE_OFFSET;
    public static final int ARRAY_FLOAT_BASE_OFFSET;
    public static final int ARRAY_DOUBLE_BASE_OFFSET;
    public static final int ARRAY_OBJECT_BASE_OFFSET;
    public static final int ARRAY_BOOLEAN_INDEX_SCALE;
    public static final int ARRAY_BYTE_INDEX_SCALE;
    public static final int ARRAY_SHORT_INDEX_SCALE;
    public static final int ARRAY_CHAR_INDEX_SCALE;
    public static final int ARRAY_INT_INDEX_SCALE;
    public static final int ARRAY_LONG_INDEX_SCALE;
    public static final int ARRAY_FLOAT_INDEX_SCALE;
    public static final int ARRAY_DOUBLE_INDEX_SCALE;
    public static final int ARRAY_OBJECT_INDEX_SCALE;
    public static final int ADDRESS_SIZE;
    
    private static native void registerNatives();
    
    private Unsafe() {
    }
    
    @CallerSensitive
    public static Unsafe getUnsafe() {
        if (!VM.isSystemDomainLoader(Reflection.getCallerClass().getClassLoader())) {
            throw new SecurityException("Unsafe");
        }
        return Unsafe.theUnsafe;
    }
    
    public native int getInt(final Object p0, final long p1);
    
    public native void putInt(final Object p0, final long p1, final int p2);
    
    public native Object getObject(final Object p0, final long p1);
    
    public native void putObject(final Object p0, final long p1, final Object p2);
    
    public native boolean getBoolean(final Object p0, final long p1);
    
    public native void putBoolean(final Object p0, final long p1, final boolean p2);
    
    public native byte getByte(final Object p0, final long p1);
    
    public native void putByte(final Object p0, final long p1, final byte p2);
    
    public native short getShort(final Object p0, final long p1);
    
    public native void putShort(final Object p0, final long p1, final short p2);
    
    public native char getChar(final Object p0, final long p1);
    
    public native void putChar(final Object p0, final long p1, final char p2);
    
    public native long getLong(final Object p0, final long p1);
    
    public native void putLong(final Object p0, final long p1, final long p2);
    
    public native float getFloat(final Object p0, final long p1);
    
    public native void putFloat(final Object p0, final long p1, final float p2);
    
    public native double getDouble(final Object p0, final long p1);
    
    public native void putDouble(final Object p0, final long p1, final double p2);
    
    @Deprecated
    public int getInt(final Object o, final int n) {
        return this.getInt(o, (long)n);
    }
    
    @Deprecated
    public void putInt(final Object o, final int n, final int n2) {
        this.putInt(o, (long)n, n2);
    }
    
    @Deprecated
    public Object getObject(final Object o, final int n) {
        return this.getObject(o, (long)n);
    }
    
    @Deprecated
    public void putObject(final Object o, final int n, final Object o2) {
        this.putObject(o, (long)n, o2);
    }
    
    @Deprecated
    public boolean getBoolean(final Object o, final int n) {
        return this.getBoolean(o, (long)n);
    }
    
    @Deprecated
    public void putBoolean(final Object o, final int n, final boolean b) {
        this.putBoolean(o, (long)n, b);
    }
    
    @Deprecated
    public byte getByte(final Object o, final int n) {
        return this.getByte(o, (long)n);
    }
    
    @Deprecated
    public void putByte(final Object o, final int n, final byte b) {
        this.putByte(o, (long)n, b);
    }
    
    @Deprecated
    public short getShort(final Object o, final int n) {
        return this.getShort(o, (long)n);
    }
    
    @Deprecated
    public void putShort(final Object o, final int n, final short n2) {
        this.putShort(o, (long)n, n2);
    }
    
    @Deprecated
    public char getChar(final Object o, final int n) {
        return this.getChar(o, (long)n);
    }
    
    @Deprecated
    public void putChar(final Object o, final int n, final char c) {
        this.putChar(o, (long)n, c);
    }
    
    @Deprecated
    public long getLong(final Object o, final int n) {
        return this.getLong(o, (long)n);
    }
    
    @Deprecated
    public void putLong(final Object o, final int n, final long n2) {
        this.putLong(o, (long)n, n2);
    }
    
    @Deprecated
    public float getFloat(final Object o, final int n) {
        return this.getFloat(o, (long)n);
    }
    
    @Deprecated
    public void putFloat(final Object o, final int n, final float n2) {
        this.putFloat(o, (long)n, n2);
    }
    
    @Deprecated
    public double getDouble(final Object o, final int n) {
        return this.getDouble(o, (long)n);
    }
    
    @Deprecated
    public void putDouble(final Object o, final int n, final double n2) {
        this.putDouble(o, (long)n, n2);
    }
    
    public native byte getByte(final long p0);
    
    public native void putByte(final long p0, final byte p1);
    
    public native short getShort(final long p0);
    
    public native void putShort(final long p0, final short p1);
    
    public native char getChar(final long p0);
    
    public native void putChar(final long p0, final char p1);
    
    public native int getInt(final long p0);
    
    public native void putInt(final long p0, final int p1);
    
    public native long getLong(final long p0);
    
    public native void putLong(final long p0, final long p1);
    
    public native float getFloat(final long p0);
    
    public native void putFloat(final long p0, final float p1);
    
    public native double getDouble(final long p0);
    
    public native void putDouble(final long p0, final double p1);
    
    public native long getAddress(final long p0);
    
    public native void putAddress(final long p0, final long p1);
    
    public native long allocateMemory(final long p0);
    
    public native long reallocateMemory(final long p0, final long p1);
    
    public native void setMemory(final Object p0, final long p1, final long p2, final byte p3);
    
    public void setMemory(final long n, final long n2, final byte b) {
        this.setMemory(null, n, n2, b);
    }
    
    public native void copyMemory(final Object p0, final long p1, final Object p2, final long p3, final long p4);
    
    public void copyMemory(final long n, final long n2, final long n3) {
        this.copyMemory(null, n, null, n2, n3);
    }
    
    public native void freeMemory(final long p0);
    
    @Deprecated
    public int fieldOffset(final Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            return (int)this.staticFieldOffset(field);
        }
        return (int)this.objectFieldOffset(field);
    }
    
    @Deprecated
    public Object staticFieldBase(final Class<?> clazz) {
        final Field[] declaredFields = clazz.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; ++i) {
            if (Modifier.isStatic(declaredFields[i].getModifiers())) {
                return this.staticFieldBase(declaredFields[i]);
            }
        }
        return null;
    }
    
    public native long staticFieldOffset(final Field p0);
    
    public native long objectFieldOffset(final Field p0);
    
    public native Object staticFieldBase(final Field p0);
    
    public native boolean shouldBeInitialized(final Class<?> p0);
    
    public native void ensureClassInitialized(final Class<?> p0);
    
    public native int arrayBaseOffset(final Class<?> p0);
    
    public native int arrayIndexScale(final Class<?> p0);
    
    public native int addressSize();
    
    public native int pageSize();
    
    public native Class<?> defineClass(final String p0, final byte[] p1, final int p2, final int p3, final ClassLoader p4, final ProtectionDomain p5);
    
    public native Class<?> defineAnonymousClass(final Class<?> p0, final byte[] p1, final Object[] p2);
    
    public native Object allocateInstance(final Class<?> p0) throws InstantiationException;
    
    @Deprecated
    public native void monitorEnter(final Object p0);
    
    @Deprecated
    public native void monitorExit(final Object p0);
    
    @Deprecated
    public native boolean tryMonitorEnter(final Object p0);
    
    public native void throwException(final Throwable p0);
    
    public final native boolean compareAndSwapObject(final Object p0, final long p1, final Object p2, final Object p3);
    
    public final native boolean compareAndSwapInt(final Object p0, final long p1, final int p2, final int p3);
    
    public final native boolean compareAndSwapLong(final Object p0, final long p1, final long p2, final long p3);
    
    public native Object getObjectVolatile(final Object p0, final long p1);
    
    public native void putObjectVolatile(final Object p0, final long p1, final Object p2);
    
    public native int getIntVolatile(final Object p0, final long p1);
    
    public native void putIntVolatile(final Object p0, final long p1, final int p2);
    
    public native boolean getBooleanVolatile(final Object p0, final long p1);
    
    public native void putBooleanVolatile(final Object p0, final long p1, final boolean p2);
    
    public native byte getByteVolatile(final Object p0, final long p1);
    
    public native void putByteVolatile(final Object p0, final long p1, final byte p2);
    
    public native short getShortVolatile(final Object p0, final long p1);
    
    public native void putShortVolatile(final Object p0, final long p1, final short p2);
    
    public native char getCharVolatile(final Object p0, final long p1);
    
    public native void putCharVolatile(final Object p0, final long p1, final char p2);
    
    public native long getLongVolatile(final Object p0, final long p1);
    
    public native void putLongVolatile(final Object p0, final long p1, final long p2);
    
    public native float getFloatVolatile(final Object p0, final long p1);
    
    public native void putFloatVolatile(final Object p0, final long p1, final float p2);
    
    public native double getDoubleVolatile(final Object p0, final long p1);
    
    public native void putDoubleVolatile(final Object p0, final long p1, final double p2);
    
    public native void putOrderedObject(final Object p0, final long p1, final Object p2);
    
    public native void putOrderedInt(final Object p0, final long p1, final int p2);
    
    public native void putOrderedLong(final Object p0, final long p1, final long p2);
    
    public native void unpark(final Object p0);
    
    public native void park(final boolean p0, final long p1);
    
    public native int getLoadAverage(final double[] p0, final int p1);
    
    public final int getAndAddInt(final Object o, final long n, final int n2) {
        int intVolatile;
        do {
            intVolatile = this.getIntVolatile(o, n);
        } while (!this.compareAndSwapInt(o, n, intVolatile, intVolatile + n2));
        return intVolatile;
    }
    
    public final long getAndAddLong(final Object o, final long n, final long n2) {
        long longVolatile;
        do {
            longVolatile = this.getLongVolatile(o, n);
        } while (!this.compareAndSwapLong(o, n, longVolatile, longVolatile + n2));
        return longVolatile;
    }
    
    public final int getAndSetInt(final Object o, final long n, final int n2) {
        int intVolatile;
        do {
            intVolatile = this.getIntVolatile(o, n);
        } while (!this.compareAndSwapInt(o, n, intVolatile, n2));
        return intVolatile;
    }
    
    public final long getAndSetLong(final Object o, final long n, final long n2) {
        long longVolatile;
        do {
            longVolatile = this.getLongVolatile(o, n);
        } while (!this.compareAndSwapLong(o, n, longVolatile, n2));
        return longVolatile;
    }
    
    public final Object getAndSetObject(final Object o, final long n, final Object o2) {
        Object objectVolatile;
        do {
            objectVolatile = this.getObjectVolatile(o, n);
        } while (!this.compareAndSwapObject(o, n, objectVolatile, o2));
        return objectVolatile;
    }
    
    public native void loadFence();
    
    public native void storeFence();
    
    public native void fullFence();
    
    private static void throwIllegalAccessError() {
        throw new IllegalAccessError();
    }
    
    static {
        registerNatives();
        Reflection.registerMethodsToFilter(Unsafe.class, "getUnsafe");
        theUnsafe = new Unsafe();
        ARRAY_BOOLEAN_BASE_OFFSET = Unsafe.theUnsafe.arrayBaseOffset(boolean[].class);
        ARRAY_BYTE_BASE_OFFSET = Unsafe.theUnsafe.arrayBaseOffset(byte[].class);
        ARRAY_SHORT_BASE_OFFSET = Unsafe.theUnsafe.arrayBaseOffset(short[].class);
        ARRAY_CHAR_BASE_OFFSET = Unsafe.theUnsafe.arrayBaseOffset(char[].class);
        ARRAY_INT_BASE_OFFSET = Unsafe.theUnsafe.arrayBaseOffset(int[].class);
        ARRAY_LONG_BASE_OFFSET = Unsafe.theUnsafe.arrayBaseOffset(long[].class);
        ARRAY_FLOAT_BASE_OFFSET = Unsafe.theUnsafe.arrayBaseOffset(float[].class);
        ARRAY_DOUBLE_BASE_OFFSET = Unsafe.theUnsafe.arrayBaseOffset(double[].class);
        ARRAY_OBJECT_BASE_OFFSET = Unsafe.theUnsafe.arrayBaseOffset(Object[].class);
        ARRAY_BOOLEAN_INDEX_SCALE = Unsafe.theUnsafe.arrayIndexScale(boolean[].class);
        ARRAY_BYTE_INDEX_SCALE = Unsafe.theUnsafe.arrayIndexScale(byte[].class);
        ARRAY_SHORT_INDEX_SCALE = Unsafe.theUnsafe.arrayIndexScale(short[].class);
        ARRAY_CHAR_INDEX_SCALE = Unsafe.theUnsafe.arrayIndexScale(char[].class);
        ARRAY_INT_INDEX_SCALE = Unsafe.theUnsafe.arrayIndexScale(int[].class);
        ARRAY_LONG_INDEX_SCALE = Unsafe.theUnsafe.arrayIndexScale(long[].class);
        ARRAY_FLOAT_INDEX_SCALE = Unsafe.theUnsafe.arrayIndexScale(float[].class);
        ARRAY_DOUBLE_INDEX_SCALE = Unsafe.theUnsafe.arrayIndexScale(double[].class);
        ARRAY_OBJECT_INDEX_SCALE = Unsafe.theUnsafe.arrayIndexScale(Object[].class);
        ADDRESS_SIZE = Unsafe.theUnsafe.addressSize();
    }
}
