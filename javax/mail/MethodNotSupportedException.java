package javax.mail;

public class MethodNotSupportedException extends MessagingException
{
    private static final long serialVersionUID = -3757386618726131322L;
    
    public MethodNotSupportedException() {
    }
    
    public MethodNotSupportedException(final String s) {
        super(s);
    }
    
    public MethodNotSupportedException(final String s, final Exception e) {
        super(s, e);
    }
}
