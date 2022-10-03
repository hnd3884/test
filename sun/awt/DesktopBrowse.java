package sun.awt;

import java.net.URL;

public abstract class DesktopBrowse
{
    private static volatile DesktopBrowse mInstance;
    
    public static void setInstance(final DesktopBrowse mInstance) {
        if (DesktopBrowse.mInstance != null) {
            throw new IllegalStateException("DesktopBrowse instance has already been set.");
        }
        DesktopBrowse.mInstance = mInstance;
    }
    
    public static DesktopBrowse getInstance() {
        return DesktopBrowse.mInstance;
    }
    
    public abstract void browse(final URL p0);
}
