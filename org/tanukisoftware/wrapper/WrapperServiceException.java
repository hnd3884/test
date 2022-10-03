package org.tanukisoftware.wrapper;

public class WrapperServiceException extends Exception
{
    private static final long serialVersionUID = 5163822791166376887L;
    private final int m_errorCode;
    
    WrapperServiceException(final int errorCode, final byte[] message) {
        super(new String(message));
        this.m_errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return this.m_errorCode;
    }
    
    public String toString() {
        return this.getClass().getName() + " " + this.getMessage() + WrapperManager.getRes().getString(" Error Code: ") + this.getErrorCode();
    }
}
