package javax.management.openmbean;

public class InvalidKeyException extends IllegalArgumentException
{
    private static final long serialVersionUID = 4224269443946322062L;
    
    public InvalidKeyException() {
    }
    
    public InvalidKeyException(final String s) {
        super(s);
    }
}
