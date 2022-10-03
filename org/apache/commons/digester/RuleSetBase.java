package org.apache.commons.digester;

public abstract class RuleSetBase implements RuleSet
{
    protected String namespaceURI;
    
    public RuleSetBase() {
        this.namespaceURI = null;
    }
    
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    public abstract void addRuleInstances(final Digester p0);
}
