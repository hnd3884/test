package io.netty.handler.codec.spdy;

import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.PlatformDependent;

public class SpdyProtocolException extends Exception
{
    private static final long serialVersionUID = 7870000537743847264L;
    
    public SpdyProtocolException() {
    }
    
    public SpdyProtocolException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public SpdyProtocolException(final String message) {
        super(message);
    }
    
    public SpdyProtocolException(final Throwable cause) {
        super(cause);
    }
    
    static SpdyProtocolException newStatic(final String message, final Class<?> clazz, final String method) {
        SpdyProtocolException exception;
        if (PlatformDependent.javaVersion() >= 7) {
            exception = new StacklessSpdyProtocolException(message, true);
        }
        else {
            exception = new StacklessSpdyProtocolException(message);
        }
        return ThrowableUtil.unknownStackTrace(exception, clazz, method);
    }
    
    @SuppressJava6Requirement(reason = "uses Java 7+ Exception.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
    private SpdyProtocolException(final String message, final boolean shared) {
        super(message, null, false, true);
        assert shared;
    }
    
    private static final class StacklessSpdyProtocolException extends SpdyProtocolException
    {
        private static final long serialVersionUID = -6302754207557485099L;
        
        StacklessSpdyProtocolException(final String message) {
            super(message);
        }
        
        StacklessSpdyProtocolException(final String message, final boolean shared) {
            super(message, shared, null);
        }
        
        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
}
