package org.glassfish.jersey.internal.util.collection;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.jersey.internal.util.JdkVersion;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.BlockingQueue;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public final class DataStructures
{
    private static final Class<?> LTQ_CLASS;
    public static final int DEFAULT_CONCURENCY_LEVEL;
    
    private static int ceilingNextPowerOfTwo(final int x) {
        return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
    }
    
    private static Class<?> getAndVerify(final String cn) throws Throwable {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>)new PrivilegedExceptionAction<Class<?>>() {
                @Override
                public Class<?> run() throws Exception {
                    return DataStructures.class.getClassLoader().loadClass(cn).newInstance().getClass();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw ex.getCause();
        }
    }
    
    public static <E> BlockingQueue<E> createLinkedTransferQueue() {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<BlockingQueue<E>>)new PrivilegedExceptionAction<BlockingQueue<E>>() {
                @Override
                public BlockingQueue<E> run() throws Exception {
                    return DataStructures.LTQ_CLASS.newInstance();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new RuntimeException(cause);
        }
    }
    
    public static <K, V> ConcurrentMap<K, V> createConcurrentMap() {
        return (ConcurrentMap<K, V>)(JdkVersion.getJdkVersion().isUnsafeSupported() ? new ConcurrentHashMapV8<Object, Object>() : new ConcurrentHashMap<Object, Object>());
    }
    
    public static <K, V> ConcurrentMap<K, V> createConcurrentMap(final Map<? extends K, ? extends V> map) {
        return (ConcurrentMap<K, V>)(JdkVersion.getJdkVersion().isUnsafeSupported() ? new ConcurrentHashMapV8<Object, Object>(map) : new ConcurrentHashMap<Object, Object>(map));
    }
    
    public static <K, V> ConcurrentMap<K, V> createConcurrentMap(final int initialCapacity) {
        return (ConcurrentMap<K, V>)(JdkVersion.getJdkVersion().isUnsafeSupported() ? new ConcurrentHashMapV8<Object, Object>(initialCapacity) : new ConcurrentHashMap<Object, Object>(initialCapacity));
    }
    
    public static <K, V> ConcurrentMap<K, V> createConcurrentMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        return (ConcurrentMap<K, V>)(JdkVersion.getJdkVersion().isUnsafeSupported() ? new ConcurrentHashMapV8<Object, Object>(initialCapacity, loadFactor, concurrencyLevel) : new ConcurrentHashMap<Object, Object>(initialCapacity, loadFactor, concurrencyLevel));
    }
    
    static {
        String className = null;
        Class<?> c;
        try {
            final JdkVersion jdkVersion = JdkVersion.getJdkVersion();
            final JdkVersion minimumVersion = JdkVersion.parseVersion("1.7.0");
            className = ((minimumVersion.compareTo(jdkVersion) <= 0) ? "java.util.concurrent.LinkedTransferQueue" : "org.glassfish.jersey.internal.util.collection.LinkedTransferQueue");
            c = getAndVerify(className);
            Logger.getLogger(DataStructures.class.getName()).log(Level.FINE, "USING LTQ class:{0}", c);
        }
        catch (final Throwable t) {
            Logger.getLogger(DataStructures.class.getName()).log(Level.FINE, "failed loading data structure class:" + className + " fallback to embedded one", t);
            c = LinkedBlockingQueue.class;
        }
        LTQ_CLASS = c;
        DEFAULT_CONCURENCY_LEVEL = ceilingNextPowerOfTwo(Runtime.getRuntime().availableProcessors());
    }
}
