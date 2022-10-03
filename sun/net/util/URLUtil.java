package sun.net.util;

import java.net.URLPermission;
import java.io.IOException;
import java.security.Permission;
import java.net.URL;

public class URLUtil
{
    public static String urlNoFragString(final URL url) {
        final StringBuilder sb = new StringBuilder();
        final String protocol = url.getProtocol();
        if (protocol != null) {
            sb.append(protocol.toLowerCase());
            sb.append("://");
        }
        final String host = url.getHost();
        if (host != null) {
            sb.append(host.toLowerCase());
            int n = url.getPort();
            if (n == -1) {
                n = url.getDefaultPort();
            }
            if (n != -1) {
                sb.append(":").append(n);
            }
        }
        final String file = url.getFile();
        if (file != null) {
            sb.append(file);
        }
        return sb.toString();
    }
    
    public static Permission getConnectPermission(final URL url) throws IOException {
        final String lowerCase = url.toString().toLowerCase();
        if (lowerCase.startsWith("http:") || lowerCase.startsWith("https:")) {
            return getURLConnectPermission(url);
        }
        if (lowerCase.startsWith("jar:http:") || lowerCase.startsWith("jar:https:")) {
            final String string = url.toString();
            final int index = string.indexOf("!/");
            return getURLConnectPermission(new URL(string.substring(4, (index > -1) ? index : string.length())));
        }
        return url.openConnection().getPermission();
    }
    
    private static Permission getURLConnectPermission(final URL url) {
        return new URLPermission(url.getProtocol() + "://" + url.getAuthority() + url.getPath());
    }
}
