package org.apache.tomcat.util.http.fileupload.impl;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import java.io.IOException;

public class FileUploadIOException extends IOException
{
    private static final long serialVersionUID = -7047616958165584154L;
    private final FileUploadException cause;
    
    public FileUploadIOException(final FileUploadException pCause) {
        this.cause = pCause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
