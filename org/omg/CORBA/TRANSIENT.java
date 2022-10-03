package org.omg.CORBA;

public final class TRANSIENT extends SystemException
{
    public TRANSIENT() {
        this("");
    }
    
    public TRANSIENT(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public TRANSIENT(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public TRANSIENT(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
