package java.applet;

import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.awt.Image;
import java.net.URL;

public interface AppletContext
{
    AudioClip getAudioClip(final URL p0);
    
    Image getImage(final URL p0);
    
    Applet getApplet(final String p0);
    
    Enumeration<Applet> getApplets();
    
    void showDocument(final URL p0);
    
    void showDocument(final URL p0, final String p1);
    
    void showStatus(final String p0);
    
    void setStream(final String p0, final InputStream p1) throws IOException;
    
    InputStream getStream(final String p0);
    
    Iterator<String> getStreamKeys();
}
