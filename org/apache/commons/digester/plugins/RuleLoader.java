package org.apache.commons.digester.plugins;

import org.apache.commons.digester.Digester;

public abstract class RuleLoader
{
    public abstract void addRules(final Digester p0, final String p1) throws PluginException;
}
