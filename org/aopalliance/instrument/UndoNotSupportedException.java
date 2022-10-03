package org.aopalliance.instrument;

public class UndoNotSupportedException extends Exception
{
    public UndoNotSupportedException(final Instrumentation instrumentation) {
        super("Undo not supported for instrumentation: " + instrumentation);
    }
}
