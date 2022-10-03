package org.omg.CORBA.portable;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class UnknownException extends SystemException
{
    public Throwable originalEx;
    
    public UnknownException(final Throwable originalEx) {
        super("", 0, CompletionStatus.COMPLETED_MAYBE);
        this.originalEx = originalEx;
    }
}
