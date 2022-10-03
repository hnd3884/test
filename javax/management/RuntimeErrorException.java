package javax.management;

public class RuntimeErrorException extends JMRuntimeException
{
    private static final long serialVersionUID = 704338937753949796L;
    private Error error;
    
    public RuntimeErrorException(final Error error) {
        this.error = error;
    }
    
    public RuntimeErrorException(final Error error, final String s) {
        super(s);
        this.error = error;
    }
    
    public Error getTargetError() {
        return this.error;
    }
    
    @Override
    public Throwable getCause() {
        return this.error;
    }
}
