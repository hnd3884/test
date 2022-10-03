package org.jfree.util;

import java.io.PrintWriter;
import java.io.PrintStream;

public abstract class StackableException extends Exception
{
    private Exception parent;
    
    public StackableException() {
    }
    
    public StackableException(final String message) {
        super(message);
    }
    
    public StackableException(final String message, final Exception ex) {
        super(message);
        this.parent = ex;
    }
    
    public Exception getParent() {
        return this.parent;
    }
    
    public void printStackTrace() {
        synchronized (System.err) {
            this.printStackTrace(System.err);
            monitorexit(System.err);
        }
    }
    
    public void printStackTrace(final PrintStream stream) {
        super.printStackTrace(stream);
        if (this.getParent() != null) {
            stream.println("ParentException: ");
            this.getParent().printStackTrace(stream);
        }
    }
    
    public void printStackTrace(final PrintWriter writer) {
        super.printStackTrace(writer);
        if (this.getParent() != null) {
            writer.println("ParentException: ");
            this.getParent().printStackTrace(writer);
        }
    }
}
