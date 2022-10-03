package org.apache.tomcat.util.http.fileupload;

import java.util.List;
import java.io.IOException;

public interface FileItemIterator
{
    long getFileSizeMax();
    
    void setFileSizeMax(final long p0);
    
    long getSizeMax();
    
    void setSizeMax(final long p0);
    
    boolean hasNext() throws FileUploadException, IOException;
    
    FileItemStream next() throws FileUploadException, IOException;
    
    List<FileItem> getFileItems() throws FileUploadException, IOException;
}
