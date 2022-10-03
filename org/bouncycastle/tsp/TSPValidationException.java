package org.bouncycastle.tsp;

public class TSPValidationException extends TSPException
{
    private int failureCode;
    
    public TSPValidationException(final String s) {
        super(s);
        this.failureCode = -1;
    }
    
    public TSPValidationException(final String s, final int failureCode) {
        super(s);
        this.failureCode = -1;
        this.failureCode = failureCode;
    }
    
    public int getFailureCode() {
        return this.failureCode;
    }
}
