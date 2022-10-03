package sun.net.www.protocol.http;

import java.net.URISyntaxException;
import java.net.URI;
import sun.net.www.HeaderParser;
import java.net.URL;
import java.util.Base64;
import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;

class BasicAuthentication extends AuthenticationInfo
{
    private static final long serialVersionUID = 100L;
    String auth;
    
    public BasicAuthentication(final boolean b, final String s, final int n, final String s2, final PasswordAuthentication pw) {
        super(b ? 'p' : 's', AuthScheme.BASIC, s, n, s2);
        final String string = pw.getUserName() + ":";
        Object bytes = null;
        try {
            bytes = string.getBytes("ISO-8859-1");
        }
        catch (final UnsupportedEncodingException ex) {
            assert false;
        }
        final char[] password = pw.getPassword();
        final byte[] array = new byte[password.length];
        for (int i = 0; i < password.length; ++i) {
            array[i] = (byte)password[i];
        }
        final byte[] array2 = new byte[bytes.length + array.length];
        System.arraycopy(bytes, 0, array2, 0, bytes.length);
        System.arraycopy(array, 0, array2, bytes.length, array.length);
        this.auth = "Basic " + Base64.getEncoder().encodeToString(array2);
        this.pw = pw;
    }
    
    public BasicAuthentication(final boolean b, final String s, final int n, final String s2, final String s3) {
        super(b ? 'p' : 's', AuthScheme.BASIC, s, n, s2);
        this.auth = "Basic " + s3;
    }
    
    public BasicAuthentication(final boolean b, final URL url, final String s, final PasswordAuthentication pw) {
        super(b ? 'p' : 's', AuthScheme.BASIC, url, s);
        final String string = pw.getUserName() + ":";
        Object bytes = null;
        try {
            bytes = string.getBytes("ISO-8859-1");
        }
        catch (final UnsupportedEncodingException ex) {
            assert false;
        }
        final char[] password = pw.getPassword();
        final byte[] array = new byte[password.length];
        for (int i = 0; i < password.length; ++i) {
            array[i] = (byte)password[i];
        }
        final byte[] array2 = new byte[bytes.length + array.length];
        System.arraycopy(bytes, 0, array2, 0, bytes.length);
        System.arraycopy(array, 0, array2, bytes.length, array.length);
        this.auth = "Basic " + Base64.getEncoder().encodeToString(array2);
        this.pw = pw;
    }
    
    public BasicAuthentication(final boolean b, final URL url, final String s, final String s2) {
        super(b ? 'p' : 's', AuthScheme.BASIC, url, s);
        this.auth = "Basic " + s2;
    }
    
    @Override
    public boolean supportsPreemptiveAuthorization() {
        return true;
    }
    
    @Override
    public boolean setHeaders(final HttpURLConnection httpURLConnection, final HeaderParser headerParser, final String s) {
        httpURLConnection.setAuthenticationProperty(this.getHeaderName(), this.getHeaderValue(null, null));
        return true;
    }
    
    @Override
    public String getHeaderValue(final URL url, final String s) {
        return this.auth;
    }
    
    @Override
    public boolean isAuthorizationStale(final String s) {
        return false;
    }
    
    static String getRootPath(String path, String path2) {
        int i = 0;
        try {
            path = new URI(path).normalize().getPath();
            path2 = new URI(path2).normalize().getPath();
        }
        catch (final URISyntaxException ex) {}
        while (i < path2.length()) {
            final int index = path2.indexOf(47, i + 1);
            if (index == -1 || !path2.regionMatches(0, path, 0, index + 1)) {
                return path2.substring(0, i + 1);
            }
            i = index;
        }
        return path;
    }
}
