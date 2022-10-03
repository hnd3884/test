package org.omg.CORBA;

public final class TRANSACTION_ROLLEDBACK extends SystemException
{
    public TRANSACTION_ROLLEDBACK() {
        this("");
    }
    
    public TRANSACTION_ROLLEDBACK(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public TRANSACTION_ROLLEDBACK(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public TRANSACTION_ROLLEDBACK(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
