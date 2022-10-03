package org.apache.taglibs.standard.lang.jstl;

public class ELException extends Exception
{
    Throwable mRootCause;
    
    public ELException() {
    }
    
    public ELException(final String pMessage) {
        super(pMessage);
    }
    
    public ELException(final Throwable pRootCause) {
        this.mRootCause = pRootCause;
    }
    
    public ELException(final String pMessage, final Throwable pRootCause) {
        super(pMessage);
        this.mRootCause = pRootCause;
    }
    
    public Throwable getRootCause() {
        return this.mRootCause;
    }
    
    @Override
    public String toString() {
        if (this.getMessage() == null) {
            return this.mRootCause.toString();
        }
        if (this.mRootCause == null) {
            return this.getMessage();
        }
        return this.getMessage() + ": " + this.mRootCause;
    }
}
