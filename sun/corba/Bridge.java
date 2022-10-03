package sun.corba;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.io.ObjectInputStream;
import java.security.PrivilegedAction;
import sun.reflect.ReflectionFactory;
import sun.misc.Unsafe;
import java.lang.reflect.Method;
import java.security.Permission;

public final class Bridge
{
    private static final Class[] NO_ARGS;
    private static final Permission getBridgePermission;
    private static Bridge bridge;
    private final Method latestUserDefinedLoaderMethod;
    private final Unsafe unsafe;
    private final ReflectionFactory reflectionFactory;
    public static final long INVALID_FIELD_OFFSET = -1L;
    
    private Method getLatestUserDefinedLoaderMethod() {
        return AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedAction() {
            @Override
            public Object run() {
                Method declaredMethod;
                try {
                    declaredMethod = ObjectInputStream.class.getDeclaredMethod("latestUserDefinedLoader", (Class<?>[])Bridge.NO_ARGS);
                    declaredMethod.setAccessible(true);
                }
                catch (final NoSuchMethodException ex) {
                    final Error error = new Error("java.io.ObjectInputStream latestUserDefinedLoader " + ex);
                    error.initCause(ex);
                    throw error;
                }
                return declaredMethod;
            }
        });
    }
    
    private Unsafe getUnsafe() {
        final Field field = AccessController.doPrivileged((PrivilegedAction<Field>)new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    final Field declaredField = Unsafe.class.getDeclaredField("theUnsafe");
                    declaredField.setAccessible(true);
                    return declaredField;
                }
                catch (final NoSuchFieldException ex) {
                    final Error error = new Error("Could not access Unsafe");
                    error.initCause(ex);
                    throw error;
                }
            }
        });
        Unsafe unsafe;
        try {
            unsafe = (Unsafe)field.get(null);
        }
        catch (final Throwable t) {
            final Error error = new Error("Could not access Unsafe");
            error.initCause(t);
            throw error;
        }
        return unsafe;
    }
    
    private Bridge() {
        this.latestUserDefinedLoaderMethod = this.getLatestUserDefinedLoaderMethod();
        this.unsafe = this.getUnsafe();
        this.reflectionFactory = AccessController.doPrivileged((PrivilegedAction<ReflectionFactory>)new ReflectionFactory.GetReflectionFactoryAction());
    }
    
    public static final synchronized Bridge get() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(Bridge.getBridgePermission);
        }
        if (Bridge.bridge == null) {
            Bridge.bridge = new Bridge();
        }
        return Bridge.bridge;
    }
    
    public final ClassLoader getLatestUserDefinedLoader() {
        try {
            return (ClassLoader)this.latestUserDefinedLoaderMethod.invoke(null, (Object[])Bridge.NO_ARGS);
        }
        catch (final InvocationTargetException ex) {
            final Error error = new Error("sun.corba.Bridge.latestUserDefinedLoader: " + ex);
            error.initCause(ex);
            throw error;
        }
        catch (final IllegalAccessException ex2) {
            final Error error2 = new Error("sun.corba.Bridge.latestUserDefinedLoader: " + ex2);
            error2.initCause(ex2);
            throw error2;
        }
    }
    
    public final int getInt(final Object o, final long n) {
        return this.unsafe.getInt(o, n);
    }
    
    public final void putInt(final Object o, final long n, final int n2) {
        this.unsafe.putInt(o, n, n2);
    }
    
    public final Object getObject(final Object o, final long n) {
        return this.unsafe.getObject(o, n);
    }
    
    public final void putObject(final Object o, final long n, final Object o2) {
        this.unsafe.putObject(o, n, o2);
    }
    
    public final boolean getBoolean(final Object o, final long n) {
        return this.unsafe.getBoolean(o, n);
    }
    
    public final void putBoolean(final Object o, final long n, final boolean b) {
        this.unsafe.putBoolean(o, n, b);
    }
    
    public final byte getByte(final Object o, final long n) {
        return this.unsafe.getByte(o, n);
    }
    
    public final void putByte(final Object o, final long n, final byte b) {
        this.unsafe.putByte(o, n, b);
    }
    
    public final short getShort(final Object o, final long n) {
        return this.unsafe.getShort(o, n);
    }
    
    public final void putShort(final Object o, final long n, final short n2) {
        this.unsafe.putShort(o, n, n2);
    }
    
    public final char getChar(final Object o, final long n) {
        return this.unsafe.getChar(o, n);
    }
    
    public final void putChar(final Object o, final long n, final char c) {
        this.unsafe.putChar(o, n, c);
    }
    
    public final long getLong(final Object o, final long n) {
        return this.unsafe.getLong(o, n);
    }
    
    public final void putLong(final Object o, final long n, final long n2) {
        this.unsafe.putLong(o, n, n2);
    }
    
    public final float getFloat(final Object o, final long n) {
        return this.unsafe.getFloat(o, n);
    }
    
    public final void putFloat(final Object o, final long n, final float n2) {
        this.unsafe.putFloat(o, n, n2);
    }
    
    public final double getDouble(final Object o, final long n) {
        return this.unsafe.getDouble(o, n);
    }
    
    public final void putDouble(final Object o, final long n, final double n2) {
        this.unsafe.putDouble(o, n, n2);
    }
    
    public final long objectFieldOffset(final Field field) {
        return this.unsafe.objectFieldOffset(field);
    }
    
    public final void throwException(final Throwable t) {
        this.unsafe.throwException(t);
    }
    
    public final Constructor newConstructorForSerialization(final Class clazz, final Constructor constructor) {
        return this.reflectionFactory.newConstructorForSerialization(clazz, constructor);
    }
    
    static {
        NO_ARGS = new Class[0];
        getBridgePermission = new BridgePermission("getBridge");
        Bridge.bridge = null;
    }
}
