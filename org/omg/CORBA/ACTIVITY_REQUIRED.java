package org.omg.CORBA;

public final class ACTIVITY_REQUIRED extends SystemException
{
    public ACTIVITY_REQUIRED() {
        this("");
    }
    
    public ACTIVITY_REQUIRED(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public ACTIVITY_REQUIRED(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public ACTIVITY_REQUIRED(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
