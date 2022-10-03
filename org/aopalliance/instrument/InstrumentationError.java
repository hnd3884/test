package org.aopalliance.instrument;

public class InstrumentationError extends Error
{
    public InstrumentationError(final Instrumentation instrumentation, final Throwable cause) {
        super("Error while instrumenting " + instrumentation, cause);
    }
}
