package org.omg.CORBA;

public final class FREE_MEM extends SystemException
{
    public FREE_MEM() {
        this("");
    }
    
    public FREE_MEM(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public FREE_MEM(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public FREE_MEM(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
