package javax.transaction;

public class HeuristicMixedException extends Exception
{
    public HeuristicMixedException() {
    }
    
    public HeuristicMixedException(final String msg) {
        super(msg);
    }
}
