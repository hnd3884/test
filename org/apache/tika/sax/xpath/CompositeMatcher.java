package org.apache.tika.sax.xpath;

public class CompositeMatcher extends Matcher
{
    private final Matcher a;
    private final Matcher b;
    
    public CompositeMatcher(final Matcher a, final Matcher b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public Matcher descend(final String namespace, final String name) {
        final Matcher a = this.a.descend(namespace, name);
        final Matcher b = this.b.descend(namespace, name);
        if (a == CompositeMatcher.FAIL) {
            return b;
        }
        if (b == CompositeMatcher.FAIL) {
            return a;
        }
        if (this.a == a && this.b == b) {
            return this;
        }
        return new CompositeMatcher(a, b);
    }
    
    @Override
    public boolean matchesElement() {
        return this.a.matchesElement() || this.b.matchesElement();
    }
    
    @Override
    public boolean matchesAttribute(final String namespace, final String name) {
        return this.a.matchesAttribute(namespace, name) || this.b.matchesAttribute(namespace, name);
    }
    
    @Override
    public boolean matchesText() {
        return this.a.matchesText() || this.b.matchesText();
    }
}
