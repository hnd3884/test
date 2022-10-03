package javax.ejb;

public class FinderException extends Exception
{
    public FinderException() {
    }
    
    public FinderException(final String message) {
        super(message);
    }
}
