package org.apache.http.conn.util;

public class DnsUtils
{
    private DnsUtils() {
    }
    
    private static boolean isUpper(final char c) {
        return c >= 'A' && c <= 'Z';
    }
    
    public static String normalize(final String s) {
        if (s == null) {
            return null;
        }
        int pos;
        int remaining;
        for (pos = 0, remaining = s.length(); remaining > 0 && !isUpper(s.charAt(pos)); ++pos, --remaining) {}
        if (remaining > 0) {
            final StringBuilder buf = new StringBuilder(s.length());
            buf.append(s, 0, pos);
            while (remaining > 0) {
                final char c = s.charAt(pos);
                if (isUpper(c)) {
                    buf.append((char)(c + ' '));
                }
                else {
                    buf.append(c);
                }
                ++pos;
                --remaining;
            }
            return buf.toString();
        }
        return s;
    }
}
