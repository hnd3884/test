package org.apache.tika.sax.xpath;

import java.util.Objects;

public class NamedElementMatcher extends ChildMatcher
{
    private final String namespace;
    private final String name;
    
    protected NamedElementMatcher(final String namespace, final String name, final Matcher then) {
        super(then);
        this.namespace = namespace;
        this.name = name;
    }
    
    @Override
    public Matcher descend(final String namespace, final String name) {
        if (Objects.equals(namespace, this.namespace) && name.equals(this.name)) {
            return super.descend(namespace, name);
        }
        return NamedElementMatcher.FAIL;
    }
}
