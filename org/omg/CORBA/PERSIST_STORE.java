package org.omg.CORBA;

public final class PERSIST_STORE extends SystemException
{
    public PERSIST_STORE() {
        this("");
    }
    
    public PERSIST_STORE(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public PERSIST_STORE(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public PERSIST_STORE(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
