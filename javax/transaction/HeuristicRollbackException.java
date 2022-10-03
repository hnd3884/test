package javax.transaction;

public class HeuristicRollbackException extends Exception
{
    public HeuristicRollbackException() {
    }
    
    public HeuristicRollbackException(final String msg) {
        super(msg);
    }
}
