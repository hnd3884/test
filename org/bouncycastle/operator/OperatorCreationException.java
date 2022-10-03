package org.bouncycastle.operator;

public class OperatorCreationException extends OperatorException
{
    public OperatorCreationException(final String s, final Throwable t) {
        super(s, t);
    }
    
    public OperatorCreationException(final String s) {
        super(s);
    }
}
