package org.glassfish.jersey.internal.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtils
{
    private ExceptionUtils() {
    }
    
    public static String exceptionStackTraceAsString(final Throwable t) {
        final StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
    
    public static <T extends Exception> void conditionallyReThrow(final T e, final boolean rethrow, final Logger logger, final String m, final Level level) throws T, Exception {
        if (rethrow) {
            throw e;
        }
        logger.log(level, m, e);
    }
}
