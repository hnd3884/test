package javax.management;

public class RuntimeMBeanException extends JMRuntimeException
{
    private static final long serialVersionUID = 5274912751982730171L;
    private RuntimeException runtimeException;
    
    public RuntimeMBeanException(final RuntimeException runtimeException) {
        this.runtimeException = runtimeException;
    }
    
    public RuntimeMBeanException(final RuntimeException runtimeException, final String s) {
        super(s);
        this.runtimeException = runtimeException;
    }
    
    public RuntimeException getTargetException() {
        return this.runtimeException;
    }
    
    @Override
    public Throwable getCause() {
        return this.runtimeException;
    }
}
