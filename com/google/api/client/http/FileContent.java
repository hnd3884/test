package com.google.api.client.http;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import com.google.api.client.util.Preconditions;
import java.io.File;

public final class FileContent extends AbstractInputStreamContent
{
    private final File file;
    
    public FileContent(final String type, final File file) {
        super(type);
        this.file = Preconditions.checkNotNull(file);
    }
    
    @Override
    public long getLength() {
        return this.file.length();
    }
    
    @Override
    public boolean retrySupported() {
        return true;
    }
    
    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(this.file);
    }
    
    public File getFile() {
        return this.file;
    }
    
    @Override
    public FileContent setType(final String type) {
        return (FileContent)super.setType(type);
    }
    
    @Override
    public FileContent setCloseInputStream(final boolean closeInputStream) {
        return (FileContent)super.setCloseInputStream(closeInputStream);
    }
}
