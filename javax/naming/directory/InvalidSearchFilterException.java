package javax.naming.directory;

import javax.naming.NamingException;

public class InvalidSearchFilterException extends NamingException
{
    private static final long serialVersionUID = 2902700940682875441L;
    
    public InvalidSearchFilterException() {
    }
    
    public InvalidSearchFilterException(final String s) {
        super(s);
    }
}
