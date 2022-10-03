package java.applet;

import java.net.URL;

public interface AppletStub
{
    boolean isActive();
    
    URL getDocumentBase();
    
    URL getCodeBase();
    
    String getParameter(final String p0);
    
    AppletContext getAppletContext();
    
    void appletResize(final int p0, final int p1);
}
