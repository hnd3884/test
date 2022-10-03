package org.apache.commons.fileupload;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

@Deprecated
public class DiskFileUpload extends FileUploadBase
{
    private DefaultFileItemFactory fileItemFactory;
    
    @Deprecated
    public DiskFileUpload() {
        this.fileItemFactory = new DefaultFileItemFactory();
    }
    
    @Deprecated
    public DiskFileUpload(final DefaultFileItemFactory fileItemFactory) {
        this.fileItemFactory = fileItemFactory;
    }
    
    @Deprecated
    @Override
    public FileItemFactory getFileItemFactory() {
        return this.fileItemFactory;
    }
    
    @Deprecated
    @Override
    public void setFileItemFactory(final FileItemFactory factory) {
        this.fileItemFactory = (DefaultFileItemFactory)factory;
    }
    
    @Deprecated
    public int getSizeThreshold() {
        return this.fileItemFactory.getSizeThreshold();
    }
    
    @Deprecated
    public void setSizeThreshold(final int sizeThreshold) {
        this.fileItemFactory.setSizeThreshold(sizeThreshold);
    }
    
    @Deprecated
    public String getRepositoryPath() {
        return this.fileItemFactory.getRepository().getPath();
    }
    
    @Deprecated
    public void setRepositoryPath(final String repositoryPath) {
        this.fileItemFactory.setRepository(new File(repositoryPath));
    }
    
    @Deprecated
    public List<FileItem> parseRequest(final HttpServletRequest req, final int sizeThreshold, final long sizeMax, final String path) throws FileUploadException {
        this.setSizeThreshold(sizeThreshold);
        this.setSizeMax(sizeMax);
        this.setRepositoryPath(path);
        return this.parseRequest(req);
    }
}
