package org.apache.tomcat.util.http.fileupload.impl;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

public class InvalidContentTypeException extends FileUploadException
{
    private static final long serialVersionUID = -9073026332015646668L;
    
    public InvalidContentTypeException() {
    }
    
    public InvalidContentTypeException(final String message) {
        super(message);
    }
    
    public InvalidContentTypeException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
