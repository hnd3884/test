package org.apache.commons.digester.plugins.strategies;

import org.apache.commons.digester.plugins.RuleLoader;
import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.RuleFinder;

public class FinderSetProperties extends RuleFinder
{
    public static String DFLT_PROPS_ATTR;
    public static String DFLT_FALSEVAL;
    private String propsAttr;
    private String falseval;
    
    public FinderSetProperties() {
        this(FinderSetProperties.DFLT_PROPS_ATTR, FinderSetProperties.DFLT_FALSEVAL);
    }
    
    public FinderSetProperties(final String propsAttr, final String falseval) {
        this.propsAttr = propsAttr;
        this.falseval = falseval;
    }
    
    public RuleLoader findLoader(final Digester d, final Class pluginClass, final Properties p) {
        final String state = p.getProperty(this.propsAttr);
        if (state != null && state.equals(this.falseval)) {
            return null;
        }
        return new LoaderSetProperties();
    }
    
    static {
        FinderSetProperties.DFLT_PROPS_ATTR = "setprops";
        FinderSetProperties.DFLT_FALSEVAL = "false";
    }
}
