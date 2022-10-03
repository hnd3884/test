package org.owasp.validator.html;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public class TagMatcher
{
    private final Set<String> allowedLowercase;
    
    public TagMatcher(final Iterable<String> allowedValues) {
        this.allowedLowercase = new HashSet<String>();
        for (final String item : allowedValues) {
            this.allowedLowercase.add(item.toLowerCase());
        }
    }
    
    public boolean matches(final String tagName) {
        return this.allowedLowercase.contains(tagName.toLowerCase());
    }
    
    public int size() {
        return this.allowedLowercase.size();
    }
}
