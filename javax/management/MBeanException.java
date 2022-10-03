package javax.management;

public class MBeanException extends JMException
{
    private static final long serialVersionUID = 4066342430588744142L;
    private Exception exception;
    
    public MBeanException(final Exception exception) {
        this.exception = exception;
    }
    
    public MBeanException(final Exception exception, final String s) {
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
