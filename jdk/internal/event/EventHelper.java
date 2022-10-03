package jdk.internal.event;

import sun.misc.SharedSecrets;
import java.time.temporal.Temporal;
import java.time.Duration;
import java.util.Date;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.time.Instant;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import sun.misc.JavaUtilJarAccess;

public final class EventHelper
{
    private static final JavaUtilJarAccess JUJA;
    private static volatile boolean loggingSecurity;
    private static volatile Logger securityLogger;
    private static AtomicReference<Logger> loggerRef;
    private static final Level LOG_LEVEL;
    private static final String SECURITY_LOGGER_NAME = "jdk.event.security";
    
    public static void logTLSHandshakeEvent(final Instant instant, final String s, final int n, final String s2, final String s3, final long n2) {
        assert EventHelper.securityLogger != null;
        EventHelper.securityLogger.log(EventHelper.LOG_LEVEL, getDurationString(instant) + " TLSHandshake: {0}:{1,number,#}, {2}, {3}, {4,number,#}", new Object[] { s, n, s3, s2, n2 });
    }
    
    public static void logSecurityPropertyEvent(final String s, final String s2) {
        assert EventHelper.securityLogger != null;
        EventHelper.securityLogger.log(EventHelper.LOG_LEVEL, "SecurityPropertyModification: key:{0}, value:{1}", new Object[] { s, s2 });
    }
    
    public static void logX509ValidationEvent(final int n, final int[] array) {
        assert EventHelper.securityLogger != null;
        EventHelper.securityLogger.log(EventHelper.LOG_LEVEL, "ValidationChain: {0,number,#}, {1}", new Object[] { n, IntStream.of(array).mapToObj((IntFunction<?>)Integer::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(", ")) });
    }
    
    public static void logX509CertificateEvent(final String s, final String s2, final String s3, final String s4, final String s5, final int n, final long n2, final long n3, final long n4) {
        assert EventHelper.securityLogger != null;
        EventHelper.securityLogger.log(EventHelper.LOG_LEVEL, "X509Certificate: Alg:{0}, Serial:{1}, Subject:{2}, Issuer:{3}, Key type:{4}, Length:{5,number,#}, Cert Id:{6,number,#}, Valid from:{7}, Valid until:{8}", new Object[] { s, s2, s3, s4, s5, n, n2, new Date(n3), new Date(n4) });
    }
    
    private static String getDurationString(final Instant instant) {
        if (instant == null) {
            return "";
        }
        if (instant.equals(Instant.MIN)) {
            return "N/A";
        }
        final long n = Duration.between(instant, Instant.now()).toNanos() / 1000L;
        if (n < 1000000L) {
            return "duration = " + n / 1000.0 + " ms:";
        }
        return "duration = " + n / 1000L / 1000.0 + " s:";
    }
    
    public static boolean isLoggingSecurity() {
        if (EventHelper.securityLogger == null && !EventHelper.JUJA.isInitializing() && EventHelper.loggerRef.compareAndSet(null, Logger.getLogger("jdk.event.security"))) {
            EventHelper.securityLogger = EventHelper.loggerRef.get();
            EventHelper.loggingSecurity = EventHelper.securityLogger.isLoggable(EventHelper.LOG_LEVEL);
        }
        return EventHelper.loggingSecurity;
    }
    
    static {
        JUJA = SharedSecrets.javaUtilJarAccess();
        EventHelper.loggerRef = new AtomicReference<Logger>();
        LOG_LEVEL = Level.FINE;
    }
}
