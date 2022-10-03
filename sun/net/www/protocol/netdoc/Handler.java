package sun.net.www.protocol.netdoc;

import java.io.IOException;
import java.net.MalformedURLException;
import sun.security.action.GetPropertyAction;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler
{
    static URL base;
    
    public synchronized URLConnection openConnection(final URL url) throws IOException {
        URLConnection urlConnection = null;
        final boolean booleanValue = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("newdoc.localonly"));
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("doc.url"));
        final String file = url.getFile();
        if (!booleanValue) {
            URL url2;
            try {
                if (Handler.base == null) {
                    Handler.base = new URL(s);
                }
                url2 = new URL(Handler.base, file);
            }
            catch (final MalformedURLException ex) {
                url2 = null;
            }
            if (url2 != null) {
                urlConnection = url2.openConnection();
            }
        }
        if (urlConnection == null) {
            try {
                urlConnection = new URL("file", "~", file).openConnection();
                urlConnection.getInputStream();
            }
            catch (final MalformedURLException ex2) {
                urlConnection = null;
            }
            catch (final IOException ex3) {
                urlConnection = null;
            }
        }
        if (urlConnection == null) {
            throw new IOException("Can't find file for URL: " + url.toExternalForm());
        }
        return urlConnection;
    }
}
