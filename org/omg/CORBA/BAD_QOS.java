package org.omg.CORBA;

public final class BAD_QOS extends SystemException
{
    public BAD_QOS() {
        this("");
    }
    
    public BAD_QOS(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public BAD_QOS(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public BAD_QOS(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
