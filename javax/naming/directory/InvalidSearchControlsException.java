package javax.naming.directory;

import javax.naming.NamingException;

public class InvalidSearchControlsException extends NamingException
{
    private static final long serialVersionUID = -5124108943352665777L;
    
    public InvalidSearchControlsException() {
    }
    
    public InvalidSearchControlsException(final String s) {
        super(s);
    }
}
