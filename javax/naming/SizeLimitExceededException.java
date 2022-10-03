package javax.naming;

public class SizeLimitExceededException extends LimitExceededException
{
    private static final long serialVersionUID = 7129289564879168579L;
    
    public SizeLimitExceededException() {
    }
    
    public SizeLimitExceededException(final String s) {
        super(s);
    }
}
