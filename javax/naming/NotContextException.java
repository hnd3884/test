package javax.naming;

public class NotContextException extends NamingException
{
    private static final long serialVersionUID = 849752551644540417L;
    
    public NotContextException(final String s) {
        super(s);
    }
    
    public NotContextException() {
    }
}
