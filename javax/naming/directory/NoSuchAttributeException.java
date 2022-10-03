package javax.naming.directory;

import javax.naming.NamingException;

public class NoSuchAttributeException extends NamingException
{
    private static final long serialVersionUID = 4836415647935888137L;
    
    public NoSuchAttributeException(final String s) {
        super(s);
    }
    
    public NoSuchAttributeException() {
    }
}
