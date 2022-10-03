package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.nio.ByteBuffer;
import io.netty.util.internal.logging.InternalLogger;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class CleanerJava6 implements Cleaner
{
    private static final long CLEANER_FIELD_OFFSET;
    private static final Method CLEAN_METHOD;
    private static final Field CLEANER_FIELD;
    private static final InternalLogger logger;
    
    static boolean isSupported() {
        return CleanerJava6.CLEANER_FIELD_OFFSET != -1L || CleanerJava6.CLEANER_FIELD != null;
    }
    
    @Override
    public void freeDirectBuffer(final ByteBuffer buffer) {
        if (!buffer.isDirect()) {
            return;
        }
        if (System.getSecurityManager() == null) {
            try {
                freeDirectBuffer0(buffer);
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
        final Throwable cause = AccessController.doPrivileged((PrivilegedAction<Throwable>)new PrivilegedAction<Throwable>() {
            @Override
            public Throwable run() {
                try {
                    freeDirectBuffer0(buffer);
                    return null;
                }
                catch (final Throwable cause) {
                    return cause;
                }
            }
        });
        if (cause != null) {
            PlatformDependent0.throwException(cause);
        }
    }
    
    private static void freeDirectBuffer0(final ByteBuffer buffer) throws Exception {
        Object cleaner;
        if (CleanerJava6.CLEANER_FIELD_OFFSET == -1L) {
            cleaner = CleanerJava6.CLEANER_FIELD.get(buffer);
        }
        else {
            cleaner = PlatformDependent0.getObject(buffer, CleanerJava6.CLEANER_FIELD_OFFSET);
        }
        if (cleaner != null) {
            CleanerJava6.CLEAN_METHOD.invoke(cleaner, new Object[0]);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(CleanerJava6.class);
        Throwable error = null;
        final ByteBuffer direct = ByteBuffer.allocateDirect(1);
        Field cleanerField;
        long fieldOffset;
        Method clean;
        try {
            final Object mayBeCleanerField = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        final Field cleanerField = direct.getClass().getDeclaredField("cleaner");
                        if (!PlatformDependent.hasUnsafe()) {
                            cleanerField.setAccessible(true);
                        }
                        return cleanerField;
                    }
                    catch (final Throwable cause) {
                        return cause;
                    }
                }
            });
            if (mayBeCleanerField instanceof Throwable) {
                throw (Throwable)mayBeCleanerField;
            }
            cleanerField = (Field)mayBeCleanerField;
            Object cleaner;
            if (PlatformDependent.hasUnsafe()) {
                fieldOffset = PlatformDependent0.objectFieldOffset(cleanerField);
                cleaner = PlatformDependent0.getObject(direct, fieldOffset);
            }
            else {
                fieldOffset = -1L;
                cleaner = cleanerField.get(direct);
            }
            clean = cleaner.getClass().getDeclaredMethod("clean", (Class<?>[])new Class[0]);
            clean.invoke(cleaner, new Object[0]);
        }
        catch (final Throwable t) {
            fieldOffset = -1L;
            clean = null;
            error = t;
            cleanerField = null;
        }
        if (error == null) {
            CleanerJava6.logger.debug("java.nio.ByteBuffer.cleaner(): available");
        }
        else {
            CleanerJava6.logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
        }
        CLEANER_FIELD = cleanerField;
        CLEANER_FIELD_OFFSET = fieldOffset;
        CLEAN_METHOD = clean;
    }
}
