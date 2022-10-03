package org.apache.commons.fileupload;

import java.io.PrintWriter;
import java.io.PrintStream;

public class FileUploadException extends Exception
{
    private static final long serialVersionUID = 8881893724388807504L;
    private final Throwable cause;
    
    public FileUploadException() {
        this(null, null);
    }
    
    public FileUploadException(final String msg) {
        this(msg, null);
    }
    
    public FileUploadException(final String msg, final Throwable cause) {
        super(msg);
        this.cause = cause;
    }
    
    @Override
    public void printStackTrace(final PrintStream stream) {
        super.printStackTrace(stream);
        if (this.cause != null) {
            stream.println("Caused by:");
            this.cause.printStackTrace(stream);
        }
    }
    
    @Override
    public void printStackTrace(final PrintWriter writer) {
        super.printStackTrace(writer);
        if (this.cause != null) {
            writer.println("Caused by:");
            this.cause.printStackTrace(writer);
        }
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
