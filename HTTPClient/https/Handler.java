package HTTPClient.https;

import java.io.IOException;
import HTTPClient.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
import HTTPClient.ProtocolNotSuppException;
import HTTPClient.HTTPConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler
{
    public Handler() throws ProtocolNotSuppException {
        new HTTPConnection("https", "", -1);
    }
    
    public URLConnection openConnection(final URL url) throws IOException, ProtocolNotSuppException {
        return new HttpURLConnection(url);
    }
}
