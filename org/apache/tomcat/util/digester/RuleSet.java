package org.apache.tomcat.util.digester;

public interface RuleSet
{
    @Deprecated
    String getNamespaceURI();
    
    void addRuleInstances(final Digester p0);
}
