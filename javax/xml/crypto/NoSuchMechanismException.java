package javax.xml.crypto;

import java.io.PrintWriter;
import java.io.PrintStream;

public class NoSuchMechanismException extends RuntimeException
{
    private static final long serialVersionUID = 4189669069570660166L;
    private Throwable cause;
    
    public NoSuchMechanismException() {
    }
    
    public NoSuchMechanismException(final String s) {
        super(s);
    }
    
    public NoSuchMechanismException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public NoSuchMechanismException(final Throwable cause) {
        super((cause == null) ? null : cause.toString());
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    public void printStackTrace() {
        super.printStackTrace();
    }
    
    public void printStackTrace(final PrintStream printStream) {
        super.printStackTrace(printStream);
    }
    
    public void printStackTrace(final PrintWriter printWriter) {
        super.printStackTrace(printWriter);
    }
}
