package javax.naming.directory;

import javax.naming.NamingException;

public class InvalidAttributeIdentifierException extends NamingException
{
    private static final long serialVersionUID = -9036920266322999923L;
    
    public InvalidAttributeIdentifierException(final String s) {
        super(s);
    }
    
    public InvalidAttributeIdentifierException() {
    }
}
