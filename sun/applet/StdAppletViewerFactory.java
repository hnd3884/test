package sun.applet;

import java.awt.MenuBar;
import java.util.Hashtable;
import java.net.URL;

final class StdAppletViewerFactory implements AppletViewerFactory
{
    @Override
    public AppletViewer createAppletViewer(final int n, final int n2, final URL url, final Hashtable hashtable) {
        return new AppletViewer(n, n2, url, hashtable, System.out, this);
    }
    
    @Override
    public MenuBar getBaseMenuBar() {
        return new MenuBar();
    }
    
    @Override
    public boolean isStandalone() {
        return true;
    }
}
