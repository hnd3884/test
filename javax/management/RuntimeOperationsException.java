package javax.management;

public class RuntimeOperationsException extends JMRuntimeException
{
    private static final long serialVersionUID = -8408923047489133588L;
    private RuntimeException runtimeException;
    
    public RuntimeOperationsException(final RuntimeException runtimeException) {
        this.runtimeException = runtimeException;
    }
    
    public RuntimeOperationsException(final RuntimeException runtimeException, final String s) {
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
