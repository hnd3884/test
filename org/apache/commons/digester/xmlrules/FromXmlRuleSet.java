package org.apache.commons.digester.xmlrules;

import org.apache.commons.digester.RuleSet;
import org.xml.sax.InputSource;
import java.net.URL;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class FromXmlRuleSet extends RuleSetBase
{
    public static final String DIGESTER_DTD_PATH = "org/apache/commons/digester/xmlrules/digester-rules.dtd";
    private XMLRulesLoader rulesLoader;
    private DigesterRuleParser parser;
    private Digester rulesDigester;
    
    public FromXmlRuleSet(final URL rulesXml) {
        this(rulesXml, new DigesterRuleParser(), new Digester());
    }
    
    public FromXmlRuleSet(final URL rulesXml, final Digester rulesDigester) {
        this(rulesXml, new DigesterRuleParser(), rulesDigester);
    }
    
    public FromXmlRuleSet(final URL rulesXml, final DigesterRuleParser parser) {
        this(rulesXml, parser, new Digester());
    }
    
    public FromXmlRuleSet(final URL rulesXml, final DigesterRuleParser parser, final Digester rulesDigester) {
        this.init(new URLXMLRulesLoader(rulesXml), parser, rulesDigester);
    }
    
    public FromXmlRuleSet(final InputSource inputSource) {
        this(inputSource, new DigesterRuleParser(), new Digester());
    }
    
    public FromXmlRuleSet(final InputSource inputSource, final Digester rulesDigester) {
        this(inputSource, new DigesterRuleParser(), rulesDigester);
    }
    
    public FromXmlRuleSet(final InputSource inputSource, final DigesterRuleParser parser) {
        this(inputSource, parser, new Digester());
    }
    
    public FromXmlRuleSet(final InputSource inputSource, final DigesterRuleParser parser, final Digester rulesDigester) {
        this.init(new InputSourceXMLRulesLoader(inputSource), parser, rulesDigester);
    }
    
    private void init(final XMLRulesLoader rulesLoader, final DigesterRuleParser parser, final Digester rulesDigester) {
        this.rulesLoader = rulesLoader;
        this.parser = parser;
        this.rulesDigester = rulesDigester;
    }
    
    public void addRuleInstances(final Digester digester) throws XmlLoadException {
        this.addRuleInstances(digester, null);
    }
    
    public void addRuleInstances(final Digester digester, final String basePath) throws XmlLoadException {
        final URL dtdURL = this.getClass().getClassLoader().getResource("org/apache/commons/digester/xmlrules/digester-rules.dtd");
        if (dtdURL == null) {
            throw new XmlLoadException("Cannot find resource \"org/apache/commons/digester/xmlrules/digester-rules.dtd\"");
        }
        this.parser.setDigesterRulesDTD(dtdURL.toString());
        this.parser.setTarget(digester);
        this.parser.setBasePath(basePath);
        this.rulesDigester.addRuleSet(this.parser);
        this.rulesDigester.push(this.parser);
        this.rulesLoader.loadRules();
    }
    
    private abstract static class XMLRulesLoader
    {
        public abstract void loadRules() throws XmlLoadException;
    }
    
    private class URLXMLRulesLoader extends XMLRulesLoader
    {
        private URL url;
        
        public URLXMLRulesLoader(final URL url) {
            this.url = url;
        }
        
        public void loadRules() throws XmlLoadException {
            try {
                FromXmlRuleSet.this.rulesDigester.parse(this.url.openStream());
            }
            catch (final Exception ex) {
                throw new XmlLoadException(ex);
            }
        }
    }
    
    private class InputSourceXMLRulesLoader extends XMLRulesLoader
    {
        private InputSource inputSource;
        
        public InputSourceXMLRulesLoader(final InputSource inputSource) {
            this.inputSource = inputSource;
        }
        
        public void loadRules() throws XmlLoadException {
            try {
                FromXmlRuleSet.this.rulesDigester.parse(this.inputSource);
            }
            catch (final Exception ex) {
                throw new XmlLoadException(ex);
            }
        }
    }
}
