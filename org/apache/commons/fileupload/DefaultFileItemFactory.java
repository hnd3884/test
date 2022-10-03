package org.apache.commons.fileupload;

import java.io.File;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

@Deprecated
public class DefaultFileItemFactory extends DiskFileItemFactory
{
    @Deprecated
    public DefaultFileItemFactory() {
    }
    
    @Deprecated
    public DefaultFileItemFactory(final int sizeThreshold, final File repository) {
        super(sizeThreshold, repository);
    }
    
    @Deprecated
    @Override
    public FileItem createItem(final String fieldName, final String contentType, final boolean isFormField, final String fileName) {
        return new DefaultFileItem(fieldName, contentType, isFormField, fileName, this.getSizeThreshold(), this.getRepository());
    }
}
