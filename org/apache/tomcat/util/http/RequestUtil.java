package org.apache.tomcat.util.http;

import java.net.URISyntaxException;
import java.net.URI;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

public class RequestUtil
{
    private RequestUtil() {
    }
    
    public static String normalize(final String path) {
        return normalize(path, true);
    }
    
    public static String normalize(final String path, final boolean replaceBackSlash) {
        if (path == null) {
            return null;
        }
        String normalized = path;
        if (replaceBackSlash && normalized.indexOf(92) >= 0) {
            normalized = normalized.replace('\\', '/');
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        boolean addedTrailingSlash = false;
        if (normalized.endsWith("/.") || normalized.endsWith("/..")) {
            normalized += "/";
            addedTrailingSlash = true;
        }
        while (true) {
            final int index = normalized.indexOf("//");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }
        while (true) {
            final int index = normalized.indexOf("/./");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
        }
        while (true) {
            final int index = normalized.indexOf("/../");
            if (index < 0) {
                if (normalized.length() > 1 && addedTrailingSlash) {
                    normalized = normalized.substring(0, normalized.length() - 1);
                }
                return normalized;
            }
            if (index == 0) {
                return null;
            }
            final int index2 = normalized.lastIndexOf(47, index - 1);
            normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
        }
    }
    
    public static boolean isSameOrigin(final HttpServletRequest request, final String origin) {
        final StringBuilder target = new StringBuilder();
        String scheme = request.getScheme();
        if (scheme == null) {
            return false;
        }
        scheme = scheme.toLowerCase(Locale.ENGLISH);
        target.append(scheme);
        target.append("://");
        final String host = request.getServerName();
        if (host == null) {
            return false;
        }
        target.append(host);
        final int port = request.getServerPort();
        if (target.length() == origin.length()) {
            if ((("http".equals(scheme) || "ws".equals(scheme)) && port != 80) || (("https".equals(scheme) || "wss".equals(scheme)) && port != 443)) {
                target.append(':');
                target.append(port);
            }
        }
        else {
            target.append(':');
            target.append(port);
        }
        return origin.equals(target.toString());
    }
    
    public static boolean isValidOrigin(final String origin) {
        if (origin.contains("%")) {
            return false;
        }
        if ("null".equals(origin)) {
            return true;
        }
        if (origin.startsWith("file://")) {
            return true;
        }
        URI originURI;
        try {
            originURI = new URI(origin);
        }
        catch (final URISyntaxException e) {
            return false;
        }
        return originURI.getScheme() != null;
    }
}
