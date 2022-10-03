package com.adventnet.webclient;

import java.io.PrintWriter;
import java.io.PrintStream;

public class ClientException extends Exception
{
    public Throwable rootCause;
    
    public ClientException() {
    }
    
    public ClientException(final String message) {
        super(message);
    }
    
    public ClientException(final String message, final Throwable ex) {
        super(message);
        this.rootCause = ex;
    }
    
    public String getMessage() {
        if (this.rootCause == null) {
            return super.getMessage();
        }
        return super.getMessage() + "; nested exception is: \n\t" + this.rootCause.toString();
    }
    
    public void printStackTrace(final PrintStream ps) {
        if (this.rootCause == null) {
            super.printStackTrace(ps);
        }
        else {
            synchronized (ps) {
                ps.println(this);
                this.rootCause.printStackTrace(ps);
            }
        }
    }
    
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
    
    public void printStackTrace(final PrintWriter pw) {
        if (this.rootCause == null) {
            super.printStackTrace(pw);
        }
        else {
            synchronized (pw) {
                pw.println(this);
                this.rootCause.printStackTrace(pw);
            }
        }
    }
}
