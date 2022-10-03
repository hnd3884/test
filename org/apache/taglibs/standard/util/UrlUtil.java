package org.apache.taglibs.standard.util;

import java.util.BitSet;

public class UrlUtil
{
    private static final BitSet VALID_SCHEME_CHARS;
    
    public static boolean isAbsoluteUrl(final String url) {
        if (url == null) {
            return false;
        }
        final int colonPos = url.indexOf(":");
        if (colonPos == -1) {
            return false;
        }
        for (int i = 0; i < colonPos; ++i) {
            if (!UrlUtil.VALID_SCHEME_CHARS.get(url.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static String getScheme(final CharSequence url) {
        final StringBuilder scheme = new StringBuilder();
        for (int i = 0; i < url.length(); ++i) {
            final char ch = url.charAt(i);
            if (ch == ':') {
                final String result = scheme.toString();
                if (!"jar".equals(result)) {
                    return result;
                }
            }
            scheme.append(ch);
        }
        throw new IllegalArgumentException("No scheme found: " + (Object)url);
    }
    
    static {
        (VALID_SCHEME_CHARS = new BitSet(128)).set(65, 91);
        UrlUtil.VALID_SCHEME_CHARS.set(97, 123);
        UrlUtil.VALID_SCHEME_CHARS.set(48, 58);
        UrlUtil.VALID_SCHEME_CHARS.set(43);
        UrlUtil.VALID_SCHEME_CHARS.set(46);
        UrlUtil.VALID_SCHEME_CHARS.set(45);
    }
}
