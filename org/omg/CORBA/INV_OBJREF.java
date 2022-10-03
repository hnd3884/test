package org.omg.CORBA;

public final class INV_OBJREF extends SystemException
{
    public INV_OBJREF() {
        this("");
    }
    
    public INV_OBJREF(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public INV_OBJREF(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public INV_OBJREF(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
