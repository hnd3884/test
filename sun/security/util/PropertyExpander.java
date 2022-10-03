package sun.security.util;

import java.security.GeneralSecurityException;
import java.net.URISyntaxException;
import sun.net.www.ParseUtil;
import java.net.URI;
import java.io.File;

public class PropertyExpander
{
    public static String expand(final String s) throws ExpandException {
        return expand(s, false);
    }
    
    public static String expand(final String s, final boolean b) throws ExpandException {
        if (s == null) {
            return null;
        }
        int i = s.indexOf("${", 0);
        if (i == -1) {
            return s;
        }
        final StringBuffer sb = new StringBuffer(s.length());
        final int length = s.length();
        int n = 0;
        while (i < length) {
            if (i > n) {
                sb.append(s.substring(n, i));
            }
            int index = i + 2;
            if (index < length && s.charAt(index) == '{') {
                index = s.indexOf("}}", index);
                if (index == -1 || index + 2 == length) {
                    sb.append(s.substring(i));
                    break;
                }
                ++index;
                sb.append(s.substring(i, index + 1));
            }
            else {
                while (index < length && s.charAt(index) != '}') {
                    ++index;
                }
                if (index == length) {
                    sb.append(s.substring(i, index));
                    break;
                }
                final String substring = s.substring(i + 2, index);
                if (substring.equals("/")) {
                    sb.append(File.separatorChar);
                }
                else {
                    String s2 = System.getProperty(substring);
                    if (s2 == null) {
                        throw new ExpandException("unable to expand property " + substring);
                    }
                    if (b) {
                        try {
                            if (sb.length() > 0 || !new URI(s2).isAbsolute()) {
                                s2 = ParseUtil.encodePath(s2);
                            }
                        }
                        catch (final URISyntaxException ex) {
                            s2 = ParseUtil.encodePath(s2);
                        }
                    }
                    sb.append(s2);
                }
            }
            n = index + 1;
            i = s.indexOf("${", n);
            if (i == -1) {
                if (n < length) {
                    sb.append(s.substring(n, length));
                    break;
                }
                break;
            }
        }
        return sb.toString();
    }
    
    public static class ExpandException extends GeneralSecurityException
    {
        private static final long serialVersionUID = -7941948581406161702L;
        
        public ExpandException(final String s) {
            super(s);
        }
    }
}
