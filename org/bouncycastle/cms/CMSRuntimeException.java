package org.bouncycastle.cms;

public class CMSRuntimeException extends RuntimeException
{
    Exception e;
    
    public CMSRuntimeException(final String s) {
        super(s);
    }
    
    public CMSRuntimeException(final String s, final Exception e) {
        super(s);
        this.e = e;
    }
    
    public Exception getUnderlyingException() {
        return this.e;
    }
    
    @Override
    public Throwable getCause() {
        return this.e;
    }
}
