package org.apache.commons.digester.plugins.strategies;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.RuleLoader;
import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.RuleFinder;

public class FinderFromResource extends RuleFinder
{
    public static String DFLT_RESOURCE_ATTR;
    private String resourceAttr;
    
    public FinderFromResource() {
        this(FinderFromResource.DFLT_RESOURCE_ATTR);
    }
    
    public FinderFromResource(final String resourceAttr) {
        this.resourceAttr = resourceAttr;
    }
    
    public RuleLoader findLoader(final Digester d, final Class pluginClass, final Properties p) throws PluginException {
        final String resourceName = p.getProperty(this.resourceAttr);
        if (resourceName == null) {
            return null;
        }
        final InputStream is = pluginClass.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            throw new PluginException("Resource " + resourceName + " not found.");
        }
        return loadRules(d, pluginClass, is, resourceName);
    }
    
    public static RuleLoader loadRules(final Digester d, final Class pluginClass, final InputStream is, final String resourceName) throws PluginException {
        try {
            final RuleLoader loader = new LoaderFromStream(is);
            return loader;
        }
        catch (final Exception e) {
            throw new PluginException("Unable to load xmlrules from resource [" + resourceName + "]", e);
        }
        finally {
            try {
                is.close();
            }
            catch (final IOException ioe) {
                throw new PluginException("Unable to close stream for resource [" + resourceName + "]", ioe);
            }
        }
    }
    
    static {
        FinderFromResource.DFLT_RESOURCE_ATTR = "resource";
    }
}
