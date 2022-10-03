package org.apache.tomcat.util.http;

public class HeaderUtil
{
    public static String toPrintableString(final byte[] bytes, final int offset, final int len) {
        final StringBuilder result = new StringBuilder();
        for (int i = offset; i < offset + len; ++i) {
            final char c = (char)(bytes[i] & 0xFF);
            if (c < ' ' || c > '~') {
                result.append("0x");
                result.append(Character.forDigit(c >> 4 & 0xF, 16));
                result.append(Character.forDigit(c & '\u000f', 16));
            }
            else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    private HeaderUtil() {
    }
}
