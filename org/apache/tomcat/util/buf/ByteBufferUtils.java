package org.apache.tomcat.util.buf;

import java.lang.reflect.Field;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.juli.logging.LogFactory;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.lang.reflect.Method;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

public class ByteBufferUtils
{
    private static final StringManager sm;
    private static final Log log;
    private static final Object unsafe;
    private static final Method cleanerMethod;
    private static final Method cleanMethod;
    private static final Method invokeCleanerMethod;
    
    private ByteBufferUtils() {
    }
    
    public static ByteBuffer expand(final ByteBuffer in, final int newSize) {
        if (in.capacity() >= newSize) {
            return in;
        }
        boolean direct = false;
        ByteBuffer out;
        if (in.isDirect()) {
            out = ByteBuffer.allocateDirect(newSize);
            direct = true;
        }
        else {
            out = ByteBuffer.allocate(newSize);
        }
        in.flip();
        out.put(in);
        if (direct) {
            cleanDirectBuffer(in);
        }
        return out;
    }
    
    public static void cleanDirectBuffer(final ByteBuffer buf) {
        if (ByteBufferUtils.cleanMethod != null) {
            try {
                ByteBufferUtils.cleanMethod.invoke(ByteBufferUtils.cleanerMethod.invoke(buf, new Object[0]), new Object[0]);
            }
            catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
                if (ByteBufferUtils.log.isDebugEnabled()) {
                    ByteBufferUtils.log.debug((Object)ByteBufferUtils.sm.getString("byteBufferUtils.cleaner"), (Throwable)e);
                }
            }
        }
        else if (ByteBufferUtils.invokeCleanerMethod != null) {
            try {
                ByteBufferUtils.invokeCleanerMethod.invoke(ByteBufferUtils.unsafe, buf);
            }
            catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
                if (ByteBufferUtils.log.isDebugEnabled()) {
                    ByteBufferUtils.log.debug((Object)ByteBufferUtils.sm.getString("byteBufferUtils.cleaner"), (Throwable)e);
                }
            }
        }
    }
    
    static {
        sm = StringManager.getManager("org.apache.tomcat.util.buf");
        log = LogFactory.getLog((Class)ByteBufferUtils.class);
        final ByteBuffer tempBuffer = ByteBuffer.allocateDirect(0);
        Method cleanerMethodLocal = null;
        Method cleanMethodLocal = null;
        Object unsafeLocal = null;
        Method invokeCleanerMethodLocal = null;
        if (JreCompat.isJre9Available()) {
            try {
                final Class<?> clazz = Class.forName("sun.misc.Unsafe");
                final Field theUnsafe = clazz.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                unsafeLocal = theUnsafe.get(null);
                invokeCleanerMethodLocal = clazz.getMethod("invokeCleaner", ByteBuffer.class);
                invokeCleanerMethodLocal.invoke(unsafeLocal, tempBuffer);
            }
            catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | NoSuchFieldException e) {
                ByteBufferUtils.log.warn((Object)ByteBufferUtils.sm.getString("byteBufferUtils.cleaner"), (Throwable)e);
                unsafeLocal = null;
                invokeCleanerMethodLocal = null;
            }
        }
        else {
            try {
                cleanerMethodLocal = tempBuffer.getClass().getMethod("cleaner", (Class<?>[])new Class[0]);
                cleanerMethodLocal.setAccessible(true);
                final Object cleanerObject = cleanerMethodLocal.invoke(tempBuffer, new Object[0]);
                cleanMethodLocal = cleanerObject.getClass().getMethod("clean", (Class<?>[])new Class[0]);
                cleanMethodLocal.invoke(cleanerObject, new Object[0]);
            }
            catch (final NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                ByteBufferUtils.log.warn((Object)ByteBufferUtils.sm.getString("byteBufferUtils.cleaner"), (Throwable)e);
                cleanerMethodLocal = null;
                cleanMethodLocal = null;
            }
        }
        cleanerMethod = cleanerMethodLocal;
        cleanMethod = cleanMethodLocal;
        unsafe = unsafeLocal;
        invokeCleanerMethod = invokeCleanerMethodLocal;
    }
}
