package sun.net.www.content.audio;

import java.io.IOException;
import sun.applet.AppletAudioClip;
import java.net.URLConnection;
import java.net.ContentHandler;

public class aiff extends ContentHandler
{
    @Override
    public Object getContent(final URLConnection urlConnection) throws IOException {
        return new AppletAudioClip(urlConnection);
    }
}
