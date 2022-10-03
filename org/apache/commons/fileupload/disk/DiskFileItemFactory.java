package org.apache.commons.fileupload.disk;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileCleaningTracker;
import java.io.File;
import org.apache.commons.fileupload.FileItemFactory;

public class DiskFileItemFactory implements FileItemFactory
{
    public static final int DEFAULT_SIZE_THRESHOLD = 10240;
    private File repository;
    private int sizeThreshold;
    private FileCleaningTracker fileCleaningTracker;
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
        final FileCleaningTracker tracker = this.getFileCleaningTracker();
        if (tracker != null) {
            tracker.track(result.getTempFile(), (Object)result);
        }
        return result;
    }
    
    public FileCleaningTracker getFileCleaningTracker() {
        return this.fileCleaningTracker;
    }
    
    public void setFileCleaningTracker(final FileCleaningTracker pTracker) {
        this.fileCleaningTracker = pTracker;
    }
    
    public String getDefaultCharset() {
        return this.defaultCharset;
    }
    
    public void setDefaultCharset(final String pCharset) {
        this.defaultCharset = pCharset;
    }
}
