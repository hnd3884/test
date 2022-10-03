package com.me.devicemanagement.framework.webclient.file;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public interface FormFile
{
    String getContentType();
    
    int getFileSize();
    
    String getFileName();
    
    byte[] getFileData() throws FileNotFoundException, IOException;
    
    InputStream getInputStream() throws FileNotFoundException, IOException;
    
    void destroy();
}
