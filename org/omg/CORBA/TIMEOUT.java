package org.omg.CORBA;

public final class TIMEOUT extends SystemException
{
    public TIMEOUT() {
        this("");
    }
    
    public TIMEOUT(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public TIMEOUT(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public TIMEOUT(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
