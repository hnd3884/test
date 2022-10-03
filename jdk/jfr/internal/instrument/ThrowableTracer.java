package jdk.jfr.internal.instrument;

import jdk.jfr.events.ExceptionThrownEvent;
import jdk.jfr.events.ErrorThrownEvent;
import java.util.concurrent.atomic.AtomicLong;

public final class ThrowableTracer
{
    private static AtomicLong numThrowables;
    
    public static void traceError(final Error error, final String s) {
        if (error instanceof OutOfMemoryError) {
            return;
        }
        final ErrorThrownEvent errorThrownEvent = new ErrorThrownEvent();
        errorThrownEvent.message = s;
        errorThrownEvent.thrownClass = error.getClass();
        errorThrownEvent.commit();
        final ExceptionThrownEvent exceptionThrownEvent = new ExceptionThrownEvent();
        exceptionThrownEvent.message = s;
        exceptionThrownEvent.thrownClass = error.getClass();
        exceptionThrownEvent.commit();
        ThrowableTracer.numThrowables.incrementAndGet();
    }
    
    public static void traceThrowable(final Throwable t, final String message) {
        final ExceptionThrownEvent exceptionThrownEvent = new ExceptionThrownEvent();
        exceptionThrownEvent.message = message;
        exceptionThrownEvent.thrownClass = t.getClass();
        exceptionThrownEvent.commit();
        ThrowableTracer.numThrowables.incrementAndGet();
    }
    
    public static long numThrowables() {
        return ThrowableTracer.numThrowables.get();
    }
    
    static {
        ThrowableTracer.numThrowables = new AtomicLong(0L);
    }
}
