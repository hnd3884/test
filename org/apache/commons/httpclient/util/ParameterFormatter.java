package org.apache.commons.httpclient.util;

import org.apache.commons.httpclient.NameValuePair;

public class ParameterFormatter
{
    private static final char[] SEPARATORS;
    private static final char[] UNSAFE_CHARS;
    private boolean alwaysUseQuotes;
    
    public ParameterFormatter() {
        this.alwaysUseQuotes = true;
    }
    
    private static boolean isOneOf(final char[] chars, final char ch) {
        for (int i = 0; i < chars.length; ++i) {
            if (ch == chars[i]) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isUnsafeChar(final char ch) {
        return isOneOf(ParameterFormatter.UNSAFE_CHARS, ch);
    }
    
    private static boolean isSeparator(final char ch) {
        return isOneOf(ParameterFormatter.SEPARATORS, ch);
    }
    
    public boolean isAlwaysUseQuotes() {
        return this.alwaysUseQuotes;
    }
    
    public void setAlwaysUseQuotes(final boolean alwaysUseQuotes) {
        this.alwaysUseQuotes = alwaysUseQuotes;
    }
    
    public static void formatValue(final StringBuffer buffer, final String value, final boolean alwaysUseQuotes) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value buffer may not be null");
        }
        if (alwaysUseQuotes) {
            buffer.append('\"');
            for (int i = 0; i < value.length(); ++i) {
                final char ch = value.charAt(i);
                if (isUnsafeChar(ch)) {
                    buffer.append('\\');
                }
                buffer.append(ch);
            }
            buffer.append('\"');
        }
        else {
            final int offset = buffer.length();
            boolean unsafe = false;
            for (int j = 0; j < value.length(); ++j) {
                final char ch2 = value.charAt(j);
                if (isSeparator(ch2)) {
                    unsafe = true;
                }
                if (isUnsafeChar(ch2)) {
                    buffer.append('\\');
                }
                buffer.append(ch2);
            }
            if (unsafe) {
                buffer.insert(offset, '\"');
                buffer.append('\"');
            }
        }
    }
    
    public void format(final StringBuffer buffer, final NameValuePair param) {
        if (buffer == null) {
            throw new IllegalArgumentException("String buffer may not be null");
        }
        if (param == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        buffer.append(param.getName());
        final String value = param.getValue();
        if (value != null) {
            buffer.append("=");
            formatValue(buffer, value, this.alwaysUseQuotes);
        }
    }
    
    public String format(final NameValuePair param) {
        final StringBuffer buffer = new StringBuffer();
        this.format(buffer, param);
        return buffer.toString();
    }
    
    static {
        SEPARATORS = new char[] { '(', ')', '<', '>', '@', ',', ';', ':', '\\', '\"', '/', '[', ']', '?', '=', '{', '}', ' ', '\t' };
        UNSAFE_CHARS = new char[] { '\"', '\\' };
    }
}
