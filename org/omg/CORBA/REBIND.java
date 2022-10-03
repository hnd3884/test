package org.omg.CORBA;

public final class REBIND extends SystemException
{
    public REBIND() {
        this("");
    }
    
    public REBIND(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public REBIND(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public REBIND(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
