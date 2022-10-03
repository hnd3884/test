package javax.management;

public class ReflectionException extends JMException
{
    private static final long serialVersionUID = 9170809325636915553L;
    private Exception exception;
    
    public ReflectionException(final Exception exception) {
        this.exception = exception;
    }
    
    public ReflectionException(final Exception exception, final String s) {
        super(s);
        this.exception = exception;
    }
    
    public Exception getTargetException() {
        return this.exception;
    }
    
    @Override
    public Throwable getCause() {
        return this.exception;
    }
}
