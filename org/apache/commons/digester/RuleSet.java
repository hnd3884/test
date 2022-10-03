package org.apache.commons.digester;

public interface RuleSet
{
    String getNamespaceURI();
    
    void addRuleInstances(final Digester p0);
}
