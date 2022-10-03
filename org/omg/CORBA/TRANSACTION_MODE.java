package org.omg.CORBA;

public final class TRANSACTION_MODE extends SystemException
{
    public TRANSACTION_MODE() {
        this("");
    }
    
    public TRANSACTION_MODE(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public TRANSACTION_MODE(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public TRANSACTION_MODE(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
