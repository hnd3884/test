package sun.nio.fs;

import java.net.URISyntaxException;
import java.net.URI;

class WindowsUriSupport
{
    private static final String IPV6_LITERAL_SUFFIX = ".ipv6-literal.net";
    
    private WindowsUriSupport() {
    }
    
    private static URI toUri(final String s, final boolean b, final boolean b2) {
        String s2;
        String s3;
        if (b) {
            final int index = s.indexOf(92, 2);
            s2 = s.substring(2, index);
            s3 = s.substring(index).replace('\\', '/');
            if (s2.endsWith(".ipv6-literal.net")) {
                s2 = s2.substring(0, s2.length() - ".ipv6-literal.net".length()).replace('-', ':').replace('s', '%');
            }
        }
        else {
            s2 = "";
            s3 = "/" + s.replace('\\', '/');
        }
        if (b2) {
            s3 += "/";
        }
        try {
            return new URI("file", s2, s3, null);
        }
        catch (final URISyntaxException ex) {
            if (!b) {
                throw new AssertionError((Object)ex);
            }
            String s4 = "//" + s.replace('\\', '/');
            if (b2) {
                s4 += "/";
            }
            try {
                return new URI("file", null, s4, null);
            }
            catch (final URISyntaxException ex2) {
                throw new AssertionError((Object)ex2);
            }
        }
    }
    
    static URI toUri(WindowsPath absolutePath) {
        absolutePath = absolutePath.toAbsolutePath();
        final String string = absolutePath.toString();
        boolean directory = false;
        if (!string.endsWith("\\")) {
            try {
                absolutePath.checkRead();
                directory = WindowsFileAttributes.get(absolutePath, true).isDirectory();
            }
            catch (final SecurityException | WindowsException ex) {}
        }
        return toUri(string, absolutePath.isUnc(), directory);
    }
    
    static WindowsPath fromUri(final WindowsFileSystem windowsFileSystem, final URI uri) {
        if (!uri.isAbsolute()) {
            throw new IllegalArgumentException("URI is not absolute");
        }
        if (uri.isOpaque()) {
            throw new IllegalArgumentException("URI is not hierarchical");
        }
        final String scheme = uri.getScheme();
        if (scheme == null || !scheme.equalsIgnoreCase("file")) {
            throw new IllegalArgumentException("URI scheme is not \"file\"");
        }
        if (uri.getFragment() != null) {
            throw new IllegalArgumentException("URI has a fragment component");
        }
        if (uri.getQuery() != null) {
            throw new IllegalArgumentException("URI has a query component");
        }
        String s = uri.getPath();
        if (s.equals("")) {
            throw new IllegalArgumentException("URI path component is empty");
        }
        final String authority = uri.getAuthority();
        if (authority != null && !authority.equals("")) {
            String s2 = uri.getHost();
            if (s2 == null) {
                throw new IllegalArgumentException("URI authority component has undefined host");
            }
            if (uri.getUserInfo() != null) {
                throw new IllegalArgumentException("URI authority component has user-info");
            }
            if (uri.getPort() != -1) {
                throw new IllegalArgumentException("URI authority component has port number");
            }
            if (s2.startsWith("[")) {
                s2 = s2.substring(1, s2.length() - 1).replace(':', '-').replace('%', 's') + ".ipv6-literal.net";
            }
            s = "\\\\" + s2 + s;
        }
        else if (s.length() > 2 && s.charAt(2) == ':') {
            s = s.substring(1);
        }
        return WindowsPath.parse(windowsFileSystem, s);
    }
}
