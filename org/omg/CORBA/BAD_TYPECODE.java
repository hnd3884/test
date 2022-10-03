package org.omg.CORBA;

public final class BAD_TYPECODE extends SystemException
{
    public BAD_TYPECODE() {
        this("");
    }
    
    public BAD_TYPECODE(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public BAD_TYPECODE(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public BAD_TYPECODE(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
