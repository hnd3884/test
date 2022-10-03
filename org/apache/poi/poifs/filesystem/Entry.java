package org.apache.poi.poifs.filesystem;

public interface Entry
{
    String getName();
    
    boolean isDirectoryEntry();
    
    boolean isDocumentEntry();
    
    DirectoryEntry getParent();
    
    boolean delete();
    
    boolean renameTo(final String p0);
}
