package sun.applet;

import sun.misc.Ref;
import java.awt.Image;
import java.net.URL;

public class AppletResourceLoader
{
    public static Image getImage(final URL url) {
        return AppletViewer.getCachedImage(url);
    }
    
    public static Ref getImageRef(final URL url) {
        return AppletViewer.getCachedImageRef(url);
    }
    
    public static void flushImages() {
        AppletViewer.flushImageCache();
    }
}
