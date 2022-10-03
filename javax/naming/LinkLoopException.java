package javax.naming;

public class LinkLoopException extends LinkException
{
    private static final long serialVersionUID = -3119189944325198009L;
    
    public LinkLoopException(final String s) {
        super(s);
    }
    
    public LinkLoopException() {
    }
}
