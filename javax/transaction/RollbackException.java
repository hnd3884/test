package javax.transaction;

public class RollbackException extends Exception
{
    public RollbackException() {
    }
    
    public RollbackException(final String msg) {
        super(msg);
    }
}
