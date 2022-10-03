package org.apache.commons.digester.plugins.strategies;

import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.RuleLoader;
import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.RuleFinder;

public class FinderFromMethod extends RuleFinder
{
    public static String DFLT_METHOD_ATTR;
    private String methodAttr;
    
    public FinderFromMethod() {
        this(FinderFromMethod.DFLT_METHOD_ATTR);
    }
    
    public FinderFromMethod(final String methodAttr) {
        this.methodAttr = methodAttr;
    }
    
    public RuleLoader findLoader(final Digester d, final Class pluginClass, final Properties p) throws PluginException {
        final String methodName = p.getProperty(this.methodAttr);
        if (methodName == null) {
            return null;
        }
        return new LoaderFromClass(pluginClass, methodName);
    }
    
    static {
        FinderFromMethod.DFLT_METHOD_ATTR = "method";
    }
}
