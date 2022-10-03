package org.omg.CORBA;

public final class ACTIVITY_COMPLETED extends SystemException
{
    public ACTIVITY_COMPLETED() {
        this("");
    }
    
    public ACTIVITY_COMPLETED(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public ACTIVITY_COMPLETED(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public ACTIVITY_COMPLETED(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
