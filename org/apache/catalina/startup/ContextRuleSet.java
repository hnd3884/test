package org.apache.catalina.startup;

import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSetBase;

public class ContextRuleSet extends RuleSetBase
{
    protected final String prefix;
    protected final boolean create;
    
    public ContextRuleSet() {
        this("");
    }
    
    public ContextRuleSet(final String prefix) {
        this(prefix, true);
    }
    
    public ContextRuleSet(final String prefix, final boolean create) {
        this.prefix = prefix;
        this.create = create;
    }
    
    public void addRuleInstances(final Digester digester) {
        if (this.create) {
            digester.addObjectCreate(this.prefix + "Context", "org.apache.catalina.core.StandardContext", "className");
            digester.addSetProperties(this.prefix + "Context");
        }
        else {
            digester.addRule(this.prefix + "Context", (Rule)new SetContextPropertiesRule());
        }
        if (this.create) {
            digester.addRule(this.prefix + "Context", (Rule)new LifecycleListenerRule("org.apache.catalina.startup.ContextConfig", "configClass"));
            digester.addSetNext(this.prefix + "Context", "addChild", "org.apache.catalina.Container");
        }
        digester.addObjectCreate(this.prefix + "Context/Listener", (String)null, "className");
        digester.addSetProperties(this.prefix + "Context/Listener");
        digester.addSetNext(this.prefix + "Context/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate(this.prefix + "Context/Loader", "org.apache.catalina.loader.WebappLoader", "className");
        digester.addSetProperties(this.prefix + "Context/Loader");
        digester.addSetNext(this.prefix + "Context/Loader", "setLoader", "org.apache.catalina.Loader");
        digester.addObjectCreate(this.prefix + "Context/Manager", "org.apache.catalina.session.StandardManager", "className");
        digester.addSetProperties(this.prefix + "Context/Manager");
        digester.addSetNext(this.prefix + "Context/Manager", "setManager", "org.apache.catalina.Manager");
        digester.addObjectCreate(this.prefix + "Context/Manager/Store", (String)null, "className");
        digester.addSetProperties(this.prefix + "Context/Manager/Store");
        digester.addSetNext(this.prefix + "Context/Manager/Store", "setStore", "org.apache.catalina.Store");
        digester.addObjectCreate(this.prefix + "Context/Manager/SessionIdGenerator", "org.apache.catalina.util.StandardSessionIdGenerator", "className");
        digester.addSetProperties(this.prefix + "Context/Manager/SessionIdGenerator");
        digester.addSetNext(this.prefix + "Context/Manager/SessionIdGenerator", "setSessionIdGenerator", "org.apache.catalina.SessionIdGenerator");
        digester.addObjectCreate(this.prefix + "Context/Parameter", "org.apache.tomcat.util.descriptor.web.ApplicationParameter");
        digester.addSetProperties(this.prefix + "Context/Parameter");
        digester.addSetNext(this.prefix + "Context/Parameter", "addApplicationParameter", "org.apache.tomcat.util.descriptor.web.ApplicationParameter");
        digester.addRuleSet((RuleSet)new RealmRuleSet(this.prefix + "Context/"));
        digester.addObjectCreate(this.prefix + "Context/Resources", "org.apache.catalina.webresources.StandardRoot", "className");
        digester.addSetProperties(this.prefix + "Context/Resources");
        digester.addSetNext(this.prefix + "Context/Resources", "setResources", "org.apache.catalina.WebResourceRoot");
        digester.addObjectCreate(this.prefix + "Context/Resources/PreResources", (String)null, "className");
        digester.addSetProperties(this.prefix + "Context/Resources/PreResources");
        digester.addSetNext(this.prefix + "Context/Resources/PreResources", "addPreResources", "org.apache.catalina.WebResourceSet");
        digester.addObjectCreate(this.prefix + "Context/Resources/JarResources", (String)null, "className");
        digester.addSetProperties(this.prefix + "Context/Resources/JarResources");
        digester.addSetNext(this.prefix + "Context/Resources/JarResources", "addJarResources", "org.apache.catalina.WebResourceSet");
        digester.addObjectCreate(this.prefix + "Context/Resources/PostResources", (String)null, "className");
        digester.addSetProperties(this.prefix + "Context/Resources/PostResources");
        digester.addSetNext(this.prefix + "Context/Resources/PostResources", "addPostResources", "org.apache.catalina.WebResourceSet");
        digester.addObjectCreate(this.prefix + "Context/ResourceLink", "org.apache.tomcat.util.descriptor.web.ContextResourceLink");
        digester.addSetProperties(this.prefix + "Context/ResourceLink");
        digester.addRule(this.prefix + "Context/ResourceLink", (Rule)new SetNextNamingRule("addResourceLink", "org.apache.tomcat.util.descriptor.web.ContextResourceLink"));
        digester.addObjectCreate(this.prefix + "Context/Valve", (String)null, "className");
        digester.addSetProperties(this.prefix + "Context/Valve");
        digester.addSetNext(this.prefix + "Context/Valve", "addValve", "org.apache.catalina.Valve");
        digester.addCallMethod(this.prefix + "Context/WatchedResource", "addWatchedResource", 0);
        digester.addCallMethod(this.prefix + "Context/WrapperLifecycle", "addWrapperLifecycle", 0);
        digester.addCallMethod(this.prefix + "Context/WrapperListener", "addWrapperListener", 0);
        digester.addObjectCreate(this.prefix + "Context/JarScanner", "org.apache.tomcat.util.scan.StandardJarScanner", "className");
        digester.addSetProperties(this.prefix + "Context/JarScanner");
        digester.addSetNext(this.prefix + "Context/JarScanner", "setJarScanner", "org.apache.tomcat.JarScanner");
        digester.addObjectCreate(this.prefix + "Context/JarScanner/JarScanFilter", "org.apache.tomcat.util.scan.StandardJarScanFilter", "className");
        digester.addSetProperties(this.prefix + "Context/JarScanner/JarScanFilter");
        digester.addSetNext(this.prefix + "Context/JarScanner/JarScanFilter", "setJarScanFilter", "org.apache.tomcat.JarScanFilter");
        digester.addObjectCreate(this.prefix + "Context/CookieProcessor", "org.apache.tomcat.util.http.Rfc6265CookieProcessor", "className");
        digester.addSetProperties(this.prefix + "Context/CookieProcessor");
        digester.addSetNext(this.prefix + "Context/CookieProcessor", "setCookieProcessor", "org.apache.tomcat.util.http.CookieProcessor");
    }
}
