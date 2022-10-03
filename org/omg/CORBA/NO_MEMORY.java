package org.omg.CORBA;

public final class NO_MEMORY extends SystemException
{
    public NO_MEMORY() {
        this("");
    }
    
    public NO_MEMORY(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public NO_MEMORY(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public NO_MEMORY(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
