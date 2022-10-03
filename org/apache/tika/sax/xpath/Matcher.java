package org.apache.tika.sax.xpath;

public class Matcher
{
    public static final Matcher FAIL;
    
    public Matcher descend(final String namespace, final String name) {
        return Matcher.FAIL;
    }
    
    public boolean matchesElement() {
        return false;
    }
    
    public boolean matchesAttribute(final String namespace, final String name) {
        return false;
    }
    
    public boolean matchesText() {
        return false;
    }
    
    static {
        FAIL = new Matcher();
    }
}
