package org.apache.commons.chain.config;

import java.net.URL;
import org.apache.commons.chain.Catalog;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.digester.Digester;

public class ConfigParser
{
    private Digester digester;
    private RuleSet ruleSet;
    private boolean useContextClassLoader;
    
    public ConfigParser() {
        this.digester = null;
        this.ruleSet = null;
        this.useContextClassLoader = true;
    }
    
    public Digester getDigester() {
        if (this.digester == null) {
            this.digester = new Digester();
            final RuleSet ruleSet = this.getRuleSet();
            this.digester.setNamespaceAware(ruleSet.getNamespaceURI() != null);
            this.digester.setUseContextClassLoader(this.getUseContextClassLoader());
            this.digester.setValidating(false);
            this.digester.addRuleSet(ruleSet);
        }
        return this.digester;
    }
    
    public RuleSet getRuleSet() {
        if (this.ruleSet == null) {
            this.ruleSet = (RuleSet)new ConfigRuleSet();
        }
        return this.ruleSet;
    }
    
    public void setRuleSet(final RuleSet ruleSet) {
        this.digester = null;
        this.ruleSet = ruleSet;
    }
    
    public boolean getUseContextClassLoader() {
        return this.useContextClassLoader;
    }
    
    public void setUseContextClassLoader(final boolean useContextClassLoader) {
        this.useContextClassLoader = useContextClassLoader;
    }
    
    public void parse(final Catalog catalog, final URL url) throws Exception {
        final Digester digester = this.getDigester();
        digester.clear();
        digester.push((Object)catalog);
        digester.parse(url);
    }
    
    public void parse(final URL url) throws Exception {
        final Digester digester = this.getDigester();
        digester.clear();
        digester.parse(url);
    }
}
