package org.apache.tika.sax.xpath;

public class ElementMatcher extends Matcher
{
    public static final Matcher INSTANCE;
    
    @Override
    public boolean matchesElement() {
        return true;
    }
    
    static {
        INSTANCE = new ElementMatcher();
    }
}
