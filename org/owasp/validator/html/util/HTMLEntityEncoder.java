package org.owasp.validator.html.util;

public class HTMLEntityEncoder
{
    public static String htmlEntityEncode(final String value) {
        final StringBuffer buff = new StringBuffer();
        if (value == null) {
            return null;
        }
        for (int i = 0; i < value.length(); ++i) {
            final char ch = value.charAt(i);
            if (ch == '&') {
                buff.append("&amp;");
            }
            else if (ch == '<') {
                buff.append("&lt;");
            }
            else if (ch == '>') {
                buff.append("&gt;");
            }
            else if (Character.isWhitespace(ch)) {
                buff.append(ch);
            }
            else if (Character.isLetterOrDigit(ch)) {
                buff.append(ch);
            }
            else if (ch >= ' ' && ch <= '~') {
                buff.append("&#" + (int)ch + ";");
            }
        }
        return buff.toString();
    }
}
