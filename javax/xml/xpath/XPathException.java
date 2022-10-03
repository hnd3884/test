package javax.xml.xpath;

import java.io.PrintWriter;
import java.io.PrintStream;

public class XPathException extends Exception
{
    private final Throwable cause;
    private static final long serialVersionUID = -1837080260374986980L;
    
    public XPathException(final String s) {
        super(s);
        if (s == null) {
            throw new NullPointerException("message can't be null");
        }
        this.cause = null;
    }
    
    public XPathException(final Throwable cause) {
        super((cause == null) ? null : cause.toString());
        this.cause = cause;
        if (cause == null) {
            throw new NullPointerException("cause can't be null");
        }
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    public void printStackTrace(final PrintStream printStream) {
        if (this.getCause() != null) {
            this.getCause().printStackTrace(printStream);
            printStream.println("--------------- linked to ------------------");
        }
        super.printStackTrace(printStream);
    }
    
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
    
    public void printStackTrace(final PrintWriter printWriter) {
        if (this.getCause() != null) {
            this.getCause().printStackTrace(printWriter);
            printWriter.println("--------------- linked to ------------------");
        }
        super.printStackTrace(printWriter);
    }
}
