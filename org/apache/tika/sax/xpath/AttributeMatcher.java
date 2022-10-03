package org.apache.tika.sax.xpath;

public class AttributeMatcher extends Matcher
{
    public static final Matcher INSTANCE;
    
    @Override
    public boolean matchesAttribute(final String namespace, final String name) {
        return true;
    }
    
    static {
        INSTANCE = new AttributeMatcher();
    }
}
