package org.apache.axiom.ext.io;

import java.io.IOException;

public class StreamCopyException extends IOException
{
    private static final long serialVersionUID = -6489101119109339448L;
    public static final int READ = 1;
    public static final int WRITE = 2;
    private final int operation;
    
    public StreamCopyException(final int operation, final IOException cause) {
        this.operation = operation;
        this.initCause(cause);
    }
    
    public int getOperation() {
        return this.operation;
    }
    
    @Override
    public String getMessage() {
        return (this.operation == 1) ? "Error reading from source" : "Error writing to destination";
    }
}
