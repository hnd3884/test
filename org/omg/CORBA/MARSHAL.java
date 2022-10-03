package org.omg.CORBA;

public final class MARSHAL extends SystemException
{
    public MARSHAL() {
        this("");
    }
    
    public MARSHAL(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public MARSHAL(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public MARSHAL(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
