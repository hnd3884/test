package org.apache.commons.digester.plugins;

import java.util.Properties;
import org.apache.commons.digester.Digester;

public abstract class RuleFinder
{
    public abstract RuleLoader findLoader(final Digester p0, final Class p1, final Properties p2) throws PluginException;
}
