package io.netty.util.internal.logging;

final class FormattingTuple
{
    private final String message;
    private final Throwable throwable;
    
    FormattingTuple(final String message, final Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
}
