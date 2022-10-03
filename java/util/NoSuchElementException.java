package java.util;

public class NoSuchElementException extends RuntimeException
{
    private static final long serialVersionUID = 6769829250639411880L;
    
    public NoSuchElementException() {
    }
    
    public NoSuchElementException(final String s) {
        super(s);
    }
}
