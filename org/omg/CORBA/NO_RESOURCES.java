package org.omg.CORBA;

public final class NO_RESOURCES extends SystemException
{
    public NO_RESOURCES() {
        this("");
    }
    
    public NO_RESOURCES(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public NO_RESOURCES(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public NO_RESOURCES(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
