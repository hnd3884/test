package sun.applet;

import java.awt.MenuBar;
import java.util.Hashtable;
import java.net.URL;

public interface AppletViewerFactory
{
    AppletViewer createAppletViewer(final int p0, final int p1, final URL p2, final Hashtable p3);
    
    MenuBar getBaseMenuBar();
    
    boolean isStandalone();
}
