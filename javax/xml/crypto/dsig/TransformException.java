package javax.xml.crypto.dsig;

import java.io.PrintWriter;
import java.io.PrintStream;

public class TransformException extends Exception
{
    private static final long serialVersionUID = 5082634801360427800L;
    private Throwable cause;
    
    public TransformException() {
    }
    
    public TransformException(final String s) {
        super(s);
    }
    
    public TransformException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public TransformException(final Throwable cause) {
        super((cause == null) ? null : cause.toString());
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    public void printStackTrace() {
        super.printStackTrace();
        if (this.cause != null) {
            this.cause.printStackTrace();
        }
    }
    
    public void printStackTrace(final PrintStream printStream) {
        super.printStackTrace(printStream);
        if (this.cause != null) {
            this.cause.printStackTrace(printStream);
        }
    }
    
    public void printStackTrace(final PrintWriter printWriter) {
        super.printStackTrace(printWriter);
        if (this.cause != null) {
            this.cause.printStackTrace(printWriter);
        }
    }
}
