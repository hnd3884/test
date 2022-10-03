package org.apache.poi.ss.usermodel;

import org.apache.poi.poifs.filesystem.DirectoryEntry;
import java.io.IOException;

public interface ObjectData extends SimpleShape
{
    byte[] getObjectData() throws IOException;
    
    boolean hasDirectoryEntry();
    
    DirectoryEntry getDirectory() throws IOException;
    
    String getOLE2ClassName();
    
    String getFileName();
    
    PictureData getPictureData();
    
    default String getContentType() {
        return "binary/octet-stream";
    }
}
