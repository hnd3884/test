package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;

public class FileUploadException extends IOException
{
    private static final long serialVersionUID = -4222909057964038517L;
    
    public FileUploadException() {
    }
    
    public FileUploadException(final String msg) {
        super(msg);
    }
    
    public FileUploadException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public FileUploadException(final Throwable cause) {
        super(cause);
    }
}
