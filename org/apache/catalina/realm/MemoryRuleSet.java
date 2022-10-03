package org.apache.catalina.realm;

import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSetBase;

public class MemoryRuleSet extends RuleSetBase
{
    protected final String prefix;
    
    public MemoryRuleSet() {
        this("tomcat-users/");
    }
    
    public MemoryRuleSet(final String prefix) {
        this.prefix = prefix;
    }
    
    public void addRuleInstances(final Digester digester) {
        digester.addRule(this.prefix + "user", (Rule)new MemoryUserRule());
    }
}
