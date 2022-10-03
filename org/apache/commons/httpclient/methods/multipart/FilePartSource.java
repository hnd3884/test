package org.apache.commons.httpclient.methods.multipart;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.File;

public class FilePartSource implements PartSource
{
    private File file;
    private String fileName;
    
    public FilePartSource(final File file) throws FileNotFoundException {
        this.file = null;
        this.fileName = null;
        this.file = file;
        if (file != null) {
            if (!file.isFile()) {
                throw new FileNotFoundException("File is not a normal file.");
            }
            if (!file.canRead()) {
                throw new FileNotFoundException("File is not readable.");
            }
            this.fileName = file.getName();
        }
    }
    
    public FilePartSource(final String fileName, final File file) throws FileNotFoundException {
        this(file);
        if (fileName != null) {
            this.fileName = fileName;
        }
    }
    
    public long getLength() {
        if (this.file != null) {
            return this.file.length();
        }
        return 0L;
    }
    
    public String getFileName() {
        return (this.fileName == null) ? "noname" : this.fileName;
    }
    
    public InputStream createInputStream() throws IOException {
        if (this.file != null) {
            return new FileInputStream(this.file);
        }
        return new ByteArrayInputStream(new byte[0]);
    }
}
