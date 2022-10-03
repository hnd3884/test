package org.apache.commons.digester;

import java.util.Hashtable;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.xml.sax.SAXParseException;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import java.util.Iterator;
import java.util.Map;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import java.util.Properties;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import javax.xml.parsers.ParserConfigurationException;
import java.util.EmptyStackException;
import java.util.ArrayList;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import org.xml.sax.ContentHandler;
import org.apache.commons.logging.Log;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Locator;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ErrorHandler;
import java.util.HashMap;
import org.xml.sax.EntityResolver;
import org.apache.commons.collections.ArrayStack;
import org.xml.sax.helpers.DefaultHandler;

public class Digester extends DefaultHandler
{
    protected StringBuffer bodyText;
    protected ArrayStack bodyTexts;
    protected ArrayStack matches;
    protected ClassLoader classLoader;
    protected boolean configured;
    protected EntityResolver entityResolver;
    protected HashMap entityValidator;
    protected ErrorHandler errorHandler;
    protected SAXParserFactory factory;
    protected String JAXP_SCHEMA_LANGUAGE;
    protected Locator locator;
    protected String match;
    protected boolean namespaceAware;
    protected HashMap namespaces;
    protected ArrayStack params;
    protected SAXParser parser;
    protected String publicId;
    protected XMLReader reader;
    protected Object root;
    protected Rules rules;
    protected String schemaLanguage;
    protected String schemaLocation;
    protected ArrayStack stack;
    protected boolean useContextClassLoader;
    protected boolean validating;
    protected Log log;
    protected Log saxLog;
    protected static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    protected Substitutor substitutor;
    private HashMap stacksByName;
    private ContentHandler customContentHandler;
    private StackAction stackAction;
    protected List inputSources;
    
    public Digester() {
        this.bodyText = new StringBuffer();
        this.bodyTexts = new ArrayStack();
        this.matches = new ArrayStack(10);
        this.classLoader = null;
        this.configured = false;
        this.entityValidator = new HashMap();
        this.errorHandler = null;
        this.factory = null;
        this.JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
        this.locator = null;
        this.match = "";
        this.namespaceAware = false;
        this.namespaces = new HashMap();
        this.params = new ArrayStack();
        this.parser = null;
        this.publicId = null;
        this.reader = null;
        this.root = null;
        this.rules = null;
        this.schemaLanguage = "http://www.w3.org/2001/XMLSchema";
        this.schemaLocation = null;
        this.stack = new ArrayStack();
        this.useContextClassLoader = false;
        this.validating = false;
        this.log = LogFactory.getLog("org.apache.commons.digester.Digester");
        this.saxLog = LogFactory.getLog("org.apache.commons.digester.Digester.sax");
        this.stacksByName = new HashMap();
        this.customContentHandler = null;
        this.stackAction = null;
        this.inputSources = new ArrayList(5);
    }
    
    public Digester(final SAXParser parser) {
        this.bodyText = new StringBuffer();
        this.bodyTexts = new ArrayStack();
        this.matches = new ArrayStack(10);
        this.classLoader = null;
        this.configured = false;
        this.entityValidator = new HashMap();
        this.errorHandler = null;
        this.factory = null;
        this.JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
        this.locator = null;
        this.match = "";
        this.namespaceAware = false;
        this.namespaces = new HashMap();
        this.params = new ArrayStack();
        this.parser = null;
        this.publicId = null;
        this.reader = null;
        this.root = null;
        this.rules = null;
        this.schemaLanguage = "http://www.w3.org/2001/XMLSchema";
        this.schemaLocation = null;
        this.stack = new ArrayStack();
        this.useContextClassLoader = false;
        this.validating = false;
        this.log = LogFactory.getLog("org.apache.commons.digester.Digester");
        this.saxLog = LogFactory.getLog("org.apache.commons.digester.Digester.sax");
        this.stacksByName = new HashMap();
        this.customContentHandler = null;
        this.stackAction = null;
        this.inputSources = new ArrayList(5);
        this.parser = parser;
    }
    
    public Digester(final XMLReader reader) {
        this.bodyText = new StringBuffer();
        this.bodyTexts = new ArrayStack();
        this.matches = new ArrayStack(10);
        this.classLoader = null;
        this.configured = false;
        this.entityValidator = new HashMap();
        this.errorHandler = null;
        this.factory = null;
        this.JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
        this.locator = null;
        this.match = "";
        this.namespaceAware = false;
        this.namespaces = new HashMap();
        this.params = new ArrayStack();
        this.parser = null;
        this.publicId = null;
        this.reader = null;
        this.root = null;
        this.rules = null;
        this.schemaLanguage = "http://www.w3.org/2001/XMLSchema";
        this.schemaLocation = null;
        this.stack = new ArrayStack();
        this.useContextClassLoader = false;
        this.validating = false;
        this.log = LogFactory.getLog("org.apache.commons.digester.Digester");
        this.saxLog = LogFactory.getLog("org.apache.commons.digester.Digester.sax");
        this.stacksByName = new HashMap();
        this.customContentHandler = null;
        this.stackAction = null;
        this.inputSources = new ArrayList(5);
        this.reader = reader;
    }
    
    public String findNamespaceURI(final String prefix) {
        final ArrayStack nsStack = this.namespaces.get(prefix);
        if (nsStack == null) {
            return null;
        }
        try {
            return (String)nsStack.peek();
        }
        catch (final EmptyStackException e) {
            return null;
        }
    }
    
    public ClassLoader getClassLoader() {
        if (this.classLoader != null) {
            return this.classLoader;
        }
        if (this.useContextClassLoader) {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                return classLoader;
            }
        }
        return this.getClass().getClassLoader();
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public int getCount() {
        return this.stack.size();
    }
    
    public String getCurrentElementName() {
        String elementName = this.match;
        final int lastSlash = elementName.lastIndexOf(47);
        if (lastSlash >= 0) {
            elementName = elementName.substring(lastSlash + 1);
        }
        return elementName;
    }
    
    public int getDebug() {
        return 0;
    }
    
    public void setDebug(final int debug) {
    }
    
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    public SAXParserFactory getFactory() {
        if (this.factory == null) {
            (this.factory = SAXParserFactory.newInstance()).setNamespaceAware(this.namespaceAware);
            this.factory.setValidating(this.validating);
        }
        return this.factory;
    }
    
    public boolean getFeature(final String feature) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        return this.getFactory().getFeature(feature);
    }
    
    public void setFeature(final String feature, final boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        this.getFactory().setFeature(feature, value);
    }
    
    public Log getLogger() {
        return this.log;
    }
    
    public void setLogger(final Log log) {
        this.log = log;
    }
    
    public Log getSAXLogger() {
        return this.saxLog;
    }
    
    public void setSAXLogger(final Log saxLog) {
        this.saxLog = saxLog;
    }
    
    public String getMatch() {
        return this.match;
    }
    
    public boolean getNamespaceAware() {
        return this.namespaceAware;
    }
    
    public void setNamespaceAware(final boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }
    
    public void setPublicId(final String publicId) {
        this.publicId = publicId;
    }
    
    public String getPublicId() {
        return this.publicId;
    }
    
    public String getRuleNamespaceURI() {
        return this.getRules().getNamespaceURI();
    }
    
    public void setRuleNamespaceURI(final String ruleNamespaceURI) {
        this.getRules().setNamespaceURI(ruleNamespaceURI);
    }
    
    public SAXParser getParser() {
        if (this.parser != null) {
            return this.parser;
        }
        try {
            if (this.validating && this.schemaLocation != null) {
                final Properties properties = new Properties();
                ((Hashtable<String, SAXParserFactory>)properties).put("SAXParserFactory", this.getFactory());
                if (this.schemaLocation != null) {
                    ((Hashtable<String, String>)properties).put("schemaLocation", this.schemaLocation);
                    ((Hashtable<String, String>)properties).put("schemaLanguage", this.schemaLanguage);
                }
                this.parser = ParserFeatureSetterFactory.newSAXParser(properties);
            }
            else {
                this.parser = this.getFactory().newSAXParser();
            }
        }
        catch (final Exception e) {
            this.log.error((Object)"Digester.getParser: ", (Throwable)e);
            return null;
        }
        return this.parser;
    }
    
    public Object getProperty(final String property) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.getParser().getProperty(property);
    }
    
    public void setProperty(final String property, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.getParser().setProperty(property, value);
    }
    
    public XMLReader getReader() {
        try {
            return this.getXMLReader();
        }
        catch (final SAXException e) {
            this.log.error((Object)"Cannot get XMLReader", (Throwable)e);
            return null;
        }
    }
    
    public Rules getRules() {
        if (this.rules == null) {
            (this.rules = new RulesBase()).setDigester(this);
        }
        return this.rules;
    }
    
    public void setRules(final Rules rules) {
        (this.rules = rules).setDigester(this);
    }
    
    public String getSchema() {
        return this.schemaLocation;
    }
    
    public void setSchema(final String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }
    
    public String getSchemaLanguage() {
        return this.schemaLanguage;
    }
    
    public void setSchemaLanguage(final String schemaLanguage) {
        this.schemaLanguage = schemaLanguage;
    }
    
    public boolean getUseContextClassLoader() {
        return this.useContextClassLoader;
    }
    
    public void setUseContextClassLoader(final boolean use) {
        this.useContextClassLoader = use;
    }
    
    public boolean getValidating() {
        return this.validating;
    }
    
    public void setValidating(final boolean validating) {
        this.validating = validating;
    }
    
    public XMLReader getXMLReader() throws SAXException {
        if (this.reader == null) {
            this.reader = this.getParser().getXMLReader();
        }
        this.reader.setDTDHandler(this);
        this.reader.setContentHandler(this);
        if (this.entityResolver == null) {
            this.reader.setEntityResolver(this);
        }
        else {
            this.reader.setEntityResolver(this.entityResolver);
        }
        this.reader.setErrorHandler(this);
        return this.reader;
    }
    
    public Substitutor getSubstitutor() {
        return this.substitutor;
    }
    
    public void setSubstitutor(final Substitutor substitutor) {
        this.substitutor = substitutor;
    }
    
    public ContentHandler getCustomContentHandler() {
        return this.customContentHandler;
    }
    
    public void setCustomContentHandler(final ContentHandler handler) {
        this.customContentHandler = handler;
    }
    
    public void setStackAction(final StackAction stackAction) {
        this.stackAction = stackAction;
    }
    
    public StackAction getStackAction() {
        return this.stackAction;
    }
    
    public Map getCurrentNamespaces() {
        if (!this.namespaceAware) {
            this.log.warn((Object)"Digester is not namespace aware");
        }
        final Map currentNamespaces = new HashMap();
        final Iterator nsIterator = this.namespaces.entrySet().iterator();
        while (nsIterator.hasNext()) {
            final Map.Entry nsEntry = nsIterator.next();
            try {
                currentNamespaces.put(nsEntry.getKey(), nsEntry.getValue().peek());
            }
            catch (final RuntimeException e) {
                this.log.error((Object)e.getMessage(), (Throwable)e);
                throw e;
            }
        }
        return currentNamespaces;
    }
    
    public void characters(final char[] buffer, final int start, final int length) throws SAXException {
        if (this.customContentHandler != null) {
            this.customContentHandler.characters(buffer, start, length);
            return;
        }
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("characters(" + new String(buffer, start, length) + ")"));
        }
        this.bodyText.append(buffer, start, length);
    }
    
    public void endDocument() throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            if (this.getCount() > 1) {
                this.saxLog.debug((Object)("endDocument():  " + this.getCount() + " elements left"));
            }
            else {
                this.saxLog.debug((Object)"endDocument()");
            }
        }
        final Iterator rules = this.getRules().rules().iterator();
        while (rules.hasNext()) {
            final Rule rule = rules.next();
            try {
                rule.finish();
            }
            catch (final Exception e) {
                this.log.error((Object)"Finish event threw exception", (Throwable)e);
                throw this.createSAXException(e);
            }
            catch (final Error e2) {
                this.log.error((Object)"Finish event threw error", (Throwable)e2);
                throw e2;
            }
        }
        this.clear();
    }
    
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        if (this.customContentHandler != null) {
            this.customContentHandler.endElement(namespaceURI, localName, qName);
            return;
        }
        final boolean debug = this.log.isDebugEnabled();
        if (debug) {
            if (this.saxLog.isDebugEnabled()) {
                this.saxLog.debug((Object)("endElement(" + namespaceURI + "," + localName + "," + qName + ")"));
            }
            this.log.debug((Object)("  match='" + this.match + "'"));
            this.log.debug((Object)("  bodyText='" + (Object)this.bodyText + "'"));
        }
        String name = localName;
        if (name == null || name.length() < 1) {
            name = qName;
        }
        final List rules = (List)this.matches.pop();
        if (rules != null && rules.size() > 0) {
            String bodyText = this.bodyText.toString();
            final Substitutor substitutor = this.getSubstitutor();
            if (substitutor != null) {
                bodyText = substitutor.substitute(bodyText);
            }
            for (int i = 0; i < rules.size(); ++i) {
                try {
                    final Rule rule = rules.get(i);
                    if (debug) {
                        this.log.debug((Object)("  Fire body() for " + rule));
                    }
                    rule.body(namespaceURI, name, bodyText);
                }
                catch (final Exception e) {
                    this.log.error((Object)"Body event threw exception", (Throwable)e);
                    throw this.createSAXException(e);
                }
                catch (final Error e2) {
                    this.log.error((Object)"Body event threw error", (Throwable)e2);
                    throw e2;
                }
            }
        }
        else if (debug) {
            this.log.debug((Object)("  No rules found matching '" + this.match + "'."));
        }
        this.bodyText = (StringBuffer)this.bodyTexts.pop();
        if (debug) {
            this.log.debug((Object)("  Popping body text '" + this.bodyText.toString() + "'"));
        }
        if (rules != null) {
            for (int j = 0; j < rules.size(); ++j) {
                final int k = rules.size() - j - 1;
                try {
                    final Rule rule2 = rules.get(k);
                    if (debug) {
                        this.log.debug((Object)("  Fire end() for " + rule2));
                    }
                    rule2.end(namespaceURI, name);
                }
                catch (final Exception e3) {
                    this.log.error((Object)"End event threw exception", (Throwable)e3);
                    throw this.createSAXException(e3);
                }
                catch (final Error e4) {
                    this.log.error((Object)"End event threw error", (Throwable)e4);
                    throw e4;
                }
            }
        }
        final int slash = this.match.lastIndexOf(47);
        if (slash >= 0) {
            this.match = this.match.substring(0, slash);
        }
        else {
            this.match = "";
        }
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("endPrefixMapping(" + prefix + ")"));
        }
        final ArrayStack stack = this.namespaces.get(prefix);
        if (stack == null) {
            return;
        }
        try {
            stack.pop();
            if (stack.empty()) {
                this.namespaces.remove(prefix);
            }
        }
        catch (final EmptyStackException e) {
            throw this.createSAXException("endPrefixMapping popped too many times");
        }
    }
    
    public void ignorableWhitespace(final char[] buffer, final int start, final int len) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("ignorableWhitespace(" + new String(buffer, start, len) + ")"));
        }
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.customContentHandler != null) {
            this.customContentHandler.processingInstruction(target, data);
            return;
        }
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("processingInstruction('" + target + "','" + data + "')"));
        }
    }
    
    public Locator getDocumentLocator() {
        return this.locator;
    }
    
    public void setDocumentLocator(final Locator locator) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("setDocumentLocator(" + locator + ")"));
        }
        this.locator = locator;
    }
    
    public void skippedEntity(final String name) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("skippedEntity(" + name + ")"));
        }
    }
    
    public void startDocument() throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)"startDocument()");
        }
        this.configure();
    }
    
    public void startElement(final String namespaceURI, final String localName, final String qName, Attributes list) throws SAXException {
        final boolean debug = this.log.isDebugEnabled();
        if (this.customContentHandler != null) {
            this.customContentHandler.startElement(namespaceURI, localName, qName, list);
            return;
        }
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("startElement(" + namespaceURI + "," + localName + "," + qName + ")"));
        }
        this.bodyTexts.push((Object)this.bodyText);
        if (debug) {
            this.log.debug((Object)("  Pushing body text '" + this.bodyText.toString() + "'"));
        }
        this.bodyText = new StringBuffer();
        String name = localName;
        if (name == null || name.length() < 1) {
            name = qName;
        }
        final StringBuffer sb = new StringBuffer(this.match);
        if (this.match.length() > 0) {
            sb.append('/');
        }
        sb.append(name);
        this.match = sb.toString();
        if (debug) {
            this.log.debug((Object)("  New match='" + this.match + "'"));
        }
        final List rules = this.getRules().match(namespaceURI, this.match);
        this.matches.push((Object)rules);
        if (rules != null && rules.size() > 0) {
            final Substitutor substitutor = this.getSubstitutor();
            if (substitutor != null) {
                list = substitutor.substitute(list);
            }
            for (int i = 0; i < rules.size(); ++i) {
                try {
                    final Rule rule = rules.get(i);
                    if (debug) {
                        this.log.debug((Object)("  Fire begin() for " + rule));
                    }
                    rule.begin(namespaceURI, name, list);
                }
                catch (final Exception e) {
                    this.log.error((Object)"Begin event threw exception", (Throwable)e);
                    throw this.createSAXException(e);
                }
                catch (final Error e2) {
                    this.log.error((Object)"Begin event threw error", (Throwable)e2);
                    throw e2;
                }
            }
        }
        else if (debug) {
            this.log.debug((Object)("  No rules found matching '" + this.match + "'."));
        }
    }
    
    public void startPrefixMapping(final String prefix, final String namespaceURI) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("startPrefixMapping(" + prefix + "," + namespaceURI + ")"));
        }
        ArrayStack stack = this.namespaces.get(prefix);
        if (stack == null) {
            stack = new ArrayStack();
            this.namespaces.put(prefix, stack);
        }
        stack.push((Object)namespaceURI);
    }
    
    public void notationDecl(final String name, final String publicId, final String systemId) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("notationDecl(" + name + "," + publicId + "," + systemId + ")"));
        }
    }
    
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notation) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("unparsedEntityDecl(" + name + "," + publicId + "," + systemId + "," + notation + ")"));
        }
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
    
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }
    
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("resolveEntity('" + publicId + "', '" + systemId + "')"));
        }
        if (publicId != null) {
            this.publicId = publicId;
        }
        URL entityURL = null;
        if (publicId != null) {
            entityURL = this.entityValidator.get(publicId);
        }
        if (this.schemaLocation != null && entityURL == null && systemId != null) {
            entityURL = this.entityValidator.get(systemId);
        }
        if (entityURL == null) {
            if (systemId == null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)(" Cannot resolve entity: '" + entityURL + "'"));
                }
                return null;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)(" Trying to resolve using system ID '" + systemId + "'"));
            }
            try {
                entityURL = new URL(systemId);
            }
            catch (final MalformedURLException e) {
                throw new IllegalArgumentException("Malformed URL '" + systemId + "' : " + e.getMessage());
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)(" Resolving to alternate DTD '" + entityURL + "'"));
        }
        try {
            return this.createInputSourceFromURL(entityURL);
        }
        catch (final Exception e2) {
            throw this.createSAXException(e2);
        }
    }
    
    public void error(final SAXParseException exception) throws SAXException {
        this.log.error((Object)("Parse Error at line " + exception.getLineNumber() + " column " + exception.getColumnNumber() + ": " + exception.getMessage()), (Throwable)exception);
        if (this.errorHandler != null) {
            this.errorHandler.error(exception);
        }
    }
    
    public void fatalError(final SAXParseException exception) throws SAXException {
        this.log.error((Object)("Parse Fatal Error at line " + exception.getLineNumber() + " column " + exception.getColumnNumber() + ": " + exception.getMessage()), (Throwable)exception);
        if (this.errorHandler != null) {
            this.errorHandler.fatalError(exception);
        }
    }
    
    public void warning(final SAXParseException exception) throws SAXException {
        if (this.errorHandler != null) {
            this.log.warn((Object)("Parse Warning Error at line " + exception.getLineNumber() + " column " + exception.getColumnNumber() + ": " + exception.getMessage()), (Throwable)exception);
            this.errorHandler.warning(exception);
        }
    }
    
    public void log(final String message) {
        this.log.info((Object)message);
    }
    
    public void log(final String message, final Throwable exception) {
        this.log.error((Object)message, exception);
    }
    
    public Object parse(final File file) throws IOException, SAXException {
        this.configure();
        final InputSource input = new InputSource(new FileInputStream(file));
        input.setSystemId(file.toURL().toString());
        this.getXMLReader().parse(input);
        this.cleanup();
        return this.root;
    }
    
    public Object parse(final InputSource input) throws IOException, SAXException {
        this.configure();
        this.getXMLReader().parse(input);
        this.cleanup();
        return this.root;
    }
    
    public Object parse(final InputStream input) throws IOException, SAXException {
        this.configure();
        final InputSource is = new InputSource(input);
        this.getXMLReader().parse(is);
        this.cleanup();
        return this.root;
    }
    
    public Object parse(final Reader reader) throws IOException, SAXException {
        this.configure();
        final InputSource is = new InputSource(reader);
        this.getXMLReader().parse(is);
        this.cleanup();
        return this.root;
    }
    
    public Object parse(final String uri) throws IOException, SAXException {
        this.configure();
        final InputSource is = this.createInputSourceFromURL(uri);
        this.getXMLReader().parse(is);
        this.cleanup();
        return this.root;
    }
    
    public Object parse(final URL url) throws IOException, SAXException {
        this.configure();
        final InputSource is = this.createInputSourceFromURL(url);
        this.getXMLReader().parse(is);
        this.cleanup();
        return this.root;
    }
    
    public void register(final String publicId, final URL entityURL) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("register('" + publicId + "', '" + entityURL + "'"));
        }
        this.entityValidator.put(publicId, entityURL);
    }
    
    public void register(final String publicId, final String entityURL) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("register('" + publicId + "', '" + entityURL + "'"));
        }
        try {
            this.entityValidator.put(publicId, new URL(entityURL));
        }
        catch (final MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL '" + entityURL + "' : " + e.getMessage());
        }
    }
    
    public InputSource createInputSourceFromURL(final URL url) throws MalformedURLException, IOException {
        final URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        final InputStream stream = connection.getInputStream();
        final InputSource source = new InputSource(stream);
        source.setSystemId(url.toExternalForm());
        this.inputSources.add(source);
        return source;
    }
    
    public InputSource createInputSourceFromURL(final String url) throws MalformedURLException, IOException {
        return this.createInputSourceFromURL(new URL(url));
    }
    
    public void addRule(final String pattern, final Rule rule) {
        rule.setDigester(this);
        this.getRules().add(pattern, rule);
    }
    
    public void addRuleSet(final RuleSet ruleSet) {
        final String oldNamespaceURI = this.getRuleNamespaceURI();
        final String newNamespaceURI = ruleSet.getNamespaceURI();
        if (this.log.isDebugEnabled()) {
            if (newNamespaceURI == null) {
                this.log.debug((Object)"addRuleSet() with no namespace URI");
            }
            else {
                this.log.debug((Object)("addRuleSet() with namespace URI " + newNamespaceURI));
            }
        }
        this.setRuleNamespaceURI(newNamespaceURI);
        ruleSet.addRuleInstances(this);
        this.setRuleNamespaceURI(oldNamespaceURI);
    }
    
    public void addBeanPropertySetter(final String pattern) {
        this.addRule(pattern, new BeanPropertySetterRule());
    }
    
    public void addBeanPropertySetter(final String pattern, final String propertyName) {
        this.addRule(pattern, new BeanPropertySetterRule(propertyName));
    }
    
    public void addCallMethod(final String pattern, final String methodName) {
        this.addRule(pattern, new CallMethodRule(methodName));
    }
    
    public void addCallMethod(final String pattern, final String methodName, final int paramCount) {
        this.addRule(pattern, new CallMethodRule(methodName, paramCount));
    }
    
    public void addCallMethod(final String pattern, final String methodName, final int paramCount, final String[] paramTypes) {
        this.addRule(pattern, new CallMethodRule(methodName, paramCount, paramTypes));
    }
    
    public void addCallMethod(final String pattern, final String methodName, final int paramCount, final Class[] paramTypes) {
        this.addRule(pattern, new CallMethodRule(methodName, paramCount, paramTypes));
    }
    
    public void addCallParam(final String pattern, final int paramIndex) {
        this.addRule(pattern, new CallParamRule(paramIndex));
    }
    
    public void addCallParam(final String pattern, final int paramIndex, final String attributeName) {
        this.addRule(pattern, new CallParamRule(paramIndex, attributeName));
    }
    
    public void addCallParam(final String pattern, final int paramIndex, final boolean fromStack) {
        this.addRule(pattern, new CallParamRule(paramIndex, fromStack));
    }
    
    public void addCallParam(final String pattern, final int paramIndex, final int stackIndex) {
        this.addRule(pattern, new CallParamRule(paramIndex, stackIndex));
    }
    
    public void addCallParamPath(final String pattern, final int paramIndex) {
        this.addRule(pattern, new PathCallParamRule(paramIndex));
    }
    
    public void addObjectParam(final String pattern, final int paramIndex, final Object paramObj) {
        this.addRule(pattern, new ObjectParamRule(paramIndex, paramObj));
    }
    
    public void addFactoryCreate(final String pattern, final String className) {
        this.addFactoryCreate(pattern, className, false);
    }
    
    public void addFactoryCreate(final String pattern, final Class clazz) {
        this.addFactoryCreate(pattern, clazz, false);
    }
    
    public void addFactoryCreate(final String pattern, final String className, final String attributeName) {
        this.addFactoryCreate(pattern, className, attributeName, false);
    }
    
    public void addFactoryCreate(final String pattern, final Class clazz, final String attributeName) {
        this.addFactoryCreate(pattern, clazz, attributeName, false);
    }
    
    public void addFactoryCreate(final String pattern, final ObjectCreationFactory creationFactory) {
        this.addFactoryCreate(pattern, creationFactory, false);
    }
    
    public void addFactoryCreate(final String pattern, final String className, final boolean ignoreCreateExceptions) {
        this.addRule(pattern, new FactoryCreateRule(className, ignoreCreateExceptions));
    }
    
    public void addFactoryCreate(final String pattern, final Class clazz, final boolean ignoreCreateExceptions) {
        this.addRule(pattern, new FactoryCreateRule(clazz, ignoreCreateExceptions));
    }
    
    public void addFactoryCreate(final String pattern, final String className, final String attributeName, final boolean ignoreCreateExceptions) {
        this.addRule(pattern, new FactoryCreateRule(className, attributeName, ignoreCreateExceptions));
    }
    
    public void addFactoryCreate(final String pattern, final Class clazz, final String attributeName, final boolean ignoreCreateExceptions) {
        this.addRule(pattern, new FactoryCreateRule(clazz, attributeName, ignoreCreateExceptions));
    }
    
    public void addFactoryCreate(final String pattern, final ObjectCreationFactory creationFactory, final boolean ignoreCreateExceptions) {
        creationFactory.setDigester(this);
        this.addRule(pattern, new FactoryCreateRule(creationFactory, ignoreCreateExceptions));
    }
    
    public void addObjectCreate(final String pattern, final String className) {
        this.addRule(pattern, new ObjectCreateRule(className));
    }
    
    public void addObjectCreate(final String pattern, final Class clazz) {
        this.addRule(pattern, new ObjectCreateRule(clazz));
    }
    
    public void addObjectCreate(final String pattern, final String className, final String attributeName) {
        this.addRule(pattern, new ObjectCreateRule(className, attributeName));
    }
    
    public void addObjectCreate(final String pattern, final String attributeName, final Class clazz) {
        this.addRule(pattern, new ObjectCreateRule(attributeName, clazz));
    }
    
    public void addSetNestedProperties(final String pattern) {
        this.addRule(pattern, new SetNestedPropertiesRule());
    }
    
    public void addSetNestedProperties(final String pattern, final String elementName, final String propertyName) {
        this.addRule(pattern, new SetNestedPropertiesRule(elementName, propertyName));
    }
    
    public void addSetNestedProperties(final String pattern, final String[] elementNames, final String[] propertyNames) {
        this.addRule(pattern, new SetNestedPropertiesRule(elementNames, propertyNames));
    }
    
    public void addSetNext(final String pattern, final String methodName) {
        this.addRule(pattern, new SetNextRule(methodName));
    }
    
    public void addSetNext(final String pattern, final String methodName, final String paramType) {
        this.addRule(pattern, new SetNextRule(methodName, paramType));
    }
    
    public void addSetRoot(final String pattern, final String methodName) {
        this.addRule(pattern, new SetRootRule(methodName));
    }
    
    public void addSetRoot(final String pattern, final String methodName, final String paramType) {
        this.addRule(pattern, new SetRootRule(methodName, paramType));
    }
    
    public void addSetProperties(final String pattern) {
        this.addRule(pattern, new SetPropertiesRule());
    }
    
    public void addSetProperties(final String pattern, final String attributeName, final String propertyName) {
        this.addRule(pattern, new SetPropertiesRule(attributeName, propertyName));
    }
    
    public void addSetProperties(final String pattern, final String[] attributeNames, final String[] propertyNames) {
        this.addRule(pattern, new SetPropertiesRule(attributeNames, propertyNames));
    }
    
    public void addSetProperty(final String pattern, final String name, final String value) {
        this.addRule(pattern, new SetPropertyRule(name, value));
    }
    
    public void addSetTop(final String pattern, final String methodName) {
        this.addRule(pattern, new SetTopRule(methodName));
    }
    
    public void addSetTop(final String pattern, final String methodName, final String paramType) {
        this.addRule(pattern, new SetTopRule(methodName, paramType));
    }
    
    public void clear() {
        this.match = "";
        this.bodyTexts.clear();
        this.params.clear();
        this.publicId = null;
        this.stack.clear();
        this.stacksByName.clear();
        this.customContentHandler = null;
    }
    
    public Object peek() {
        try {
            return this.stack.peek();
        }
        catch (final EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }
    
    public Object peek(final int n) {
        try {
            return this.stack.peek(n);
        }
        catch (final EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }
    
    public Object pop() {
        try {
            Object popped = this.stack.pop();
            if (this.stackAction != null) {
                popped = this.stackAction.onPop(this, null, popped);
            }
            return popped;
        }
        catch (final EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }
    
    public void push(Object object) {
        if (this.stackAction != null) {
            object = this.stackAction.onPush(this, null, object);
        }
        if (this.stack.size() == 0) {
            this.root = object;
        }
        this.stack.push(object);
    }
    
    public void push(final String stackName, Object value) {
        if (this.stackAction != null) {
            value = this.stackAction.onPush(this, stackName, value);
        }
        ArrayStack namedStack = this.stacksByName.get(stackName);
        if (namedStack == null) {
            namedStack = new ArrayStack();
            this.stacksByName.put(stackName, namedStack);
        }
        namedStack.push(value);
    }
    
    public Object pop(final String stackName) {
        Object result = null;
        final ArrayStack namedStack = this.stacksByName.get(stackName);
        if (namedStack == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Stack '" + stackName + "' is empty"));
            }
            throw new EmptyStackException();
        }
        result = namedStack.pop();
        if (this.stackAction != null) {
            result = this.stackAction.onPop(this, stackName, result);
        }
        return result;
    }
    
    public Object peek(final String stackName) {
        return this.peek(stackName, 0);
    }
    
    public Object peek(final String stackName, final int n) {
        Object result = null;
        final ArrayStack namedStack = this.stacksByName.get(stackName);
        if (namedStack == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Stack '" + stackName + "' is empty"));
            }
            throw new EmptyStackException();
        }
        result = namedStack.peek(n);
        return result;
    }
    
    public boolean isEmpty(final String stackName) {
        boolean result = true;
        final ArrayStack namedStack = this.stacksByName.get(stackName);
        if (namedStack != null) {
            result = namedStack.isEmpty();
        }
        return result;
    }
    
    public Object getRoot() {
        return this.root;
    }
    
    public void resetRoot() {
        this.root = null;
    }
    
    protected void cleanup() {
        final Iterator sources = this.inputSources.iterator();
        while (sources.hasNext()) {
            final InputSource source = sources.next();
            try {
                source.getByteStream().close();
            }
            catch (final IOException ex) {}
        }
    }
    
    protected void configure() {
        if (this.configured) {
            return;
        }
        this.initialize();
        this.configured = true;
    }
    
    protected void initialize() {
    }
    
    Map getRegistrations() {
        return this.entityValidator;
    }
    
    List getRules(final String match) {
        return this.getRules().match(match);
    }
    
    public Object peekParams() {
        try {
            return this.params.peek();
        }
        catch (final EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }
    
    public Object peekParams(final int n) {
        try {
            return this.params.peek(n);
        }
        catch (final EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }
    
    public Object popParams() {
        try {
            if (this.log.isTraceEnabled()) {
                this.log.trace((Object)"Popping params");
            }
            return this.params.pop();
        }
        catch (final EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }
    
    public void pushParams(final Object object) {
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)"Pushing params");
        }
        this.params.push(object);
    }
    
    public SAXException createSAXException(final String message, Exception e) {
        if (e != null && e instanceof InvocationTargetException) {
            final Throwable t = ((InvocationTargetException)e).getTargetException();
            if (t != null && t instanceof Exception) {
                e = (Exception)t;
            }
        }
        if (this.locator != null) {
            final String error = "Error at line " + this.locator.getLineNumber() + " char " + this.locator.getColumnNumber() + ": " + message;
            if (e != null) {
                return new SAXParseException(error, this.locator, e);
            }
            return new SAXParseException(error, this.locator);
        }
        else {
            this.log.error((Object)"No Locator!");
            if (e != null) {
                return new SAXException(message, e);
            }
            return new SAXException(message);
        }
    }
    
    public SAXException createSAXException(Exception e) {
        if (e instanceof InvocationTargetException) {
            final Throwable t = ((InvocationTargetException)e).getTargetException();
            if (t != null && t instanceof Exception) {
                e = (Exception)t;
            }
        }
        return this.createSAXException(e.getMessage(), e);
    }
    
    public SAXException createSAXException(final String message) {
        return this.createSAXException(message, null);
    }
}
