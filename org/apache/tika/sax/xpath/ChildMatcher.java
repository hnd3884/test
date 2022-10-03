package org.apache.tika.sax.xpath;

public class ChildMatcher extends Matcher
{
    private final Matcher then;
    
    public ChildMatcher(final Matcher then) {
        this.then = then;
    }
    
    @Override
    public Matcher descend(final String namespace, final String name) {
        return this.then;
    }
}
