package java.lang.instrument;

public class UnmodifiableClassException extends Exception
{
    private static final long serialVersionUID = 1716652643585309178L;
    
    public UnmodifiableClassException() {
    }
    
    public UnmodifiableClassException(final String s) {
        super(s);
    }
}
