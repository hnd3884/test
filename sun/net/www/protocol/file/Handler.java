package sun.net.www.protocol.file;

import sun.net.www.ParseUtil;
import java.io.IOException;
import java.net.Proxy;
import java.net.URLConnection;
import java.io.File;
import java.net.URL;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler
{
    private String getHost(final URL url) {
        String host = url.getHost();
        if (host == null) {
            host = "";
        }
        return host;
    }
    
    @Override
    protected void parseURL(final URL url, final String s, final int n, final int n2) {
        super.parseURL(url, s.replace(File.separatorChar, '/'), n, n2);
    }
    
    public synchronized URLConnection openConnection(final URL url) throws IOException {
        return this.openConnection(url, null);
    }
    
    public synchronized URLConnection openConnection(final URL url, final Proxy proxy) throws IOException {
        final String file = url.getFile();
        final String host = url.getHost();
        final String replace = ParseUtil.decode(file).replace('/', '\\').replace('|', ':');
        if (host == null || host.equals("") || host.equalsIgnoreCase("localhost") || host.equals("~")) {
            return this.createFileURLConnection(url, new File(replace));
        }
        final String string = "\\\\" + host + replace;
        final File file2 = new File(string);
        if (file2.exists()) {
            return new UNCFileURLConnection(url, file2, string);
        }
        URLConnection urlConnection;
        try {
            final URL url2 = new URL("ftp", host, file + ((url.getRef() == null) ? "" : ("#" + url.getRef())));
            if (proxy != null) {
                urlConnection = url2.openConnection(proxy);
            }
            else {
                urlConnection = url2.openConnection();
            }
        }
        catch (final IOException ex) {
            urlConnection = null;
        }
        if (urlConnection == null) {
            throw new IOException("Unable to connect to: " + url.toExternalForm());
        }
        return urlConnection;
    }
    
    protected URLConnection createFileURLConnection(final URL url, final File file) {
        return new FileURLConnection(url, file);
    }
    
    @Override
    protected boolean hostsEqual(final URL url, final URL url2) {
        final String host = url.getHost();
        final String host2 = url2.getHost();
        return ("localhost".equalsIgnoreCase(host) && (host2 == null || "".equals(host2))) || ("localhost".equalsIgnoreCase(host2) && (host == null || "".equals(host))) || super.hostsEqual(url, url2);
    }
}
