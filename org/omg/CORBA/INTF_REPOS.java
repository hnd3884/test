package org.omg.CORBA;

public final class INTF_REPOS extends SystemException
{
    public INTF_REPOS() {
        this("");
    }
    
    public INTF_REPOS(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public INTF_REPOS(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public INTF_REPOS(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
