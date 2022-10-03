package io.netty.handler.codec.compression;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLoggerFactory;
import com.aayushatharva.brotli4j.Brotli4jLoader;
import io.netty.util.internal.logging.InternalLogger;

public final class Brotli
{
    private static final InternalLogger logger;
    private static final ClassNotFoundException CNFE;
    private static Throwable cause;
    
    public static boolean isAvailable() {
        return Brotli.CNFE == null && Brotli4jLoader.isAvailable();
    }
    
    public static void ensureAvailability() throws Throwable {
        if (Brotli.CNFE != null) {
            throw Brotli.CNFE;
        }
        Brotli4jLoader.ensureAvailability();
    }
    
    public static Throwable cause() {
        return Brotli.cause;
    }
    
    private Brotli() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(Brotli.class);
        ClassNotFoundException cnfe = null;
        try {
            Class.forName("com.aayushatharva.brotli4j.Brotli4jLoader", false, PlatformDependent.getClassLoader(Brotli.class));
        }
        catch (final ClassNotFoundException t) {
            cnfe = t;
            Brotli.logger.debug("brotli4j not in the classpath; Brotli support will be unavailable.");
        }
        CNFE = cnfe;
        if (cnfe == null) {
            Brotli.cause = Brotli4jLoader.getUnavailabilityCause();
            if (Brotli.cause != null) {
                Brotli.logger.debug("Failed to load brotli4j; Brotli support will be unavailable.", Brotli.cause);
            }
        }
    }
}
