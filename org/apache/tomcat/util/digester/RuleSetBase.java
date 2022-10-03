package org.apache.tomcat.util.digester;

@Deprecated
public abstract class RuleSetBase implements RuleSet
{
    @Deprecated
    protected String namespaceURI;
    
    public RuleSetBase() {
        this.namespaceURI = null;
    }
    
    @Deprecated
    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    @Override
    public abstract void addRuleInstances(final Digester p0);
}
