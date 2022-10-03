package io.netty.handler.codec.compression;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.InternalLogger;

public final class Zstd
{
    private static final InternalLogger logger;
    private static final Throwable cause;
    
    public static boolean isAvailable() {
        return Zstd.cause == null;
    }
    
    public static void ensureAvailability() throws Throwable {
        if (Zstd.cause != null) {
            throw Zstd.cause;
        }
    }
    
    public static Throwable cause() {
        return Zstd.cause;
    }
    
    private Zstd() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(Zstd.class);
        Throwable t = null;
        try {
            Class.forName("com.github.luben.zstd.Zstd", false, PlatformDependent.getClassLoader(Zstd.class));
        }
        catch (final ClassNotFoundException e) {
            t = e;
            Zstd.logger.debug("zstd-jni not in the classpath; Zstd support will be unavailable.");
        }
        catch (final Throwable e2) {
            t = e2;
            Zstd.logger.debug("Failed to load zstd-jni; Zstd support will be unavailable.", t);
        }
        cause = t;
    }
}
