package io.netty.util.internal.shaded.org.jctools.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class UnsafeAccess
{
    public static final boolean SUPPORTS_GET_AND_SET_REF;
    public static final boolean SUPPORTS_GET_AND_ADD_LONG;
    public static final Unsafe UNSAFE;
    
    private static Unsafe getUnsafe() {
        Unsafe instance;
        try {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            instance = (Unsafe)field.get(null);
        }
        catch (final Exception ignored) {
            try {
                final Constructor<Unsafe> c = Unsafe.class.getDeclaredConstructor((Class<?>[])new Class[0]);
                c.setAccessible(true);
                instance = c.newInstance(new Object[0]);
            }
            catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }
    
    private static boolean hasGetAndSetSupport() {
        try {
            Unsafe.class.getMethod("getAndSetObject", Object.class, Long.TYPE, Object.class);
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    private static boolean hasGetAndAddLongSupport() {
        try {
            Unsafe.class.getMethod("getAndAddLong", Object.class, Long.TYPE, Long.TYPE);
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public static long fieldOffset(final Class clz, final String fieldName) throws RuntimeException {
        try {
            return UnsafeAccess.UNSAFE.objectFieldOffset(clz.getDeclaredField(fieldName));
        }
        catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        UNSAFE = getUnsafe();
        SUPPORTS_GET_AND_SET_REF = hasGetAndSetSupport();
        SUPPORTS_GET_AND_ADD_LONG = hasGetAndAddLongSupport();
    }
}
