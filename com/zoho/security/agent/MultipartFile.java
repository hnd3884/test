package com.zoho.security.agent;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

public class MultipartFile
{
    private File file;
    private InputStream inputStream;
    private byte[] contentInBytes;
    private String fieldName;
    private String contentType;
    private String fileName;
    
    public MultipartFile(final String fieldName, final File file, final String mimeType) {
        this.file = null;
        this.fieldName = null;
        this.contentType = null;
        this.fieldName = fieldName;
        this.file = file;
        this.contentType = mimeType;
    }
    
    public MultipartFile(final String fieldName, final InputStream inputStream, final String fileName, final String mimeType) {
        this.file = null;
        this.fieldName = null;
        this.contentType = null;
        this.fieldName = fieldName;
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.contentType = mimeType;
    }
    
    public MultipartFile(final String fieldName, final byte[] contentInBytes, final String fileName, final String mimeType) {
        this.file = null;
        this.fieldName = null;
        this.contentType = null;
        this.fieldName = fieldName;
        this.contentInBytes = contentInBytes;
        this.fileName = fileName;
        this.contentType = mimeType;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public String getFileName() {
        return (this.fileName == null) ? this.file.getName() : this.fileName;
    }
    
    public InputStream getInputStream() throws FileNotFoundException {
        return (this.inputStream == null) ? new FileInputStream(this.file) : this.inputStream;
    }
    
    public byte[] getBytes() {
        return this.contentInBytes;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public static List<MultipartFile> toMultipleInstance(final List<File> files, final String type, final String fieldName) {
        final List<MultipartFile> array = new ArrayList<MultipartFile>();
        for (final File file : files) {
            final MultipartFile reqFile = new MultipartFile(fieldName, file, type);
            array.add(reqFile);
        }
        return array;
    }
}
