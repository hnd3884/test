package org.apache.commons.collections;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.PrintStream;

public class FunctorException extends RuntimeException
{
    private static final boolean JDK_SUPPORTS_NESTED;
    private final Throwable rootCause;
    
    public FunctorException() {
        this.rootCause = null;
    }
    
    public FunctorException(final String msg) {
        super(msg);
        this.rootCause = null;
    }
    
    public FunctorException(final Throwable rootCause) {
        super((rootCause == null) ? null : rootCause.getMessage());
        this.rootCause = rootCause;
    }
    
    public FunctorException(final String msg, final Throwable rootCause) {
        super(msg);
        this.rootCause = rootCause;
    }
    
    public Throwable getCause() {
        return this.rootCause;
    }
    
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
    
    public void printStackTrace(final PrintStream out) {
        synchronized (out) {
            final PrintWriter pw = new PrintWriter(out, false);
            this.printStackTrace(pw);
            pw.flush();
        }
    }
    
    public void printStackTrace(final PrintWriter out) {
        synchronized (out) {
            super.printStackTrace(out);
            if (this.rootCause != null && !FunctorException.JDK_SUPPORTS_NESTED) {
                out.print("Caused by: ");
                this.rootCause.printStackTrace(out);
            }
        }
    }
    
    static {
        boolean flag = false;
        try {
            Throwable.class.getDeclaredMethod("getCause", (Class[])new Class[0]);
            flag = true;
        }
        catch (final NoSuchMethodException ex) {
            flag = false;
        }
        JDK_SUPPORTS_NESTED = flag;
    }
}
