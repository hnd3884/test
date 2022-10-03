package org.omg.CORBA;

public final class DATA_CONVERSION extends SystemException
{
    public DATA_CONVERSION() {
        this("");
    }
    
    public DATA_CONVERSION(final String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }
    
    public DATA_CONVERSION(final int n, final CompletionStatus completionStatus) {
        this("", n, completionStatus);
    }
    
    public DATA_CONVERSION(final String s, final int n, final CompletionStatus completionStatus) {
        super(s, n, completionStatus);
    }
}
