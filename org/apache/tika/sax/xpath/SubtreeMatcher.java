package org.apache.tika.sax.xpath;

public class SubtreeMatcher extends Matcher
{
    private final Matcher then;
    
    public SubtreeMatcher(final Matcher then) {
        this.then = then;
    }
    
    @Override
    public Matcher descend(final String namespace, final String name) {
        final Matcher next = this.then.descend(namespace, name);
        if (next == SubtreeMatcher.FAIL || next == this.then) {
            return this;
        }
        return new CompositeMatcher(next, this);
    }
    
    @Override
    public boolean matchesElement() {
        return this.then.matchesElement();
    }
    
    @Override
    public boolean matchesAttribute(final String namespace, final String name) {
        return this.then.matchesAttribute(namespace, name);
    }
    
    @Override
    public boolean matchesText() {
        return this.then.matchesText();
    }
}
