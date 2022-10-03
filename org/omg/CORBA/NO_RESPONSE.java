package org.omg.CORBA;

public final class NO_RESPONSE extends SystemException
{
    public NO_RESPONSE() {
        this("");
    }
    
    public NO_RESPONSE(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public NO_RESPONSE(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public NO_RESPONSE(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
