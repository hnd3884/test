package javax.naming;

public class ContextNotEmptyException extends NamingException
{
    private static final long serialVersionUID = 1090963683348219877L;
    
    public ContextNotEmptyException(final String s) {
        super(s);
    }
    
    public ContextNotEmptyException() {
    }
}
