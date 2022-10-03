package java.awt.peer;

import java.net.URI;
import java.io.IOException;
import java.io.File;
import java.awt.Desktop;

public interface DesktopPeer
{
    boolean isSupported(final Desktop.Action p0);
    
    void open(final File p0) throws IOException;
    
    void edit(final File p0) throws IOException;
    
    void print(final File p0) throws IOException;
    
    void mail(final URI p0) throws IOException;
    
    void browse(final URI p0) throws IOException;
}
