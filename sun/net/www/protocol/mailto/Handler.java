package sun.net.www.protocol.mailto;

import java.net.URLConnection;
import java.net.URL;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler
{
    public synchronized URLConnection openConnection(final URL url) {
        return new MailToURLConnection(url);
    }
    
    public void parseURL(final URL url, final String s, final int n, final int n2) {
        final String protocol = url.getProtocol();
        final String s2 = "";
        final int port = url.getPort();
        String substring = "";
        if (n < n2) {
            substring = s.substring(n, n2);
        }
        boolean b = false;
        if (substring == null || substring.equals("")) {
            b = true;
        }
        else {
            boolean b2 = true;
            for (int i = 0; i < substring.length(); ++i) {
                if (!Character.isWhitespace(substring.charAt(i))) {
                    b2 = false;
                }
            }
            if (b2) {
                b = true;
            }
        }
        if (b) {
            throw new RuntimeException("No email address");
        }
        this.setURLHandler(url, protocol, s2, port, substring, null);
    }
    
    private void setURLHandler(final URL url, final String s, final String s2, final int n, final String s3, final String s4) {
        this.setURL(url, s, s2, n, s3, null);
    }
}
