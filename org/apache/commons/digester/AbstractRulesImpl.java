package org.apache.commons.digester;

import java.util.List;

public abstract class AbstractRulesImpl implements Rules
{
    private Digester digester;
    private String namespaceURI;
    
    public Digester getDigester() {
        return this.digester;
    }
    
    public void setDigester(final Digester digester) {
        this.digester = digester;
    }
    
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    public void setNamespaceURI(final String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }
    
    public void add(final String pattern, final Rule rule) {
        if (this.digester != null) {
            rule.setDigester(this.digester);
        }
        if (this.namespaceURI != null) {
            rule.setNamespaceURI(this.namespaceURI);
        }
        this.registerRule(pattern, rule);
    }
    
    protected abstract void registerRule(final String p0, final Rule p1);
    
    public abstract void clear();
    
    public List match(final String pattern) {
        return this.match(this.namespaceURI, pattern);
    }
    
    public abstract List match(final String p0, final String p1);
    
    public abstract List rules();
}
