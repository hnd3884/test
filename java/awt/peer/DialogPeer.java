package java.awt.peer;

import java.awt.Window;
import java.util.List;

public interface DialogPeer extends WindowPeer
{
    void setTitle(final String p0);
    
    void setResizable(final boolean p0);
    
    void blockWindows(final List<Window> p0);
}
