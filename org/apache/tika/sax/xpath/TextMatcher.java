package org.apache.tika.sax.xpath;

public class TextMatcher extends Matcher
{
    public static final Matcher INSTANCE;
    
    @Override
    public boolean matchesText() {
        return true;
    }
    
    static {
        INSTANCE = new TextMatcher();
    }
}
