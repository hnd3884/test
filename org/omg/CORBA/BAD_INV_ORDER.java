package org.omg.CORBA;

public final class BAD_INV_ORDER extends SystemException
{
    public BAD_INV_ORDER() {
        this("");
    }
    
    public BAD_INV_ORDER(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public BAD_INV_ORDER(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public BAD_INV_ORDER(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
