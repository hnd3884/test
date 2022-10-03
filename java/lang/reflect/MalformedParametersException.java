package java.lang.reflect;

public class MalformedParametersException extends RuntimeException
{
    private static final long serialVersionUID = 20130919L;
    
    public MalformedParametersException() {
    }
    
    public MalformedParametersException(final String s) {
        super(s);
    }
}
