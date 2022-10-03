package org.omg.CORBA;

public final class BAD_PARAM extends SystemException
{
    public BAD_PARAM() {
        this("");
    }
    
    public BAD_PARAM(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public BAD_PARAM(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public BAD_PARAM(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
