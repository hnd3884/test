package org.omg.CORBA;

public final class INVALID_TRANSACTION extends SystemException
{
    public INVALID_TRANSACTION() {
        this("");
    }
    
    public INVALID_TRANSACTION(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public INVALID_TRANSACTION(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public INVALID_TRANSACTION(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
