package org.apache.tika.sax.xpath;

public class NodeMatcher extends Matcher
{
    public static final Matcher INSTANCE;
    
    @Override
    public boolean matchesElement() {
        return true;
    }
    
    @Override
    public boolean matchesAttribute(final String namespace, final String name) {
        return true;
    }
    
    @Override
    public boolean matchesText() {
        return true;
    }
    
    static {
        INSTANCE = new NodeMatcher();
    }
}
