package com.me.devicemanagement.onpremise.webclient.file;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import com.adventnet.iam.security.UploadedFileItem;
import java.io.File;
import com.me.devicemanagement.framework.webclient.file.FormFile;

public class FormFileWrapper implements FormFile
{
    private final File file;
    private String fileName;
    private String contentType;
    private long filesize;
    
    public FormFileWrapper(final UploadedFileItem uploadedFileItem) {
        this.file = uploadedFileItem.getUploadedFile();
        this.fileName = uploadedFileItem.getFileName();
        this.filesize = uploadedFileItem.getFileSize();
        this.contentType = uploadedFileItem.getRequestContentType();
    }
    
    public InputStream getInputStream() throws FileNotFoundException, IOException {
        return new FileInputStream(this.file);
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public int getFileSize() {
        return (int)this.filesize;
    }
    
    public byte[] getFileData() throws FileNotFoundException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
