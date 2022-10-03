package javax.xml.crypto;

import java.io.PrintWriter;
import java.io.PrintStream;

public class URIReferenceException extends Exception
{
    private static final long serialVersionUID = 7173469703932561419L;
    private Throwable cause;
    private URIReference uriReference;
    
    public URIReferenceException() {
    }
    
    public URIReferenceException(final String s) {
        super(s);
    }
    
    public URIReferenceException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public URIReferenceException(final String s, final Throwable t, final URIReference uriReference) {
        this(s, t);
        if (uriReference == null) {
            throw new NullPointerException("uriReference cannot be null");
        }
        this.uriReference = uriReference;
    }
    
    public URIReferenceException(final Throwable cause) {
        super((cause == null) ? null : cause.toString());
        this.cause = cause;
    }
    
    public URIReference getURIReference() {
        return this.uriReference;
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
