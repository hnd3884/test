package org.apache.commons.text.lookup;

abstract class AbstractStringLookup implements StringLookup
{
    private static final String EMPTY = "";
    protected static final char SPLIT_CH = ':';
    protected static final String SPLIT_STR;
    
    protected String substringAfter(final String value, final char ch) {
        final int indexOf = value.indexOf(ch);
        return (indexOf > -1) ? value.substring(indexOf + 1) : "";
    }
    
    protected String substringAfterLast(final String value, final char ch) {
        final int indexOf = value.lastIndexOf(ch);
        return (indexOf > -1) ? value.substring(indexOf + 1) : "";
    }
    
    protected String substringAfter(final String value, final String str) {
        final int indexOf = value.indexOf(str);
        return (indexOf > -1) ? value.substring(indexOf + str.length()) : "";
    }
    
    static {
        SPLIT_STR = String.valueOf(':');
    }
}
