package jcifs.smb;

import java.io.IOException;
import jcifs.Config;
import java.net.URLConnection;
import java.net.URL;
import java.io.UnsupportedEncodingException;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler
{
    static final URLStreamHandler SMB_HANDLER;
    
    static String unescape(final String str) throws NumberFormatException, UnsupportedEncodingException {
        final byte[] b = { 0 };
        if (str == null) {
            return null;
        }
        final int len = str.length();
        final char[] out = new char[len];
        int state = 0;
        int i;
        int j;
        for (j = (i = 0); i < len; ++i) {
            switch (state) {
                case 0: {
                    final char ch = str.charAt(i);
                    if (ch == '%') {
                        state = 1;
                        break;
                    }
                    out[j++] = ch;
                    break;
                }
                case 1: {
                    b[0] = (byte)(Integer.parseInt(str.substring(i, i + 2), 16) & 0xFF);
                    out[j++] = new String(b, 0, 1, "ASCII").charAt(0);
                    ++i;
                    state = 0;
                    break;
                }
            }
        }
        return new String(out, 0, j);
    }
    
    protected int getDefaultPort() {
        return 445;
    }
    
    public URLConnection openConnection(final URL u) throws IOException {
        return new SmbFile(u, Config.getOpneConnectionAuthenticator());
    }
    
    protected void parseURL(final URL u, String spec, final int start, int limit) {
        final String host = u.getHost();
        if (spec.equals("smb://")) {
            spec = "smb:////";
            limit += 2;
        }
        else if (!spec.startsWith("smb://") && host != null && host.length() == 0) {
            spec = "//" + spec;
            limit += 2;
        }
        super.parseURL(u, spec, start, limit);
        String userinfo = u.getUserInfo();
        String path = u.getPath();
        final String ref = u.getRef();
        try {
            userinfo = unescape(userinfo);
        }
        catch (final UnsupportedEncodingException ex) {}
        if (ref != null) {
            path = path + '#' + ref;
        }
        int port = u.getPort();
        if (port == -1) {
            port = this.getDefaultPort();
        }
        this.setURL(u, "smb", u.getHost(), port, u.getAuthority(), userinfo, path, u.getQuery(), null);
    }
    
    static {
        SMB_HANDLER = new Handler();
    }
}
