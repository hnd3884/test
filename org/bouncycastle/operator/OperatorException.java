package org.bouncycastle.operator;

public class OperatorException extends Exception
{
    private Throwable cause;
    
    public OperatorException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public OperatorException(final String s) {
        super(s);
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
