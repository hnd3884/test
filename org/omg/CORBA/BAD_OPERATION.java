package org.omg.CORBA;

public final class BAD_OPERATION extends SystemException
{
    public BAD_OPERATION() {
        this("");
    }
    
    public BAD_OPERATION(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public BAD_OPERATION(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public BAD_OPERATION(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
