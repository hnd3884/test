package javax.ejb;

public class DuplicateKeyException extends CreateException
{
    public DuplicateKeyException() {
    }
    
    public DuplicateKeyException(final String message) {
        super(message);
    }
}
