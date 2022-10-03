package org.jscep.transaction;

import net.jcip.annotations.Immutable;

@Immutable
public class TransactionException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public TransactionException(final Throwable cause) {
        super(cause);
    }
    
    public TransactionException(final String message) {
        super(message);
    }
}
