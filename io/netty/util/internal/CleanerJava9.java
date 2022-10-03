package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.nio.ByteBuffer;
import java.lang.reflect.Method;
import io.netty.util.internal.logging.InternalLogger;

final class CleanerJava9 implements Cleaner
{
    private static final InternalLogger logger;
    private static final Method INVOKE_CLEANER;
    
    static boolean isSupported() {
        return CleanerJava9.INVOKE_CLEANER != null;
    }
    
    @Override
    public void freeDirectBuffer(final ByteBuffer buffer) {
        if (System.getSecurityManager() == null) {
            try {
                CleanerJava9.INVOKE_CLEANER.invoke(PlatformDependent0.UNSAFE, buffer);
            }
            catch (final Throwable cause) {
                PlatformDependent0.throwException(cause);
            }
        }
        else {
            freeDirectBufferPrivileged(buffer);
        }
    }
    
    private static void freeDirectBufferPrivileged(final ByteBuffer buffer) {
        final Exception error = AccessController.doPrivileged((PrivilegedAction<Exception>)new PrivilegedAction<Exception>() {
            @Override
            public Exception run() {
                try {
                    CleanerJava9.INVOKE_CLEANER.invoke(PlatformDependent0.UNSAFE, buffer);
                }
                catch (final InvocationTargetException e) {
                    return e;
                }
                catch (final IllegalAccessException e2) {
                    return e2;
                }
                return null;
            }
        });
        if (error != null) {
            PlatformDependent0.throwException(error);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(CleanerJava9.class);
        Method method;
        Throwable error;
        if (PlatformDependent0.hasUnsafe()) {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(1);
            final Object maybeInvokeMethod = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        final Method m = PlatformDependent0.UNSAFE.getClass().getDeclaredMethod("invokeCleaner", ByteBuffer.class);
                        m.invoke(PlatformDependent0.UNSAFE, buffer);
                        return m;
                    }
                    catch (final NoSuchMethodException e) {
                        return e;
                    }
                    catch (final InvocationTargetException e2) {
                        return e2;
                    }
                    catch (final IllegalAccessException e3) {
                        return e3;
                    }
                }
            });
            if (maybeInvokeMethod instanceof Throwable) {
                method = null;
                error = (Throwable)maybeInvokeMethod;
            }
            else {
                method = (Method)maybeInvokeMethod;
                error = null;
            }
        }
        else {
            method = null;
            error = new UnsupportedOperationException("sun.misc.Unsafe unavailable");
        }
        if (error == null) {
            CleanerJava9.logger.debug("java.nio.ByteBuffer.cleaner(): available");
        }
        else {
            CleanerJava9.logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
        }
        INVOKE_CLEANER = method;
    }
}
