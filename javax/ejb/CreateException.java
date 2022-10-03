package javax.ejb;

public class CreateException extends Exception
{
    public CreateException() {
    }
    
    public CreateException(final String message) {
        super(message);
    }
}
