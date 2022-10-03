package org.apache.commons.digester.plugins;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.digester.Digester;
import java.util.HashMap;

public class PluginManager
{
    private HashMap declarationsByClass;
    private HashMap declarationsById;
    private PluginManager parent;
    private PluginContext pluginContext;
    
    public PluginManager(final PluginContext r) {
        this.declarationsByClass = new HashMap();
        this.declarationsById = new HashMap();
        this.pluginContext = r;
    }
    
    public PluginManager(final PluginManager parent) {
        this.declarationsByClass = new HashMap();
        this.declarationsById = new HashMap();
        this.parent = parent;
        this.pluginContext = parent.pluginContext;
    }
    
    public void addDeclaration(final Declaration decl) {
        final Log log = LogUtils.getLogger(null);
        final boolean debug = log.isDebugEnabled();
        final Class pluginClass = decl.getPluginClass();
        final String id = decl.getId();
        this.declarationsByClass.put(pluginClass.getName(), decl);
        if (id != null) {
            this.declarationsById.put(id, decl);
            if (debug) {
                log.debug((Object)("Indexing plugin-id [" + id + "]" + " -> class [" + pluginClass.getName() + "]"));
            }
        }
    }
    
    public Declaration getDeclarationByClass(final String className) {
        Declaration decl = this.declarationsByClass.get(className);
        if (decl == null && this.parent != null) {
            decl = this.parent.getDeclarationByClass(className);
        }
        return decl;
    }
    
    public Declaration getDeclarationById(final String id) {
        Declaration decl = this.declarationsById.get(id);
        if (decl == null && this.parent != null) {
            decl = this.parent.getDeclarationById(id);
        }
        return decl;
    }
    
    public RuleLoader findLoader(final Digester digester, final String id, final Class pluginClass, final Properties props) throws PluginException {
        final Log log = LogUtils.getLogger(digester);
        final boolean debug = log.isDebugEnabled();
        log.debug((Object)"scanning ruleFinders to locate loader..");
        final List ruleFinders = this.pluginContext.getRuleFinders();
        RuleLoader ruleLoader = null;
        try {
            RuleFinder finder;
            for (Iterator i = ruleFinders.iterator(); i.hasNext() && ruleLoader == null; ruleLoader = finder.findLoader(digester, pluginClass, props)) {
                finder = i.next();
                if (debug) {
                    log.debug((Object)("checking finder of type " + finder.getClass().getName()));
                }
            }
        }
        catch (final PluginException e) {
            throw new PluginException("Unable to locate plugin rules for plugin with id [" + id + "]" + ", and class [" + pluginClass.getName() + "]" + ":" + e.getMessage(), e.getCause());
        }
        log.debug((Object)"scanned ruleFinders.");
        return ruleLoader;
    }
}
