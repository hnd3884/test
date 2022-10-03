package org.omg.CORBA;

public final class INV_POLICY extends SystemException
{
    public INV_POLICY() {
        this("");
    }
    
    public INV_POLICY(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public INV_POLICY(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public INV_POLICY(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
