package javax.management.openmbean;

public class InvalidOpenTypeException extends IllegalArgumentException
{
    private static final long serialVersionUID = -2837312755412327534L;
    
    public InvalidOpenTypeException() {
    }
    
    public InvalidOpenTypeException(final String s) {
        super(s);
    }
}
