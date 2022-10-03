package org.bouncycastle.operator;

import java.io.IOException;

public class OperatorStreamException extends IOException
{
    private Throwable cause;
    
    public OperatorStreamException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
