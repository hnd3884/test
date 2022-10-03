package java.rmi.activation;

public class ActivationException extends Exception
{
    public Throwable detail;
    private static final long serialVersionUID = -4320118837291406071L;
    
    public ActivationException() {
        this.initCause(null);
    }
    
    public ActivationException(final String s) {
        super(s);
        this.initCause(null);
    }
    
    public ActivationException(final String s, final Throwable detail) {
        super(s);
        this.initCause(null);
        this.detail = detail;
    }
    
    @Override
    public String getMessage() {
        if (this.detail == null) {
            return super.getMessage();
        }
        return super.getMessage() + "; nested exception is: \n\t" + this.detail.toString();
    }
    
    @Override
    public Throwable getCause() {
        return this.detail;
    }
}
