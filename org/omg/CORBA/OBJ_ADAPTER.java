package org.omg.CORBA;

public final class OBJ_ADAPTER extends SystemException
{
    public OBJ_ADAPTER() {
        this("");
    }
    
    public OBJ_ADAPTER(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public OBJ_ADAPTER(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public OBJ_ADAPTER(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
