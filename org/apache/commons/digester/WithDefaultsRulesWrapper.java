package org.apache.commons.digester;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class WithDefaultsRulesWrapper implements Rules
{
    private Rules wrappedRules;
    private List defaultRules;
    private List allRules;
    
    public WithDefaultsRulesWrapper(final Rules wrappedRules) {
        this.defaultRules = new ArrayList();
        this.allRules = new ArrayList();
        if (wrappedRules == null) {
            throw new IllegalArgumentException("Wrapped rules must not be null");
        }
        this.wrappedRules = wrappedRules;
    }
    
    public Digester getDigester() {
        return this.wrappedRules.getDigester();
    }
    
    public void setDigester(final Digester digester) {
        this.wrappedRules.setDigester(digester);
        final Iterator it = this.defaultRules.iterator();
        while (it.hasNext()) {
            final Rule rule = it.next();
            rule.setDigester(digester);
        }
    }
    
    public String getNamespaceURI() {
        return this.wrappedRules.getNamespaceURI();
    }
    
    public void setNamespaceURI(final String namespaceURI) {
        this.wrappedRules.setNamespaceURI(namespaceURI);
    }
    
    public List getDefaults() {
        return this.defaultRules;
    }
    
    public List match(final String pattern) {
        return this.match("", pattern);
    }
    
    public List match(final String namespaceURI, final String pattern) {
        final List matches = this.wrappedRules.match(namespaceURI, pattern);
        if (matches == null || matches.isEmpty()) {
            return new ArrayList(this.defaultRules);
        }
        return matches;
    }
    
    public void addDefault(final Rule rule) {
        if (this.wrappedRules.getDigester() != null) {
            rule.setDigester(this.wrappedRules.getDigester());
        }
        if (this.wrappedRules.getNamespaceURI() != null) {
            rule.setNamespaceURI(this.wrappedRules.getNamespaceURI());
        }
        this.defaultRules.add(rule);
        this.allRules.add(rule);
    }
    
    public List rules() {
        return this.allRules;
    }
    
    public void clear() {
        this.wrappedRules.clear();
        this.allRules.clear();
        this.defaultRules.clear();
    }
    
    public void add(final String pattern, final Rule rule) {
        this.wrappedRules.add(pattern, rule);
        this.allRules.add(rule);
    }
}
