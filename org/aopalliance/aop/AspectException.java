package org.aopalliance.aop;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AspectException extends RuntimeException
{
    private String message;
    private String stackTrace;
    private Throwable t;
    
    public AspectException(final String s) {
        super(s);
        this.message = s;
        this.stackTrace = s;
    }
    
    public AspectException(final String s, final Throwable t) {
        super(s + "; nested exception is " + t.getMessage());
        this.t = t;
        final StringWriter out = new StringWriter();
        t.printStackTrace(new PrintWriter(out));
        this.stackTrace = out.toString();
    }
    
    @Override
    public Throwable getCause() {
        return this.t;
    }
    
    @Override
    public String toString() {
        return this.getMessage();
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public void printStackTrace() {
        System.err.print(this.stackTrace);
    }
    
    @Override
    public void printStackTrace(final PrintStream out) {
        this.printStackTrace(new PrintWriter(out));
    }
    
    @Override
    public void printStackTrace(final PrintWriter out) {
        out.print(this.stackTrace);
    }
}
