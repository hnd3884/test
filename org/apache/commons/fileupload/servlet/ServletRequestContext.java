package org.apache.commons.fileupload.servlet;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.UploadContext;

public class ServletRequestContext implements UploadContext
{
    private final HttpServletRequest request;
    
    public ServletRequestContext(final HttpServletRequest request) {
        this.request = request;
    }
    
    @Override
    public String getCharacterEncoding() {
        return this.request.getCharacterEncoding();
    }
    
    @Override
    public String getContentType() {
        return this.request.getContentType();
    }
    
    @Deprecated
    @Override
    public int getContentLength() {
        return this.request.getContentLength();
    }
    
    @Override
    public long contentLength() {
        long size;
        try {
            size = Long.parseLong(this.request.getHeader("Content-length"));
        }
        catch (final NumberFormatException e) {
            size = this.request.getContentLength();
        }
        return size;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return (InputStream)this.request.getInputStream();
    }
    
    @Override
    public String toString() {
        return String.format("ContentLength=%s, ContentType=%s", this.contentLength(), this.getContentType());
    }
}
