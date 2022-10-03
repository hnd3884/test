package org.omg.CORBA;

public final class TRANSACTION_UNAVAILABLE extends SystemException
{
    public TRANSACTION_UNAVAILABLE() {
        this("");
    }
    
    public TRANSACTION_UNAVAILABLE(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public TRANSACTION_UNAVAILABLE(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public TRANSACTION_UNAVAILABLE(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
