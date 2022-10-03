package org.bouncycastle.cms;

public class CMSException extends Exception
{
    Exception e;
    
    public CMSException(final String s) {
        super(s);
    }
    
    public CMSException(final String s, final Exception e) {
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
