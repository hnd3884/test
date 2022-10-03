package org.apache.tomcat.util.http.fileupload.impl;

import java.io.IOException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;

public class IOFileUploadException extends FileUploadException
{
    private static final long serialVersionUID = 1749796615868477269L;
    private final IOException cause;
    
    public IOFileUploadException(final String pMsg, final IOException pException) {
        super(pMsg);
        this.cause = pException;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
