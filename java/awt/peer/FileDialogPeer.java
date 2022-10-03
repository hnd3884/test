package java.awt.peer;

import java.io.FilenameFilter;

public interface FileDialogPeer extends DialogPeer
{
    void setFile(final String p0);
    
    void setDirectory(final String p0);
    
    void setFilenameFilter(final FilenameFilter p0);
}
