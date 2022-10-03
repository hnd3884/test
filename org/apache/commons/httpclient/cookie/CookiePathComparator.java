package org.apache.commons.httpclient.cookie;

import org.apache.commons.httpclient.Cookie;
import java.util.Comparator;

public class CookiePathComparator implements Comparator
{
    private String normalizePath(final Cookie cookie) {
        String path = cookie.getPath();
        if (path == null) {
            path = "/";
        }
        if (!path.endsWith("/")) {
            path += "/";
        }
        return path;
    }
    
    public int compare(final Object o1, final Object o2) {
        final Cookie c1 = (Cookie)o1;
        final Cookie c2 = (Cookie)o2;
        final String path1 = this.normalizePath(c1);
        final String path2 = this.normalizePath(c2);
        if (path1.equals(path2)) {
            return 0;
        }
        if (path1.startsWith(path2)) {
            return -1;
        }
        if (path2.startsWith(path1)) {
            return 1;
        }
        return 0;
    }
}
