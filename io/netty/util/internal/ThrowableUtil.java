package io.netty.util.internal;

import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

public final class ThrowableUtil
{
    private ThrowableUtil() {
    }
    
    public static <T extends Throwable> T unknownStackTrace(final T cause, final Class<?> clazz, final String method) {
        cause.setStackTrace(new StackTraceElement[] { new StackTraceElement(clazz.getName(), method, null, -1) });
        return cause;
    }
    
    public static String stackTraceToString(final Throwable cause) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream pout = new PrintStream(out);
        cause.printStackTrace(pout);
        pout.flush();
        try {
            return new String(out.toByteArray());
        }
        finally {
            try {
                out.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public static boolean haveSuppressed() {
        return PlatformDependent.javaVersion() >= 7;
    }
    
    @SuppressJava6Requirement(reason = "Throwable addSuppressed is only available for >= 7. Has check for < 7.")
    public static void addSuppressed(final Throwable target, final Throwable suppressed) {
        if (!haveSuppressed()) {
            return;
        }
        target.addSuppressed(suppressed);
    }
    
    public static void addSuppressedAndClear(final Throwable target, final List<Throwable> suppressed) {
        addSuppressed(target, suppressed);
        suppressed.clear();
    }
    
    public static void addSuppressed(final Throwable target, final List<Throwable> suppressed) {
        for (final Throwable t : suppressed) {
            addSuppressed(target, t);
        }
    }
}
