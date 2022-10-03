package jdk.jfr.internal.instrument;

import jdk.jfr.events.X509ValidationEvent;
import jdk.jfr.events.X509CertificateEvent;
import jdk.jfr.events.TLSHandshakeEvent;
import jdk.jfr.events.SecurityPropertyModificationEvent;
import jdk.jfr.events.ActiveRecordingEvent;
import jdk.jfr.events.ActiveSettingEvent;
import jdk.jfr.events.ErrorThrownEvent;
import jdk.jfr.events.ExceptionThrownEvent;
import jdk.jfr.events.SocketWriteEvent;
import jdk.jfr.events.SocketReadEvent;
import jdk.jfr.events.FileWriteEvent;
import jdk.jfr.events.FileReadEvent;
import jdk.jfr.events.FileForceEvent;
import java.util.ArrayList;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import jdk.jfr.internal.RequestEngine;
import jdk.jfr.events.ExceptionStatisticsEvent;
import jdk.jfr.Event;
import jdk.jfr.internal.SecuritySupport;
import jdk.jfr.internal.JVM;

public final class JDKEvents
{
    private static final Class<?>[] eventClasses;
    private static final Class<?>[] instrumentationClasses;
    private static final Class<?>[] targetClasses;
    private static final JVM jvm;
    private static final Runnable emitExceptionStatistics;
    private static boolean initializationTriggered;
    
    public static synchronized void initialize() {
        try {
            if (!JDKEvents.initializationTriggered) {
                final Class<?>[] eventClasses = JDKEvents.eventClasses;
                for (int length = eventClasses.length, i = 0; i < length; ++i) {
                    SecuritySupport.registerEvent((Class<? extends Event>)eventClasses[i]);
                }
                JDKEvents.initializationTriggered = true;
                RequestEngine.addTrustedJDKHook((Class<? extends Event>)ExceptionStatisticsEvent.class, JDKEvents.emitExceptionStatistics);
            }
        }
        catch (final Exception ex) {
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.WARN, "Could not initialize JDK events. " + ex.getMessage());
        }
    }
    
    public static void addInstrumentation() {
        try {
            final ArrayList list = new ArrayList();
            for (int i = 0; i < JDKEvents.instrumentationClasses.length; ++i) {
                list.add(JDKEvents.targetClasses[i] = Class.forName(JDKEvents.instrumentationClasses[i].getAnnotation(JIInstrumentationTarget.class).value()));
            }
            list.add(Throwable.class);
            list.add(Error.class);
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.INFO, "Retransformed JDK classes");
            JDKEvents.jvm.retransformClasses((Class<?>[])list.toArray(new Class[list.size()]));
        }
        catch (final Exception ex) {
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.WARN, "Could not add instrumentation for JDK events. " + ex.getMessage());
        }
    }
    
    private static void emitExceptionStatistics() {
        final ExceptionStatisticsEvent exceptionStatisticsEvent = new ExceptionStatisticsEvent();
        exceptionStatisticsEvent.throwables = ThrowableTracer.numThrowables();
        exceptionStatisticsEvent.commit();
    }
    
    public static byte[] retransformCallback(final Class<?> clazz, final byte[] array) throws Throwable {
        if (Throwable.class == clazz) {
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.TRACE, "Instrumenting java.lang.Throwable");
            return ConstructorTracerWriter.generateBytes(Throwable.class, array);
        }
        if (Error.class == clazz) {
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.TRACE, "Instrumenting java.lang.Error");
            return ConstructorTracerWriter.generateBytes(Error.class, array);
        }
        for (int n = 0; n < JDKEvents.targetClasses.length; ++n) {
            if (JDKEvents.targetClasses[n].equals(clazz)) {
                Logger.log(LogTag.JFR_SYSTEM, LogLevel.TRACE, () -> {
                    final int n;
                    final Object o = JDKEvents.instrumentationClasses[n];
                    return "Processing instrumentation class: " + clazz2;
                });
                return new JIClassInstrumentation(JDKEvents.instrumentationClasses[n], clazz, array).getNewBytes();
            }
        }
        return array;
    }
    
    public static void remove() {
        RequestEngine.removeHook(JDKEvents::emitExceptionStatistics);
    }
    
    static {
        eventClasses = new Class[] { FileForceEvent.class, FileReadEvent.class, FileWriteEvent.class, SocketReadEvent.class, SocketWriteEvent.class, ExceptionThrownEvent.class, ExceptionStatisticsEvent.class, ErrorThrownEvent.class, ActiveSettingEvent.class, ActiveRecordingEvent.class, SecurityPropertyModificationEvent.class, TLSHandshakeEvent.class, X509CertificateEvent.class, X509ValidationEvent.class };
        instrumentationClasses = new Class[] { FileInputStreamInstrumentor.class, FileOutputStreamInstrumentor.class, RandomAccessFileInstrumentor.class, FileChannelImplInstrumentor.class, SocketInputStreamInstrumentor.class, SocketOutputStreamInstrumentor.class, SocketChannelImplInstrumentor.class };
        targetClasses = new Class[JDKEvents.instrumentationClasses.length];
        jvm = JVM.getJVM();
        emitExceptionStatistics = JDKEvents::emitExceptionStatistics;
    }
}
