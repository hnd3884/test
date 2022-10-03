package org.omg.CORBA;

public final class COMM_FAILURE extends SystemException
{
    public COMM_FAILURE() {
        this("");
    }
    
    public COMM_FAILURE(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public COMM_FAILURE(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public COMM_FAILURE(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
