package org.apache.tomcat.util.http.fileupload.disk;

import org.apache.tomcat.util.http.fileupload.FileItem;
import java.io.File;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;

public class DiskFileItemFactory implements FileItemFactory
{
    public static final int DEFAULT_SIZE_THRESHOLD = 10240;
    private File repository;
    private int sizeThreshold;
    private String defaultCharset;
    
    public DiskFileItemFactory() {
        this(10240, null);
    }
    
    public DiskFileItemFactory(final int sizeThreshold, final File repository) {
        this.sizeThreshold = 10240;
        this.defaultCharset = "ISO-8859-1";
        this.sizeThreshold = sizeThreshold;
        this.repository = repository;
    }
    
    public File getRepository() {
        return this.repository;
    }
    
    public void setRepository(final File repository) {
        this.repository = repository;
    }
    
    public int getSizeThreshold() {
        return this.sizeThreshold;
    }
    
    public void setSizeThreshold(final int sizeThreshold) {
        this.sizeThreshold = sizeThreshold;
    }
    
    @Override
    public FileItem createItem(final String fieldName, final String contentType, final boolean isFormField, final String fileName) {
        final DiskFileItem result = new DiskFileItem(fieldName, contentType, isFormField, fileName, this.sizeThreshold, this.repository);
        result.setDefaultCharset(this.defaultCharset);
        return result;
    }
    
    public String getDefaultCharset() {
        return this.defaultCharset;
    }
    
    public void setDefaultCharset(final String pCharset) {
        this.defaultCharset = pCharset;
    }
}
