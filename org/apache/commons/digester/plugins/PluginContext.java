package org.apache.commons.digester.plugins;

import org.apache.commons.digester.plugins.strategies.FinderSetProperties;
import org.apache.commons.digester.plugins.strategies.FinderFromDfltResource;
import org.apache.commons.digester.plugins.strategies.FinderFromDfltClass;
import org.apache.commons.digester.plugins.strategies.FinderFromDfltMethod;
import org.apache.commons.digester.plugins.strategies.FinderFromMethod;
import org.apache.commons.digester.plugins.strategies.FinderFromClass;
import org.apache.commons.digester.plugins.strategies.FinderFromResource;
import org.apache.commons.digester.plugins.strategies.FinderFromFile;
import java.util.LinkedList;
import java.util.List;

public class PluginContext
{
    public final String DFLT_PLUGIN_CLASS_ATTR_NS;
    public final String DFLT_PLUGIN_CLASS_ATTR = "plugin-class";
    public final String DFLT_PLUGIN_ID_ATTR_NS;
    public final String DFLT_PLUGIN_ID_ATTR = "plugin-id";
    private String pluginClassAttrNs;
    private String pluginClassAttr;
    private String pluginIdAttrNs;
    private String pluginIdAttr;
    private List ruleFinders;
    
    public PluginContext() {
        this.DFLT_PLUGIN_CLASS_ATTR_NS = null;
        this.DFLT_PLUGIN_ID_ATTR_NS = null;
        this.pluginClassAttrNs = this.DFLT_PLUGIN_CLASS_ATTR_NS;
        this.pluginClassAttr = "plugin-class";
        this.pluginIdAttrNs = this.DFLT_PLUGIN_ID_ATTR_NS;
        this.pluginIdAttr = "plugin-id";
    }
    
    public List getRuleFinders() {
        if (this.ruleFinders == null) {
            (this.ruleFinders = new LinkedList()).add(new FinderFromFile());
            this.ruleFinders.add(new FinderFromResource());
            this.ruleFinders.add(new FinderFromClass());
            this.ruleFinders.add(new FinderFromMethod());
            this.ruleFinders.add(new FinderFromDfltMethod());
            this.ruleFinders.add(new FinderFromDfltClass());
            this.ruleFinders.add(new FinderFromDfltResource());
            this.ruleFinders.add(new FinderFromDfltResource(".xml"));
            this.ruleFinders.add(new FinderSetProperties());
        }
        return this.ruleFinders;
    }
    
    public void setRuleFinders(final List ruleFinders) {
        this.ruleFinders = ruleFinders;
    }
    
    public void setPluginClassAttribute(final String namespaceUri, final String attrName) {
        this.pluginClassAttrNs = namespaceUri;
        this.pluginClassAttr = attrName;
    }
    
    public void setPluginIdAttribute(final String namespaceUri, final String attrName) {
        this.pluginIdAttrNs = namespaceUri;
        this.pluginIdAttr = attrName;
    }
    
    public String getPluginClassAttrNs() {
        return this.pluginClassAttrNs;
    }
    
    public String getPluginClassAttr() {
        return this.pluginClassAttr;
    }
    
    public String getPluginIdAttrNs() {
        return this.pluginIdAttrNs;
    }
    
    public String getPluginIdAttr() {
        return this.pluginIdAttr;
    }
}
