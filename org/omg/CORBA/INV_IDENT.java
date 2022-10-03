package org.omg.CORBA;

public final class INV_IDENT extends SystemException
{
    public INV_IDENT() {
        this("");
    }
    
    public INV_IDENT(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public INV_IDENT(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public INV_IDENT(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
