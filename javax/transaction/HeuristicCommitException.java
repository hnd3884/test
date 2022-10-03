package javax.transaction;

public class HeuristicCommitException extends Exception
{
    public HeuristicCommitException() {
    }
    
    public HeuristicCommitException(final String msg) {
        super(msg);
    }
}
