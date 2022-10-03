package org.apache.commons.fileupload;

public class FileUpload extends FileUploadBase
{
    private FileItemFactory fileItemFactory;
    
    public FileUpload() {
    }
    
    public FileUpload(final FileItemFactory fileItemFactory) {
        this.fileItemFactory = fileItemFactory;
    }
    
    @Override
    public FileItemFactory getFileItemFactory() {
        return this.fileItemFactory;
    }
    
    @Override
    public void setFileItemFactory(final FileItemFactory factory) {
        this.fileItemFactory = factory;
    }
}
