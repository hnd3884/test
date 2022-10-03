package org.apache.catalina.startup;

import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSetBase;

public class EngineRuleSet extends RuleSetBase
{
    protected final String prefix;
    
    public EngineRuleSet() {
        this("");
    }
    
    public EngineRuleSet(final String prefix) {
        this.prefix = prefix;
    }
    
    public void addRuleInstances(final Digester digester) {
        digester.addObjectCreate(this.prefix + "Engine", "org.apache.catalina.core.StandardEngine", "className");
        digester.addSetProperties(this.prefix + "Engine");
        digester.addRule(this.prefix + "Engine", (Rule)new LifecycleListenerRule("org.apache.catalina.startup.EngineConfig", "engineConfigClass"));
        digester.addSetNext(this.prefix + "Engine", "setContainer", "org.apache.catalina.Engine");
        digester.addObjectCreate(this.prefix + "Engine/Cluster", (String)null, "className");
        digester.addSetProperties(this.prefix + "Engine/Cluster");
        digester.addSetNext(this.prefix + "Engine/Cluster", "setCluster", "org.apache.catalina.Cluster");
        digester.addObjectCreate(this.prefix + "Engine/Listener", (String)null, "className");
        digester.addSetProperties(this.prefix + "Engine/Listener");
        digester.addSetNext(this.prefix + "Engine/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addRuleSet((RuleSet)new RealmRuleSet(this.prefix + "Engine/"));
        digester.addObjectCreate(this.prefix + "Engine/Valve", (String)null, "className");
        digester.addSetProperties(this.prefix + "Engine/Valve");
        digester.addSetNext(this.prefix + "Engine/Valve", "addValve", "org.apache.catalina.Valve");
    }
}
