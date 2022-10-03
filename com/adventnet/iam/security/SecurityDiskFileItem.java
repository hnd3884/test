package com.adventnet.iam.security;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import java.io.File;
import org.apache.commons.fileupload.disk.DiskFileItem;

class SecurityDiskFileItem extends DiskFileItem
{
    private final TempFileName tempFileName;
    private transient File tempFile;
    
    public SecurityDiskFileItem(final TempFileName srw, final String fieldName, final String contentType, final boolean isFormField, final String fileName, final int sizeThreshold, final File repository) {
        super(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
        this.tempFileName = srw;
    }
    
    protected File getTempFile() {
        if (this.tempFile == null) {
            this.tempFile = SecurityUtil.createTempFile(this.tempFileName);
        }
        return this.tempFile;
    }
    
    static DiskFileItem createDiskFileItem(final TempFileName tempFileName, final DiskFileItemFactory factory, final String fieldName, final String contentType, final boolean isFormField, final String fileName) {
        final DiskFileItem fileItem = new SecurityDiskFileItem(tempFileName, fieldName, contentType, isFormField, fileName, factory.getSizeThreshold(), factory.getRepository());
        fileItem.setDefaultCharset(factory.getDefaultCharset());
        return fileItem;
    }
}
