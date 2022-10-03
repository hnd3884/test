package javax.naming;

public class MalformedLinkException extends LinkException
{
    private static final long serialVersionUID = -3066740437737830242L;
    
    public MalformedLinkException(final String s) {
        super(s);
    }
    
    public MalformedLinkException() {
    }
}
