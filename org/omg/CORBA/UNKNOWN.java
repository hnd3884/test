package org.omg.CORBA;

public final class UNKNOWN extends SystemException
{
    public UNKNOWN() {
        this("");
    }
    
    public UNKNOWN(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public UNKNOWN(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public UNKNOWN(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
