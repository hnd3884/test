package io.netty.handler.ssl;

public final class SniCompletionEvent extends SslCompletionEvent
{
    private final String hostname;
    
    SniCompletionEvent(final String hostname) {
        this.hostname = hostname;
    }
    
    SniCompletionEvent(final String hostname, final Throwable cause) {
        super(cause);
        this.hostname = hostname;
    }
    
    SniCompletionEvent(final Throwable cause) {
        this(null, cause);
    }
    
    public String hostname() {
        return this.hostname;
    }
    
    @Override
    public String toString() {
        final Throwable cause = this.cause();
        return (cause == null) ? (this.getClass().getSimpleName() + "(SUCCESS='" + this.hostname + "'\")") : (this.getClass().getSimpleName() + '(' + cause + ')');
    }
}
