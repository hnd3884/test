package org.glassfish.jersey.server.internal;

public class ContainerUtils
{
    private static final String[] TOKENS;
    private static final String[] REPLACEMENTS;
    
    public static String encodeUnsafeCharacters(final String originalQueryString) {
        if (originalQueryString == null) {
            return null;
        }
        String result = originalQueryString;
        for (int i = 0; i < ContainerUtils.TOKENS.length; ++i) {
            if (originalQueryString.contains(ContainerUtils.TOKENS[i])) {
                result = result.replace(ContainerUtils.TOKENS[i], ContainerUtils.REPLACEMENTS[i]);
            }
        }
        return result;
    }
    
    public static String reduceLeadingSlashes(final String path) {
        final int length;
        if (path == null || (length = path.length()) == 0) {
            return path;
        }
        int start;
        for (start = 0; start != length && "/".indexOf(path.charAt(start)) != -1; ++start) {}
        return path.substring((start > 0) ? (start - 1) : 0);
    }
    
    public static String getHandlerPath(final String uri) {
        if (uri == null || uri.length() == 0 || !uri.contains("?")) {
            return uri;
        }
        return uri.substring(0, uri.indexOf("?"));
    }
    
    static {
        TOKENS = new String[] { "{", "}", "\\", "^", "|", "`" };
        REPLACEMENTS = new String[] { "%7B", "%7D", "%5C", "%5E", "%7C", "%60" };
    }
}
