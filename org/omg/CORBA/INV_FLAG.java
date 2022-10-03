package org.omg.CORBA;

public final class INV_FLAG extends SystemException
{
    public INV_FLAG() {
        this("");
    }
    
    public INV_FLAG(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public INV_FLAG(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public INV_FLAG(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
