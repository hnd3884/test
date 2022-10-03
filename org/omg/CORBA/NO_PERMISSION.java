package org.omg.CORBA;

public final class NO_PERMISSION extends SystemException
{
    public NO_PERMISSION() {
        this("");
    }
    
    public NO_PERMISSION(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public NO_PERMISSION(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public NO_PERMISSION(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
