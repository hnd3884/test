package org.omg.CORBA;

public final class IMP_LIMIT extends SystemException
{
    public IMP_LIMIT() {
        this("");
    }
    
    public IMP_LIMIT(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public IMP_LIMIT(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public IMP_LIMIT(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
