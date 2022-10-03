package redis.clients.util;

import java.net.URI;

public class JedisURIHelper
{
    private static final int DEFAULT_DB = 0;
    
    public static String getPassword(final URI uri) {
        final String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            return userInfo.split(":", 2)[1];
        }
        return null;
    }
    
    public static int getDBIndex(final URI uri) {
        final String[] pathSplit = uri.getPath().split("/", 2);
        if (pathSplit.length <= 1) {
            return 0;
        }
        final String dbIndexStr = pathSplit[1];
        if (dbIndexStr.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(dbIndexStr);
    }
    
    public static boolean isValid(final URI uri) {
        return !isEmpty(uri.getScheme()) && !isEmpty(uri.getHost()) && uri.getPort() != -1;
    }
    
    private static boolean isEmpty(final String value) {
        return value == null || value.trim().length() == 0;
    }
}
