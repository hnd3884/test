package java.lang.instrument;

public class IllegalClassFormatException extends Exception
{
    private static final long serialVersionUID = -3841736710924794009L;
    
    public IllegalClassFormatException() {
    }
    
    public IllegalClassFormatException(final String s) {
        super(s);
    }
}
