package org.apache.commons.fileupload;

import java.io.File;
import org.apache.commons.fileupload.disk.DiskFileItem;

@Deprecated
public class DefaultFileItem extends DiskFileItem
{
    @Deprecated
    public DefaultFileItem(final String fieldName, final String contentType, final boolean isFormField, final String fileName, final int sizeThreshold, final File repository) {
        super(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
    }
}
