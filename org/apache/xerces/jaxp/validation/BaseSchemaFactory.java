package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import java.util.Locale;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.apache.xerces.util.SAXMessageFormatter;
import javax.xml.stream.XMLEventReader;
import org.xml.sax.InputSource;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.util.StAXInputSource;
import javax.xml.transform.stax.StAXSource;
import org.apache.xerces.util.DOMInputSource;
import javax.xml.transform.dom.DOMSource;
import org.apache.xerces.util.SAXInputSource;
import org.xml.sax.SAXException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import javax.xml.validation.Schema;
import javax.xml.transform.Source;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.util.DOMEntityResolverWrapper;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import javax.xml.validation.SchemaFactory;

abstract class BaseSchemaFactory extends SchemaFactory
{
    private static final String JAXP_SOURCE_FEATURE_PREFIX = "http://javax.xml.transform";
    private static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
    private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String XML_SCHEMA_VERSION = "http://apache.org/xml/properties/validation/schema/version";
    private final XMLSchemaLoader fXMLSchemaLoader;
    private ErrorHandler fErrorHandler;
    private LSResourceResolver fLSResourceResolver;
    private final DOMEntityResolverWrapper fDOMEntityResolverWrapper;
    private final ErrorHandlerWrapper fErrorHandlerWrapper;
    private SecurityManager fSecurityManager;
    private final XMLGrammarPoolWrapper fXMLGrammarPoolWrapper;
    private boolean fUseGrammarPoolOnly;
    private final String fXSDVersion;
    
    BaseSchemaFactory(final String fxsdVersion) {
        this.fXMLSchemaLoader = new XMLSchemaLoader();
        this.fErrorHandlerWrapper = new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
        this.fDOMEntityResolverWrapper = new DOMEntityResolverWrapper();
        this.fXMLGrammarPoolWrapper = new XMLGrammarPoolWrapper();
        this.fXMLSchemaLoader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
        this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fXMLGrammarPoolWrapper);
        this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/validation/schema/version", fxsdVersion);
        this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
        this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
        this.fXSDVersion = fxsdVersion;
        this.fUseGrammarPoolOnly = true;
    }
    
    public LSResourceResolver getResourceResolver() {
        return this.fLSResourceResolver;
    }
    
    public void setResourceResolver(final LSResourceResolver lsResourceResolver) {
        this.fLSResourceResolver = lsResourceResolver;
        this.fDOMEntityResolverWrapper.setEntityResolver(lsResourceResolver);
        this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
    }
    
    public ErrorHandler getErrorHandler() {
        return this.fErrorHandler;
    }
    
    public void setErrorHandler(final ErrorHandler fErrorHandler) {
        this.fErrorHandler = fErrorHandler;
        this.fErrorHandlerWrapper.setErrorHandler((fErrorHandler != null) ? fErrorHandler : DraconianErrorHandler.getInstance());
        this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
    }
    
    public Schema newSchema(final Source[] array) throws SAXException {
        final XMLGrammarPoolImplExtension grammarPool = new XMLGrammarPoolImplExtension();
        this.fXMLGrammarPoolWrapper.setGrammarPool(grammarPool);
        final XMLInputSource[] array2 = new XMLInputSource[array.length];
        for (int i = 0; i < array.length; ++i) {
            final Source source = array[i];
            if (source instanceof StreamSource) {
                final StreamSource streamSource = (StreamSource)source;
                final String publicId = streamSource.getPublicId();
                final String systemId = streamSource.getSystemId();
                final InputStream inputStream = streamSource.getInputStream();
                final Reader reader = streamSource.getReader();
                final XMLInputSource xmlInputSource = new XMLInputSource(publicId, systemId, null);
                xmlInputSource.setByteStream(inputStream);
                xmlInputSource.setCharacterStream(reader);
                array2[i] = xmlInputSource;
            }
            else if (source instanceof SAXSource) {
                final SAXSource saxSource = (SAXSource)source;
                final InputSource inputSource = saxSource.getInputSource();
                if (inputSource == null) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "SAXSourceNullInputSource", null));
                }
                array2[i] = new SAXInputSource(saxSource.getXMLReader(), inputSource);
            }
            else if (source instanceof DOMSource) {
                final DOMSource domSource = (DOMSource)source;
                array2[i] = new DOMInputSource(domSource.getNode(), domSource.getSystemId());
            }
            else if (source instanceof StAXSource) {
                final StAXSource stAXSource = (StAXSource)source;
                final XMLEventReader xmlEventReader = stAXSource.getXMLEventReader();
                if (xmlEventReader != null) {
                    array2[i] = new StAXInputSource(xmlEventReader);
                }
                else {
                    array2[i] = new StAXInputSource(stAXSource.getXMLStreamReader());
                }
            }
            else {
                if (source == null) {
                    throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "SchemaSourceArrayMemberNull", null));
                }
                throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "SchemaFactorySourceUnrecognized", new Object[] { ((StAXSource)source).getClass().getName() }));
            }
        }
        try {
            this.fXMLSchemaLoader.loadGrammar(array2);
        }
        catch (final XNIException ex) {
            throw Util.toSAXException(ex);
        }
        catch (final IOException ex2) {
            final SAXParseException ex3 = new SAXParseException(ex2.getMessage(), null, ex2);
            if (this.fErrorHandler != null) {
                this.fErrorHandler.error(ex3);
            }
            throw ex3;
        }
        this.fXMLGrammarPoolWrapper.setGrammarPool(null);
        final int grammarCount = grammarPool.getGrammarCount();
        AbstractXMLSchema abstractXMLSchema;
        if (this.fUseGrammarPoolOnly) {
            if (grammarCount > 1) {
                abstractXMLSchema = new XMLSchema(new ReadOnlyGrammarPool(grammarPool), this.fXSDVersion);
            }
            else if (grammarCount == 1) {
                abstractXMLSchema = new SimpleXMLSchema(grammarPool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema")[0], this.fXSDVersion);
            }
            else {
                abstractXMLSchema = new EmptyXMLSchema(this.fXSDVersion);
            }
        }
        else {
            abstractXMLSchema = new XMLSchema(new ReadOnlyGrammarPool(grammarPool), false, this.fXSDVersion);
        }
        this.propagateFeatures(abstractXMLSchema);
        return abstractXMLSchema;
    }
    
    public Schema newSchema() throws SAXException {
        final WeakReferenceXMLSchema weakReferenceXMLSchema = new WeakReferenceXMLSchema(this.fXSDVersion);
        this.propagateFeatures(weakReferenceXMLSchema);
        return weakReferenceXMLSchema;
    }
    
    public Schema newSchema(final XMLGrammarPool xmlGrammarPool) throws SAXException {
        final XMLSchema xmlSchema = this.fUseGrammarPoolOnly ? new XMLSchema(new ReadOnlyGrammarPool(xmlGrammarPool), this.fXSDVersion) : new XMLSchema(xmlGrammarPool, false, this.fXSDVersion);
        this.propagateFeatures(xmlSchema);
        return xmlSchema;
    }
    
    public boolean getFeature(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "FeatureNameNull", null));
        }
        if (s.startsWith("http://javax.xml.transform") && (s.equals("http://javax.xml.transform.stream.StreamSource/feature") || s.equals("http://javax.xml.transform.sax.SAXSource/feature") || s.equals("http://javax.xml.transform.dom.DOMSource/feature") || s.equals("http://javax.xml.transform.stax.StAXSource/feature"))) {
            return true;
        }
        if (s.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this.fSecurityManager != null;
        }
        if (s.equals("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only")) {
            return this.fUseGrammarPoolOnly;
        }
        try {
            return this.fXMLSchemaLoader.getFeature(s);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    public Object getProperty(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "ProperyNameNull", null));
        }
        if (s.equals("http://apache.org/xml/properties/security-manager")) {
            return this.fSecurityManager;
        }
        if (s.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.getLocale(), "property-not-supported", new Object[] { s }));
        }
        if (s.equals("http://apache.org/xml/properties/validation/schema/version")) {
            return this.fXSDVersion;
        }
        try {
            return this.fXMLSchemaLoader.getProperty(s);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    public void setFeature(final String s, final boolean fUseGrammarPoolOnly) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "FeatureNameNull", null));
        }
        if (s.startsWith("http://javax.xml.transform") && (s.equals("http://javax.xml.transform.stream.StreamSource/feature") || s.equals("http://javax.xml.transform.sax.SAXSource/feature") || s.equals("http://javax.xml.transform.dom.DOMSource/feature") || s.equals("http://javax.xml.transform.stax.StAXSource/feature"))) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.getLocale(), "feature-read-only", new Object[] { s }));
        }
        if (s.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this.fSecurityManager = (fUseGrammarPoolOnly ? new SecurityManager() : null);
            this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
            return;
        }
        if (s.equals("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only")) {
            this.fUseGrammarPoolOnly = fUseGrammarPoolOnly;
            return;
        }
        try {
            this.fXMLSchemaLoader.setFeature(s, fUseGrammarPoolOnly);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    public void setProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.getLocale(), "ProperyNameNull", null));
        }
        if (s.equals("http://apache.org/xml/properties/security-manager")) {
            this.fSecurityManager = (SecurityManager)o;
            this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
            return;
        }
        if (s.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.getLocale(), "property-not-supported", new Object[] { s }));
        }
        if (s.equals("http://apache.org/xml/properties/validation/schema/version")) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.getLocale(), "property-read-only", new Object[] { s }));
        }
        try {
            this.fXMLSchemaLoader.setProperty(s, o);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    protected Locale getLocale() {
        return this.fXMLSchemaLoader.getLocale();
    }
    
    private void propagateFeatures(final AbstractXMLSchema abstractXMLSchema) {
        abstractXMLSchema.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", this.fSecurityManager != null);
        final String[] recognizedFeatures = this.fXMLSchemaLoader.getRecognizedFeatures();
        for (int i = 0; i < recognizedFeatures.length; ++i) {
            abstractXMLSchema.setFeature(recognizedFeatures[i], this.fXMLSchemaLoader.getFeature(recognizedFeatures[i]));
        }
    }
    
    static class XMLGrammarPoolImplExtension extends XMLGrammarPoolImpl
    {
        public XMLGrammarPoolImplExtension() {
        }
        
        public XMLGrammarPoolImplExtension(final int n) {
            super(n);
        }
        
        int getGrammarCount() {
            return this.fGrammarCount;
        }
    }
    
    static class XMLGrammarPoolWrapper implements XMLGrammarPool
    {
        private XMLGrammarPool fGrammarPool;
        
        public Grammar[] retrieveInitialGrammarSet(final String s) {
            return this.fGrammarPool.retrieveInitialGrammarSet(s);
        }
        
        public void cacheGrammars(final String s, final Grammar[] array) {
            this.fGrammarPool.cacheGrammars(s, array);
        }
        
        public Grammar retrieveGrammar(final XMLGrammarDescription xmlGrammarDescription) {
            return this.fGrammarPool.retrieveGrammar(xmlGrammarDescription);
        }
        
        public void lockPool() {
            this.fGrammarPool.lockPool();
        }
        
        public void unlockPool() {
            this.fGrammarPool.unlockPool();
        }
        
        public void clear() {
            this.fGrammarPool.clear();
        }
        
        void setGrammarPool(final XMLGrammarPool fGrammarPool) {
            this.fGrammarPool = fGrammarPool;
        }
        
        XMLGrammarPool getGrammarPool() {
            return this.fGrammarPool;
        }
    }
}
