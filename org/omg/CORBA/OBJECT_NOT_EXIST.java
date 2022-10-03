package org.omg.CORBA;

public final class OBJECT_NOT_EXIST extends SystemException
{
    public OBJECT_NOT_EXIST() {
        this("");
    }
    
    public OBJECT_NOT_EXIST(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public OBJECT_NOT_EXIST(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public OBJECT_NOT_EXIST(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
