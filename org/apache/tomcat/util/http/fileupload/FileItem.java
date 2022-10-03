package org.apache.tomcat.util.http.fileupload;

import java.io.OutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.InputStream;

public interface FileItem extends FileItemHeadersSupport
{
    InputStream getInputStream() throws IOException;
    
    String getContentType();
    
    String getName();
    
    boolean isInMemory();
    
    long getSize();
    
    byte[] get() throws IOException;
    
    String getString(final String p0) throws UnsupportedEncodingException, IOException;
    
    String getString();
    
    void write(final File p0) throws Exception;
    
    void delete();
    
    String getFieldName();
    
    void setFieldName(final String p0);
    
    boolean isFormField();
    
    void setFormField(final boolean p0);
    
    OutputStream getOutputStream() throws IOException;
}
