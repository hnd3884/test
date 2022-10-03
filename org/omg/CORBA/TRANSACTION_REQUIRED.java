package org.omg.CORBA;

public final class TRANSACTION_REQUIRED extends SystemException
{
    public TRANSACTION_REQUIRED() {
        this("");
    }
    
    public TRANSACTION_REQUIRED(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public TRANSACTION_REQUIRED(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public TRANSACTION_REQUIRED(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
