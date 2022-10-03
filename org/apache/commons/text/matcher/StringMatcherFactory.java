package org.apache.commons.text.matcher;

public final class StringMatcherFactory
{
    public static final StringMatcherFactory INSTANCE;
    private static final AbstractStringMatcher.CharSetMatcher SPLIT_MATCHER;
    private static final AbstractStringMatcher.CharMatcher COMMA_MATCHER;
    private static final AbstractStringMatcher.CharMatcher TAB_MATCHER;
    private static final AbstractStringMatcher.CharMatcher SPACE_MATCHER;
    private static final AbstractStringMatcher.TrimMatcher TRIM_MATCHER;
    private static final AbstractStringMatcher.CharMatcher SINGLE_QUOTE_MATCHER;
    private static final AbstractStringMatcher.CharMatcher DOUBLE_QUOTE_MATCHER;
    private static final AbstractStringMatcher.CharSetMatcher QUOTE_MATCHER;
    private static final AbstractStringMatcher.NoMatcher NONE_MATCHER;
    
    private StringMatcherFactory() {
    }
    
    public StringMatcher charMatcher(final char ch) {
        return new AbstractStringMatcher.CharMatcher(ch);
    }
    
    public StringMatcher charSetMatcher(final char... chars) {
        if (chars == null || chars.length == 0) {
            return StringMatcherFactory.NONE_MATCHER;
        }
        if (chars.length == 1) {
            return new AbstractStringMatcher.CharMatcher(chars[0]);
        }
        return new AbstractStringMatcher.CharSetMatcher(chars);
    }
    
    public StringMatcher charSetMatcher(final String chars) {
        if (chars == null || chars.length() == 0) {
            return StringMatcherFactory.NONE_MATCHER;
        }
        if (chars.length() == 1) {
            return new AbstractStringMatcher.CharMatcher(chars.charAt(0));
        }
        return new AbstractStringMatcher.CharSetMatcher(chars.toCharArray());
    }
    
    public StringMatcher commaMatcher() {
        return StringMatcherFactory.COMMA_MATCHER;
    }
    
    public StringMatcher doubleQuoteMatcher() {
        return StringMatcherFactory.DOUBLE_QUOTE_MATCHER;
    }
    
    public StringMatcher noneMatcher() {
        return StringMatcherFactory.NONE_MATCHER;
    }
    
    public StringMatcher quoteMatcher() {
        return StringMatcherFactory.QUOTE_MATCHER;
    }
    
    public StringMatcher singleQuoteMatcher() {
        return StringMatcherFactory.SINGLE_QUOTE_MATCHER;
    }
    
    public StringMatcher spaceMatcher() {
        return StringMatcherFactory.SPACE_MATCHER;
    }
    
    public StringMatcher splitMatcher() {
        return StringMatcherFactory.SPLIT_MATCHER;
    }
    
    public StringMatcher stringMatcher(final String str) {
        if (str == null || str.length() == 0) {
            return StringMatcherFactory.NONE_MATCHER;
        }
        return new AbstractStringMatcher.StringMatcher(str);
    }
    
    public StringMatcher tabMatcher() {
        return StringMatcherFactory.TAB_MATCHER;
    }
    
    public StringMatcher trimMatcher() {
        return StringMatcherFactory.TRIM_MATCHER;
    }
    
    static {
        INSTANCE = new StringMatcherFactory();
        SPLIT_MATCHER = new AbstractStringMatcher.CharSetMatcher(" \t\n\r\f".toCharArray());
        COMMA_MATCHER = new AbstractStringMatcher.CharMatcher(',');
        TAB_MATCHER = new AbstractStringMatcher.CharMatcher('\t');
        SPACE_MATCHER = new AbstractStringMatcher.CharMatcher(' ');
        TRIM_MATCHER = new AbstractStringMatcher.TrimMatcher();
        SINGLE_QUOTE_MATCHER = new AbstractStringMatcher.CharMatcher('\'');
        DOUBLE_QUOTE_MATCHER = new AbstractStringMatcher.CharMatcher('\"');
        QUOTE_MATCHER = new AbstractStringMatcher.CharSetMatcher("'\"".toCharArray());
        NONE_MATCHER = new AbstractStringMatcher.NoMatcher();
    }
}
