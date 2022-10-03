package org.apache.commons.digester.plugins;

import org.apache.commons.digester.Rules;
import org.apache.commons.digester.Digester;

public abstract class RulesFactory
{
    public abstract Rules newRules(final Digester p0, final Class p1) throws PluginException;
}
