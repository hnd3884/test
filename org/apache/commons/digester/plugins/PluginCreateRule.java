package org.apache.commons.digester.plugins;

import java.util.List;
import org.apache.commons.digester.Rules;
import org.xml.sax.Attributes;
import org.apache.commons.logging.Log;
import org.apache.commons.digester.Rule;

public class PluginCreateRule extends Rule implements InitializableRule
{
    private String pluginClassAttrNs;
    private String pluginClassAttr;
    private String pluginIdAttrNs;
    private String pluginIdAttr;
    private String pattern;
    private Class baseClass;
    private Declaration defaultPlugin;
    private PluginConfigurationException initException;
    
    public PluginCreateRule(final Class baseClass) {
        this.pluginClassAttrNs = null;
        this.pluginClassAttr = null;
        this.pluginIdAttrNs = null;
        this.pluginIdAttr = null;
        this.baseClass = null;
        this.baseClass = baseClass;
    }
    
    public PluginCreateRule(final Class baseClass, final Class dfltPluginClass) {
        this.pluginClassAttrNs = null;
        this.pluginClassAttr = null;
        this.pluginIdAttrNs = null;
        this.pluginIdAttr = null;
        this.baseClass = null;
        this.baseClass = baseClass;
        if (dfltPluginClass != null) {
            this.defaultPlugin = new Declaration(dfltPluginClass);
        }
    }
    
    public PluginCreateRule(final Class baseClass, final Class dfltPluginClass, final RuleLoader dfltPluginRuleLoader) {
        this.pluginClassAttrNs = null;
        this.pluginClassAttr = null;
        this.pluginIdAttrNs = null;
        this.pluginIdAttr = null;
        this.baseClass = null;
        this.baseClass = baseClass;
        if (dfltPluginClass != null) {
            this.defaultPlugin = new Declaration(dfltPluginClass, dfltPluginRuleLoader);
        }
    }
    
    public void setPluginClassAttribute(final String namespaceUri, final String attrName) {
        this.pluginClassAttrNs = namespaceUri;
        this.pluginClassAttr = attrName;
    }
    
    public void setPluginIdAttribute(final String namespaceUri, final String attrName) {
        this.pluginIdAttrNs = namespaceUri;
        this.pluginIdAttr = attrName;
    }
    
    public void postRegisterInit(final String matchPattern) throws PluginConfigurationException {
        final Log log = LogUtils.getLogger(this.digester);
        final boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("PluginCreateRule.postRegisterInit: rule registered for pattern [" + matchPattern + "]"));
        }
        if (this.digester == null) {
            throw this.initException = new PluginConfigurationException("Invalid invocation of postRegisterInit: digester not set.");
        }
        if (this.pattern != null) {
            throw this.initException = new PluginConfigurationException("A single PluginCreateRule instance has been mapped to multiple patterns; this is not supported.");
        }
        if (matchPattern.indexOf(42) != -1) {
            throw this.initException = new PluginConfigurationException("A PluginCreateRule instance has been mapped to pattern [" + matchPattern + "]." + " This pattern includes a wildcard character." + " This is not supported by the plugin architecture.");
        }
        if (this.baseClass == null) {
            this.baseClass = Object.class;
        }
        final PluginRules rules = (PluginRules)this.digester.getRules();
        final PluginManager pm = rules.getPluginManager();
        if (this.defaultPlugin != null) {
            if (!this.baseClass.isAssignableFrom(this.defaultPlugin.getPluginClass())) {
                throw this.initException = new PluginConfigurationException("Default class [" + this.defaultPlugin.getPluginClass().getName() + "] does not inherit from [" + this.baseClass.getName() + "].");
            }
            try {
                this.defaultPlugin.init(this.digester, pm);
            }
            catch (final PluginException pwe) {
                throw new PluginConfigurationException(pwe.getMessage(), pwe.getCause());
            }
        }
        this.pattern = matchPattern;
        if (this.pluginClassAttr == null) {
            this.pluginClassAttrNs = rules.getPluginClassAttrNs();
            this.pluginClassAttr = rules.getPluginClassAttr();
            if (debug) {
                log.debug((Object)("init: pluginClassAttr set to per-digester values [ns=" + this.pluginClassAttrNs + ", name=" + this.pluginClassAttr + "]"));
            }
        }
        else if (debug) {
            log.debug((Object)("init: pluginClassAttr set to rule-specific values [ns=" + this.pluginClassAttrNs + ", name=" + this.pluginClassAttr + "]"));
        }
        if (this.pluginIdAttr == null) {
            this.pluginIdAttrNs = rules.getPluginIdAttrNs();
            this.pluginIdAttr = rules.getPluginIdAttr();
            if (debug) {
                log.debug((Object)("init: pluginIdAttr set to per-digester values [ns=" + this.pluginIdAttrNs + ", name=" + this.pluginIdAttr + "]"));
            }
        }
        else if (debug) {
            log.debug((Object)("init: pluginIdAttr set to rule-specific values [ns=" + this.pluginIdAttrNs + ", name=" + this.pluginIdAttr + "]"));
        }
    }
    
    public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
        final Log log = this.digester.getLogger();
        final boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("PluginCreateRule.begin: pattern=[" + this.pattern + "]" + " match=[" + this.digester.getMatch() + "]"));
        }
        if (this.initException != null) {
            throw this.initException;
        }
        final PluginRules oldRules = (PluginRules)this.digester.getRules();
        final PluginManager pluginManager = oldRules.getPluginManager();
        Declaration currDeclaration = null;
        String pluginClassName;
        if (this.pluginClassAttrNs == null) {
            pluginClassName = attributes.getValue(this.pluginClassAttr);
        }
        else {
            pluginClassName = attributes.getValue(this.pluginClassAttrNs, this.pluginClassAttr);
        }
        String pluginId;
        if (this.pluginIdAttrNs == null) {
            pluginId = attributes.getValue(this.pluginIdAttr);
        }
        else {
            pluginId = attributes.getValue(this.pluginIdAttrNs, this.pluginIdAttr);
        }
        if (pluginClassName != null) {
            currDeclaration = pluginManager.getDeclarationByClass(pluginClassName);
            if (currDeclaration == null) {
                currDeclaration = new Declaration(pluginClassName);
                try {
                    currDeclaration.init(this.digester, pluginManager);
                }
                catch (final PluginException pwe) {
                    throw new PluginInvalidInputException(pwe.getMessage(), pwe.getCause());
                }
                pluginManager.addDeclaration(currDeclaration);
            }
        }
        else if (pluginId != null) {
            currDeclaration = pluginManager.getDeclarationById(pluginId);
            if (currDeclaration == null) {
                throw new PluginInvalidInputException("Plugin id [" + pluginId + "] is not defined.");
            }
        }
        else {
            if (this.defaultPlugin == null) {
                throw new PluginInvalidInputException("No plugin class specified for element " + this.pattern);
            }
            currDeclaration = this.defaultPlugin;
        }
        final Class pluginClass = currDeclaration.getPluginClass();
        final String path = this.digester.getMatch();
        final PluginRules newRules = new PluginRules(this.digester, path, oldRules, pluginClass);
        this.digester.setRules(newRules);
        if (debug) {
            log.debug((Object)("PluginCreateRule.begin: installing new plugin: oldrules=" + oldRules.toString() + ", newrules=" + newRules.toString()));
        }
        currDeclaration.configure(this.digester, this.pattern);
        final Object instance = pluginClass.newInstance();
        this.getDigester().push(instance);
        if (debug) {
            log.debug((Object)("PluginCreateRule.begin: pattern=[" + this.pattern + "]" + " match=[" + this.digester.getMatch() + "]" + " pushed instance of plugin [" + pluginClass.getName() + "]"));
        }
        final List rules = newRules.getDecoratedRules().match(namespace, path);
        this.fireBeginMethods(rules, namespace, name, attributes);
    }
    
    public void body(final String namespace, final String name, final String text) throws Exception {
        final String path = this.digester.getMatch();
        final PluginRules newRules = (PluginRules)this.digester.getRules();
        final List rules = newRules.getDecoratedRules().match(namespace, path);
        this.fireBodyMethods(rules, namespace, name, text);
    }
    
    public void end(final String namespace, final String name) throws Exception {
        final String path = this.digester.getMatch();
        final PluginRules newRules = (PluginRules)this.digester.getRules();
        final List rules = newRules.getDecoratedRules().match(namespace, path);
        this.fireEndMethods(rules, namespace, name);
        this.digester.setRules(newRules.getParent());
        this.digester.pop();
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public void fireBeginMethods(final List rules, final String namespace, final String name, final Attributes list) throws Exception {
        if (rules != null && rules.size() > 0) {
            final Log log = this.digester.getLogger();
            final boolean debug = log.isDebugEnabled();
            for (int i = 0; i < rules.size(); ++i) {
                try {
                    final Rule rule = rules.get(i);
                    if (debug) {
                        log.debug((Object)("  Fire begin() for " + rule));
                    }
                    rule.begin(namespace, name, list);
                }
                catch (final Exception e) {
                    throw this.digester.createSAXException(e);
                }
                catch (final Error e2) {
                    throw e2;
                }
            }
        }
    }
    
    private void fireBodyMethods(final List rules, final String namespaceURI, final String name, final String text) throws Exception {
        if (rules != null && rules.size() > 0) {
            final Log log = this.digester.getLogger();
            final boolean debug = log.isDebugEnabled();
            for (int i = 0; i < rules.size(); ++i) {
                try {
                    final Rule rule = rules.get(i);
                    if (debug) {
                        log.debug((Object)("  Fire body() for " + rule));
                    }
                    rule.body(namespaceURI, name, text);
                }
                catch (final Exception e) {
                    throw this.digester.createSAXException(e);
                }
                catch (final Error e2) {
                    throw e2;
                }
            }
        }
    }
    
    public void fireEndMethods(final List rules, final String namespaceURI, final String name) throws Exception {
        if (rules != null) {
            final Log log = this.digester.getLogger();
            final boolean debug = log.isDebugEnabled();
            for (int i = 0; i < rules.size(); ++i) {
                final int j = rules.size() - i - 1;
                try {
                    final Rule rule = rules.get(j);
                    if (debug) {
                        log.debug((Object)("  Fire end() for " + rule));
                    }
                    rule.end(namespaceURI, name);
                }
                catch (final Exception e) {
                    throw this.digester.createSAXException(e);
                }
                catch (final Error e2) {
                    throw e2;
                }
            }
        }
    }
}
