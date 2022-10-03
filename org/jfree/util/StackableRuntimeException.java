package org.jfree.util;

import java.io.PrintWriter;
import java.io.PrintStream;

public class StackableRuntimeException extends RuntimeException
{
    private Exception parent;
    
    public StackableRuntimeException() {
    }
    
    public StackableRuntimeException(final String message) {
        super(message);
    }
    
    public StackableRuntimeException(final String message, final Exception ex) {
        super(message);
        this.parent = ex;
    }
    
    public Exception getParent() {
        return this.parent;
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
