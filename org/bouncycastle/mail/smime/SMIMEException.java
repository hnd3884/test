package org.bouncycastle.mail.smime;

public class SMIMEException extends Exception
{
    Exception e;
    
    public SMIMEException(final String s) {
        super(s);
    }
    
    public SMIMEException(final String s, final Exception e) {
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
