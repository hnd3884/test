package javax.mail;

public class MessageRemovedException extends MessagingException
{
    private static final long serialVersionUID = 1951292550679528690L;
    
    public MessageRemovedException() {
    }
    
    public MessageRemovedException(final String s) {
        super(s);
    }
    
    public MessageRemovedException(final String s, final Exception e) {
        super(s, e);
    }
}
