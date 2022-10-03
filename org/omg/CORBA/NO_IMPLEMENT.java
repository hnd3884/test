package org.omg.CORBA;

public final class NO_IMPLEMENT extends SystemException
{
    public NO_IMPLEMENT() {
        this("");
    }
    
    public NO_IMPLEMENT(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public NO_IMPLEMENT(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public NO_IMPLEMENT(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
