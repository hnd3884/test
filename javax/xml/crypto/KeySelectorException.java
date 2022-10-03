package javax.xml.crypto;

import java.io.PrintWriter;
import java.io.PrintStream;

public class KeySelectorException extends Exception
{
    private static final long serialVersionUID = -7480033639322531109L;
    private Throwable cause;
    
    public KeySelectorException() {
    }
    
    public KeySelectorException(final String s) {
        super(s);
    }
    
    public KeySelectorException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public KeySelectorException(final Throwable cause) {
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
