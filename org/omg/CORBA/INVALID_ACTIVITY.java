package org.omg.CORBA;

public final class INVALID_ACTIVITY extends SystemException
{
    public INVALID_ACTIVITY() {
        this("");
    }
    
    public INVALID_ACTIVITY(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public INVALID_ACTIVITY(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public INVALID_ACTIVITY(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
