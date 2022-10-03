package org.omg.CORBA;

public final class INITIALIZE extends SystemException
{
    public INITIALIZE() {
        this("");
    }
    
    public INITIALIZE(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public INITIALIZE(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public INITIALIZE(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
