package com.me.devicemanagement.onpremise.webclient.file;

import org.apache.commons.fileupload.disk.DiskFileItem;
import java.io.File;
import com.adventnet.iam.security.UploadedFileItem;

public class UploadedFile extends UploadedFileItem
{
    public UploadedFile(final String fileName, final long fileSize, final File uploadedFile, final String fieldName, final String contentTypeFromRequest, final String contentTypeDetected, final DiskFileItem item) {
        super(fileName, fileSize, uploadedFile, fieldName, contentTypeFromRequest, contentTypeDetected, item);
    }
    
    public File getUploadedFile() {
        return super.getUploadedFileForValidation();
    }
}
