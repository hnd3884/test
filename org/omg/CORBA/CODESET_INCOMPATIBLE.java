package org.omg.CORBA;

public final class CODESET_INCOMPATIBLE extends SystemException
{
    public CODESET_INCOMPATIBLE() {
        this("");
    }
    
    public CODESET_INCOMPATIBLE(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public CODESET_INCOMPATIBLE(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public CODESET_INCOMPATIBLE(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
