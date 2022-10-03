package jcifs.util.transport;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;

public class TransportException extends IOException
{
    private Throwable rootCause;
    
    public TransportException() {
    }
    
    public TransportException(final String msg) {
        super(msg);
    }
    
    public TransportException(final Throwable rootCause) {
        this.rootCause = rootCause;
    }
    
    public TransportException(final String msg, final Throwable rootCause) {
        super(msg);
        this.rootCause = rootCause;
    }
    
    public Throwable getRootCause() {
        return this.rootCause;
    }
    
    public String toString() {
        if (this.rootCause != null) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            this.rootCause.printStackTrace(pw);
            return super.toString() + "\n" + sw;
        }
        return super.toString();
    }
}
