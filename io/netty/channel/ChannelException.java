package io.netty.channel;

import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;

public class ChannelException extends RuntimeException
{
    private static final long serialVersionUID = 2908618315971075004L;
    
    public ChannelException() {
    }
    
    public ChannelException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ChannelException(final String message) {
        super(message);
    }
    
    public ChannelException(final Throwable cause) {
        super(cause);
    }
    
    @SuppressJava6Requirement(reason = "uses Java 7+ RuntimeException.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
    protected ChannelException(final String message, final Throwable cause, final boolean shared) {
        super(message, cause, false, true);
        assert shared;
    }
    
    static ChannelException newStatic(final String message, final Class<?> clazz, final String method) {
        ChannelException exception;
        if (PlatformDependent.javaVersion() >= 7) {
            exception = new StacklessChannelException(message, null, true);
        }
        else {
            exception = new StacklessChannelException(message, null);
        }
        return ThrowableUtil.unknownStackTrace(exception, clazz, method);
    }
    
    private static final class StacklessChannelException extends ChannelException
    {
        private static final long serialVersionUID = -6384642137753538579L;
        
        StacklessChannelException(final String message, final Throwable cause) {
            super(message, cause);
        }
        
        StacklessChannelException(final String message, final Throwable cause, final boolean shared) {
            super(message, cause, shared);
        }
        
        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
}
