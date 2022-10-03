package org.apache.tika.sax.xpath;

import java.util.Objects;

public class NamedAttributeMatcher extends Matcher
{
    private final String namespace;
    private final String name;
    
    public NamedAttributeMatcher(final String namespace, final String name) {
        this.namespace = namespace;
        this.name = name;
    }
    
    @Override
    public boolean matchesAttribute(final String namespace, final String name) {
        return Objects.equals(namespace, this.namespace) && name.equals(this.name);
    }
}
