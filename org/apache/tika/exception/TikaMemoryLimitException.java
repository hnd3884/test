package org.apache.tika.exception;

public class TikaMemoryLimitException extends TikaException
{
    public TikaMemoryLimitException(final String msg) {
        super(msg);
    }
    
    public TikaMemoryLimitException(final long triedToAllocate, final long maxAllowable) {
        super(msg(triedToAllocate, maxAllowable));
    }
    
    private static String msg(final long triedToAllocate, final long maxAllowable) {
        return "Tried to allocate " + triedToAllocate + " bytes, but " + maxAllowable + " is the maximum allowed. Please open an issue https://issues.apache.org/jira/projects/TIKA if you believe this file is not corrupt.";
    }
}
