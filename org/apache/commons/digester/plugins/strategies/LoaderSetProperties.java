package org.apache.commons.digester.plugins.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.RuleLoader;

public class LoaderSetProperties extends RuleLoader
{
    public void addRules(final Digester digester, final String path) {
        final Log log = digester.getLogger();
        final boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("LoaderSetProperties loading rules for plugin at path [" + path + "]"));
        }
        digester.addSetProperties(path);
    }
}
