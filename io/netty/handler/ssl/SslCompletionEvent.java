package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;

public abstract class SslCompletionEvent
{
    private final Throwable cause;
    
    SslCompletionEvent() {
        this.cause = null;
    }
    
    SslCompletionEvent(final Throwable cause) {
        this.cause = ObjectUtil.checkNotNull(cause, "cause");
    }
    
    public final boolean isSuccess() {
        return this.cause == null;
    }
    
    public final Throwable cause() {
        return this.cause;
    }
    
    @Override
    public String toString() {
        final Throwable cause = this.cause();
        return (cause == null) ? (this.getClass().getSimpleName() + "(SUCCESS)") : (this.getClass().getSimpleName() + '(' + cause + ')');
    }
}
