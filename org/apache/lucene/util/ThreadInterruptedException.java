package org.apache.lucene.util;

public final class ThreadInterruptedException extends RuntimeException
{
    public ThreadInterruptedException(final InterruptedException ie) {
        super(ie);
    }
}
