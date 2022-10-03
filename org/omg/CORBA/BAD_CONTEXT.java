package org.omg.CORBA;

public final class BAD_CONTEXT extends SystemException
{
    public BAD_CONTEXT() {
        this("");
    }
    
    public BAD_CONTEXT(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public BAD_CONTEXT(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public BAD_CONTEXT(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
