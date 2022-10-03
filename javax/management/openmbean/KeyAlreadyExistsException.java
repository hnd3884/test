package javax.management.openmbean;

public class KeyAlreadyExistsException extends IllegalArgumentException
{
    private static final long serialVersionUID = 1845183636745282866L;
    
    public KeyAlreadyExistsException() {
    }
    
    public KeyAlreadyExistsException(final String s) {
        super(s);
    }
}
