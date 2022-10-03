package sun.awt.image;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.Permission;
import java.io.IOException;
import java.net.SocketPermission;
import java.io.FilePermission;
import sun.net.util.URLUtil;
import java.net.URLConnection;
import java.net.URL;

public class URLImageSource extends InputStreamImageSource
{
    URL url;
    URLConnection conn;
    String actualHost;
    int actualPort;
    
    public URLImageSource(final URL url) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            try {
                final Permission connectPermission = URLUtil.getConnectPermission(url);
                if (connectPermission != null) {
                    try {
                        securityManager.checkPermission(connectPermission);
                    }
                    catch (final SecurityException ex) {
                        if (connectPermission instanceof FilePermission && connectPermission.getActions().indexOf("read") != -1) {
                            securityManager.checkRead(connectPermission.getName());
                        }
                        else {
                            if (!(connectPermission instanceof SocketPermission) || connectPermission.getActions().indexOf("connect") == -1) {
                                throw ex;
                            }
                            securityManager.checkConnect(url.getHost(), url.getPort());
                        }
                    }
                }
            }
            catch (final IOException ex2) {
                securityManager.checkConnect(url.getHost(), url.getPort());
            }
        }
        this.url = url;
    }
    
    public URLImageSource(final String s) throws MalformedURLException {
        this(new URL(null, s));
    }
    
    public URLImageSource(final URL url, final URLConnection conn) {
        this(url);
        this.conn = conn;
    }
    
    public URLImageSource(final URLConnection urlConnection) {
        this(urlConnection.getURL(), urlConnection);
    }
    
    @Override
    final boolean checkSecurity(final Object o, final boolean b) {
        if (this.actualHost != null) {
            try {
                final SecurityManager securityManager = System.getSecurityManager();
                if (securityManager != null) {
                    securityManager.checkConnect(this.actualHost, this.actualPort, o);
                }
            }
            catch (final SecurityException ex) {
                if (!b) {
                    throw ex;
                }
                return false;
            }
        }
        return true;
    }
    
    private synchronized URLConnection getConnection() throws IOException {
        URLConnection urlConnection;
        if (this.conn != null) {
            urlConnection = this.conn;
            this.conn = null;
        }
        else {
            urlConnection = this.url.openConnection();
        }
        return urlConnection;
    }
    
    @Override
    protected ImageDecoder getDecoder() {
        InputStream inputStream = null;
        URLConnection connection = null;
        String contentType;
        try {
            connection = this.getConnection();
            inputStream = connection.getInputStream();
            contentType = connection.getContentType();
            final URL url = connection.getURL();
            if (url != this.url && (!url.getHost().equals(this.url.getHost()) || url.getPort() != this.url.getPort())) {
                if (this.actualHost != null && (!this.actualHost.equals(url.getHost()) || this.actualPort != url.getPort())) {
                    throw new SecurityException("image moved!");
                }
                this.actualHost = url.getHost();
                this.actualPort = url.getPort();
            }
        }
        catch (final IOException ex) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex2) {}
            }
            else if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection)connection).disconnect();
            }
            return null;
        }
        ImageDecoder imageDecoder = this.decoderForType(inputStream, contentType);
        if (imageDecoder == null) {
            imageDecoder = this.getDecoder(inputStream);
        }
        if (imageDecoder == null) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex3) {}
            }
            else if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection)connection).disconnect();
            }
        }
        return imageDecoder;
    }
}
