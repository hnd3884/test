package javax.transaction;

public class SystemException extends Exception
{
    public int errorCode;
    
    public SystemException() {
    }
    
    public SystemException(final String msg) {
        super(msg);
    }
    
    public SystemException(final int errcode) {
        this.errorCode = errcode;
    }
}
