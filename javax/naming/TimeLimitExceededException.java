package javax.naming;

public class TimeLimitExceededException extends LimitExceededException
{
    private static final long serialVersionUID = -3597009011385034696L;
    
    public TimeLimitExceededException() {
    }
    
    public TimeLimitExceededException(final String s) {
        super(s);
    }
}
