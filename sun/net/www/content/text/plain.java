package sun.net.www.content.text;

import java.io.IOException;
import java.net.URLConnection;
import java.net.ContentHandler;

public class plain extends ContentHandler
{
    @Override
    public Object getContent(final URLConnection urlConnection) {
        try {
            urlConnection.getInputStream();
            return new PlainTextInputStream(urlConnection.getInputStream());
        }
        catch (final IOException ex) {
            return "Error reading document:\n" + ex.toString();
        }
    }
}
