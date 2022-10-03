package org.apache.commons.digester.plugins;

import org.apache.commons.logging.Log;
import org.apache.commons.digester.Digester;
import java.util.Map;
import java.util.Properties;

public class Declaration
{
    private Class pluginClass;
    private String pluginClassName;
    private String id;
    private Properties properties;
    private boolean initialized;
    private RuleLoader ruleLoader;
    
    public Declaration(final String pluginClassName) {
        this.properties = new Properties();
        this.initialized = false;
        this.ruleLoader = null;
        this.pluginClassName = pluginClassName;
    }
    
    public Declaration(final Class pluginClass) {
        this.properties = new Properties();
        this.initialized = false;
        this.ruleLoader = null;
        this.pluginClass = pluginClass;
        this.pluginClassName = pluginClass.getName();
    }
    
    public Declaration(final Class pluginClass, final RuleLoader ruleLoader) {
        this.properties = new Properties();
        this.initialized = false;
        this.ruleLoader = null;
        this.pluginClass = pluginClass;
        this.pluginClassName = pluginClass.getName();
        this.ruleLoader = ruleLoader;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setProperties(final Properties p) {
        this.properties.putAll(p);
    }
    
    public Class getPluginClass() {
        return this.pluginClass;
    }
    
    public void init(final Digester digester, final PluginManager pm) throws PluginException {
        final Log log = digester.getLogger();
        final boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)"init being called!");
        }
        if (this.initialized) {
            throw new PluginAssertionFailure("Init called multiple times.");
        }
        if (this.pluginClass == null && this.pluginClassName != null) {
            try {
                this.pluginClass = digester.getClassLoader().loadClass(this.pluginClassName);
            }
            catch (final ClassNotFoundException cnfe) {
                throw new PluginException("Unable to load class " + this.pluginClassName, cnfe);
            }
        }
        if (this.ruleLoader == null) {
            log.debug((Object)"Searching for ruleloader...");
            this.ruleLoader = pm.findLoader(digester, this.id, this.pluginClass, this.properties);
        }
        else {
            log.debug((Object)"This declaration has an explicit ruleLoader.");
        }
        if (debug) {
            if (this.ruleLoader == null) {
                log.debug((Object)("No ruleLoader found for plugin declaration id [" + this.id + "]" + ", class [" + this.pluginClass.getClass().getName() + "]."));
            }
            else {
                log.debug((Object)("RuleLoader of type [" + this.ruleLoader.getClass().getName() + "] associated with plugin declaration" + " id [" + this.id + "]" + ", class [" + this.pluginClass.getClass().getName() + "]."));
            }
        }
        this.initialized = true;
    }
    
    public void configure(final Digester digester, final String pattern) throws PluginException {
        final Log log = digester.getLogger();
        final boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)"configure being called!");
        }
        if (!this.initialized) {
            throw new PluginAssertionFailure("Not initialized.");
        }
        if (this.ruleLoader != null) {
            this.ruleLoader.addRules(digester, pattern);
        }
    }
}
