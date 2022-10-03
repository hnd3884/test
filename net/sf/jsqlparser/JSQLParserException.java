package net.sf.jsqlparser;

import java.io.PrintStream;
import java.io.PrintWriter;

public class JSQLParserException extends Exception
{
    private static final long serialVersionUID = -1099039459759769980L;
    private Throwable cause;
    
    public JSQLParserException() {
        this.cause = null;
    }
    
    public JSQLParserException(final String arg0) {
        super(arg0);
        this.cause = null;
    }
    
    public JSQLParserException(final Throwable arg0) {
        this.cause = null;
        this.cause = arg0;
    }
    
    public JSQLParserException(final String arg0, final Throwable arg1) {
        super(arg0);
        this.cause = null;
        this.cause = arg1;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
    
    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
    
    @Override
    public void printStackTrace(final PrintWriter pw) {
        super.printStackTrace(pw);
        if (this.cause != null) {
            pw.println("Caused by:");
            this.cause.printStackTrace(pw);
        }
    }
    
    @Override
    public void printStackTrace(final PrintStream ps) {
        super.printStackTrace(ps);
        if (this.cause != null) {
            ps.println("Caused by:");
            this.cause.printStackTrace(ps);
        }
    }
}
