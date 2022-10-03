package sun.net.www.content.image;

import java.awt.image.ImageProducer;
import java.awt.Toolkit;
import java.awt.Image;
import java.io.IOException;
import sun.awt.image.URLImageSource;
import java.net.URLConnection;
import java.net.ContentHandler;

public class gif extends ContentHandler
{
    @Override
    public Object getContent(final URLConnection urlConnection) throws IOException {
        return new URLImageSource(urlConnection);
    }
    
    @Override
    public Object getContent(final URLConnection urlConnection, final Class[] array) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].isAssignableFrom(URLImageSource.class)) {
                return new URLImageSource(urlConnection);
            }
            if (array[i].isAssignableFrom(Image.class)) {
                return Toolkit.getDefaultToolkit().createImage(new URLImageSource(urlConnection));
            }
        }
        return null;
    }
}
