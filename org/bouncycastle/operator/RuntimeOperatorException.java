package org.bouncycastle.operator;

public class RuntimeOperatorException extends RuntimeException
{
    private Throwable cause;
    
    public RuntimeOperatorException(final String s) {
        super(s);
    }
    
    public RuntimeOperatorException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
