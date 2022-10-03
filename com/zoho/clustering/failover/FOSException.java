package com.zoho.clustering.failover;

public class FOSException extends RuntimeException
{
    private ErrorCode errCode;
    
    public static void main(final String[] args) {
        try {
            testMeth();
            System.out.print("\nOVER");
        }
        catch (final FOSException exp) {
            exp.printStackTrace();
        }
    }
    
    private static void testMeth() {
        try {
            Integer.parseInt("");
        }
        catch (final RuntimeException exp) {
            throw new FOSException(ErrorCode.ERROR_GENERAL, exp.getMessage());
        }
    }
    
    public FOSException(final ErrorCode errCode, final String message) {
        super(message);
        this.errCode = errCode;
    }
    
    public ErrorCode getErrCode() {
        return this.errCode;
    }
    
    @Override
    public String getMessage() {
        final String msg = super.getMessage();
        return this.getErrCode() + ". " + ((msg == null) ? "" : msg);
    }
}
