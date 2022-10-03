package javax.xml.crypto;

import java.io.PrintWriter;
import java.io.PrintStream;

public class MarshalException extends Exception
{
    private static final long serialVersionUID = -863185580332643547L;
    private Throwable cause;
    
    public MarshalException() {
    }
    
    public MarshalException(final String s) {
        super(s);
    }
    
    public MarshalException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public MarshalException(final Throwable cause) {
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
