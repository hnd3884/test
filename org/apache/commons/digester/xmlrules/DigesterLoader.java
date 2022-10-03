package org.apache.commons.digester.xmlrules;

import java.io.Reader;
import java.io.InputStream;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;

public class DigesterLoader
{
    public static Digester createDigester(final InputSource rulesSource) {
        final RuleSet ruleSet = new FromXmlRuleSet(rulesSource);
        final Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }
    
    public static Digester createDigester(final InputSource rulesSource, final Digester rulesDigester) {
        final RuleSet ruleSet = new FromXmlRuleSet(rulesSource, rulesDigester);
        final Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }
    
    public static Digester createDigester(final URL rulesXml) {
        final RuleSet ruleSet = new FromXmlRuleSet(rulesXml);
        final Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }
    
    public static Digester createDigester(final URL rulesXml, final Digester rulesDigester) {
        final RuleSet ruleSet = new FromXmlRuleSet(rulesXml, rulesDigester);
        final Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }
    
    public static Object load(final URL digesterRules, final ClassLoader classLoader, final URL fileURL) throws IOException, SAXException, DigesterLoadingException {
        return load(digesterRules, classLoader, fileURL.openStream());
    }
    
    public static Object load(final URL digesterRules, final ClassLoader classLoader, final InputStream input) throws IOException, SAXException, DigesterLoadingException {
        final Digester digester = createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        try {
            return digester.parse(input);
        }
        catch (final XmlLoadException ex) {
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }
    
    public static Object load(final URL digesterRules, final ClassLoader classLoader, final Reader reader) throws IOException, SAXException, DigesterLoadingException {
        final Digester digester = createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        try {
            return digester.parse(reader);
        }
        catch (final XmlLoadException ex) {
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }
    
    public static Object load(final URL digesterRules, final ClassLoader classLoader, final URL fileURL, final Object rootObject) throws IOException, SAXException, DigesterLoadingException {
        return load(digesterRules, classLoader, fileURL.openStream(), rootObject);
    }
    
    public static Object load(final URL digesterRules, final ClassLoader classLoader, final InputStream input, final Object rootObject) throws IOException, SAXException, DigesterLoadingException {
        final Digester digester = createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        digester.push(rootObject);
        try {
            return digester.parse(input);
        }
        catch (final XmlLoadException ex) {
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }
    
    public static Object load(final URL digesterRules, final ClassLoader classLoader, final Reader input, final Object rootObject) throws IOException, SAXException, DigesterLoadingException {
        final Digester digester = createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        digester.push(rootObject);
        try {
            return digester.parse(input);
        }
        catch (final XmlLoadException ex) {
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }
}
