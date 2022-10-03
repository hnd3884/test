package org.omg.CORBA;

public final class INTERNAL extends SystemException
{
    public INTERNAL() {
        this("");
    }
    
    public INTERNAL(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public INTERNAL(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public INTERNAL(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
