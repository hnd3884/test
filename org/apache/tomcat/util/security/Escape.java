package org.apache.tomcat.util.security;

public class Escape
{
    public static String htmlElementContent(final String content) {
        if (content == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); ++i) {
            final char c = content.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
            }
            else if (c == '>') {
                sb.append("&gt;");
            }
            else if (c == '\'') {
                sb.append("&#39;");
            }
            else if (c == '&') {
                sb.append("&amp;");
            }
            else if (c == '\"') {
                sb.append("&quot;");
            }
            else if (c == '/') {
                sb.append("&#47;");
            }
            else {
                sb.append(c);
            }
        }
        return (sb.length() > content.length()) ? sb.toString() : content;
    }
    
    public static String htmlElementContext(final Object obj) {
        if (obj == null) {
            return "?";
        }
        try {
            return htmlElementContent(obj.toString());
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static String xml(final String content) {
        return xml(null, content);
    }
    
    public static String xml(final String ifNull, final String content) {
        return xml(ifNull, false, content);
    }
    
    public static String xml(final String ifNull, final boolean escapeCRLF, final String content) {
        if (content == null) {
            return ifNull;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); ++i) {
            final char c = content.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
            }
            else if (c == '>') {
                sb.append("&gt;");
            }
            else if (c == '\'') {
                sb.append("&apos;");
            }
            else if (c == '&') {
                sb.append("&amp;");
            }
            else if (c == '\"') {
                sb.append("&quot;");
            }
            else if (escapeCRLF && c == '\r') {
                sb.append("&#13;");
            }
            else if (escapeCRLF && c == '\n') {
                sb.append("&#10;");
            }
            else {
                sb.append(c);
            }
        }
        return (sb.length() > content.length()) ? sb.toString() : content;
    }
}
