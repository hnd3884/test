package javax.ejb;

public class ObjectNotFoundException extends FinderException
{
    public ObjectNotFoundException() {
    }
    
    public ObjectNotFoundException(final String message) {
        super(message);
    }
}
