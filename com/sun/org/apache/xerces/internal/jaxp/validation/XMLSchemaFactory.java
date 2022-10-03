package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.impl.Constants;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import javax.xml.stream.XMLEventReader;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.util.StAXInputSource;
import javax.xml.transform.stax.StAXSource;
import com.sun.org.apache.xerces.internal.util.DOMInputSource;
import javax.xml.transform.dom.DOMSource;
import com.sun.org.apache.xerces.internal.util.SAXInputSource;
import org.xml.sax.SAXException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import javax.xml.validation.Schema;
import javax.xml.transform.Source;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import jdk.xml.internal.JdkXmlFeatures;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import javax.xml.validation.SchemaFactory;

public final class XMLSchemaFactory extends SchemaFactory
{
    private static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    private final XMLSchemaLoader fXMLSchemaLoader;
    private ErrorHandler fErrorHandler;
    private LSResourceResolver fLSResourceResolver;
    private final DOMEntityResolverWrapper fDOMEntityResolverWrapper;
    private ErrorHandlerWrapper fErrorHandlerWrapper;
    private XMLSecurityManager fSecurityManager;
    private XMLSecurityPropertyManager fSecurityPropertyMgr;
    private XMLGrammarPoolWrapper fXMLGrammarPoolWrapper;
    private final JdkXmlFeatures fXmlFeatures;
    private final boolean fOverrideDefaultParser;
    
    public XMLSchemaFactory() {
        this.fXMLSchemaLoader = new XMLSchemaLoader();
        this.fErrorHandlerWrapper = new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
        this.fDOMEntityResolverWrapper = new DOMEntityResolverWrapper();
        this.fXMLGrammarPoolWrapper = new XMLGrammarPoolWrapper();
        this.fXMLSchemaLoader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
        this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fXMLGrammarPoolWrapper);
        this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
        this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
        this.fSecurityManager = new XMLSecurityManager(true);
        this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
        this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
        this.fXMLSchemaLoader.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
        this.fXmlFeatures = new JdkXmlFeatures(this.fSecurityManager.isSecureProcessing());
        this.fOverrideDefaultParser = this.fXmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
        this.fXMLSchemaLoader.setFeature("jdk.xml.overrideDefaultParser", this.fOverrideDefaultParser);
    }
    
    @Override
    public boolean isSchemaLanguageSupported(final String schemaLanguage) {
        if (schemaLanguage == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaLanguageNull", null));
        }
        if (schemaLanguage.length() == 0) {
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaLanguageLengthZero", null));
        }
        return schemaLanguage.equals("http://www.w3.org/2001/XMLSchema");
    }
    
    @Override
    public LSResourceResolver getResourceResolver() {
        return this.fLSResourceResolver;
    }
    
    @Override
    public void setResourceResolver(final LSResourceResolver resourceResolver) {
        this.fLSResourceResolver = resourceResolver;
        this.fDOMEntityResolverWrapper.setEntityResolver(resourceResolver);
        this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return this.fErrorHandler;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.fErrorHandler = errorHandler;
        this.fErrorHandlerWrapper.setErrorHandler((errorHandler != null) ? errorHandler : DraconianErrorHandler.getInstance());
        this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
    }
    
    @Override
    public Schema newSchema(final Source[] schemas) throws SAXException {
        final XMLGrammarPoolImplExtension pool = new XMLGrammarPoolImplExtension();
        this.fXMLGrammarPoolWrapper.setGrammarPool(pool);
        final XMLInputSource[] xmlInputSources = new XMLInputSource[schemas.length];
        for (int i = 0; i < schemas.length; ++i) {
            final Source source = schemas[i];
            if (source instanceof StreamSource) {
                final StreamSource streamSource = (StreamSource)source;
                final String publicId = streamSource.getPublicId();
                final String systemId = streamSource.getSystemId();
                final InputStream inputStream = streamSource.getInputStream();
                final Reader reader = streamSource.getReader();
                (xmlInputSources[i] = new XMLInputSource(publicId, systemId, null)).setByteStream(inputStream);
                xmlInputSources[i].setCharacterStream(reader);
            }
            else if (source instanceof SAXSource) {
                final SAXSource saxSource = (SAXSource)source;
                final InputSource inputSource = saxSource.getInputSource();
                if (inputSource == null) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SAXSourceNullInputSource", null));
                }
                xmlInputSources[i] = new SAXInputSource(saxSource.getXMLReader(), inputSource);
            }
            else if (source instanceof DOMSource) {
                final DOMSource domSource = (DOMSource)source;
                final Node node = domSource.getNode();
                final String systemID = domSource.getSystemId();
                xmlInputSources[i] = new DOMInputSource(node, systemID);
            }
            else if (source instanceof StAXSource) {
                final StAXSource staxSource = (StAXSource)source;
                final XMLEventReader eventReader = staxSource.getXMLEventReader();
                if (eventReader != null) {
                    xmlInputSources[i] = new StAXInputSource(eventReader);
                }
                else {
                    xmlInputSources[i] = new StAXInputSource(staxSource.getXMLStreamReader());
                }
            }
            else {
                if (source == null) {
                    throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaSourceArrayMemberNull", null));
                }
                throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaFactorySourceUnrecognized", new Object[] { source.getClass().getName() }));
            }
        }
        try {
            this.fXMLSchemaLoader.loadGrammar(xmlInputSources);
        }
        catch (final XNIException e) {
            throw Util.toSAXException(e);
        }
        catch (final IOException e2) {
            final SAXParseException se = new SAXParseException(e2.getMessage(), null, e2);
            this.fErrorHandler.error(se);
            throw se;
        }
        this.fXMLGrammarPoolWrapper.setGrammarPool(null);
        final int grammarCount = pool.getGrammarCount();
        AbstractXMLSchema schema = null;
        if (grammarCount > 1) {
            schema = new XMLSchema(new ReadOnlyGrammarPool(pool));
        }
        else if (grammarCount == 1) {
            final Grammar[] grammars = pool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
            schema = new SimpleXMLSchema(grammars[0]);
        }
        else {
            schema = new EmptyXMLSchema();
        }
        this.propagateFeatures(schema);
        this.propagateProperties(schema);
        return schema;
    }
    
    @Override
    public Schema newSchema() throws SAXException {
        final AbstractXMLSchema schema = new WeakReferenceXMLSchema();
        this.propagateFeatures(schema);
        this.propagateProperties(schema);
        return schema;
    }
    
    @Override
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "FeatureNameNull", null));
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this.fSecurityManager != null && this.fSecurityManager.isSecureProcessing();
        }
        try {
            return this.fXMLSchemaLoader.getFeature(name);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    @Override
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "ProperyNameNull", null));
        }
        if (name.equals("http://apache.org/xml/properties/security-manager")) {
            return this.fSecurityManager;
        }
        if (name.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { name }));
        }
        final int index = this.fXmlFeatures.getIndex(name);
        if (index > -1) {
            return this.fXmlFeatures.getFeature(index);
        }
        try {
            return this.fXMLSchemaLoader.getProperty(name);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "FeatureNameNull", null));
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            if (System.getSecurityManager() != null && !value) {
                throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null));
            }
            this.fSecurityManager.setSecureProcessing(value);
            if (value && Constants.IS_JDK8_OR_ABOVE) {
                this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
                this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
            }
            this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
        }
        else {
            if (name.equals("http://www.oracle.com/feature/use-service-mechanism") && System.getSecurityManager() != null) {
                return;
            }
            if (this.fXmlFeatures != null && this.fXmlFeatures.setFeature(name, JdkXmlFeatures.State.APIPROPERTY, value)) {
                if (name.equals("jdk.xml.overrideDefaultParser") || name.equals("http://www.oracle.com/feature/use-service-mechanism")) {
                    this.fXMLSchemaLoader.setFeature(name, value);
                }
                return;
            }
            try {
                this.fXMLSchemaLoader.setFeature(name, value);
            }
            catch (final XMLConfigurationException e) {
                final String identifier = e.getIdentifier();
                if (e.getType() == Status.NOT_RECOGNIZED) {
                    throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[] { identifier }));
                }
                throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[] { identifier }));
            }
        }
    }
    
    @Override
    public void setProperty(final String name, final Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "ProperyNameNull", null));
        }
        if (name.equals("http://apache.org/xml/properties/security-manager")) {
            this.fSecurityManager = XMLSecurityManager.convert(object, this.fSecurityManager);
            this.fXMLSchemaLoader.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
            return;
        }
        if (name.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
            if (object == null) {
                this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
            }
            else {
                this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)object;
            }
            this.fXMLSchemaLoader.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
            return;
        }
        if (name.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { name }));
        }
        try {
            if ((this.fSecurityManager == null || !this.fSecurityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, object)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(name, XMLSecurityPropertyManager.State.APIPROPERTY, object))) {
                this.fXMLSchemaLoader.setProperty(name, object);
            }
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    private void propagateFeatures(final AbstractXMLSchema schema) {
        schema.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", this.fSecurityManager != null && this.fSecurityManager.isSecureProcessing());
        schema.setFeature("jdk.xml.overrideDefaultParser", this.fOverrideDefaultParser);
        final String[] features = this.fXMLSchemaLoader.getRecognizedFeatures();
        for (int i = 0; i < features.length; ++i) {
            final boolean state = this.fXMLSchemaLoader.getFeature(features[i]);
            schema.setFeature(features[i], state);
        }
    }
    
    private void propagateProperties(final AbstractXMLSchema schema) {
        final String[] properties = this.fXMLSchemaLoader.getRecognizedProperties();
        for (int i = 0; i < properties.length; ++i) {
            final Object state = this.fXMLSchemaLoader.getProperty(properties[i]);
            schema.setProperty(properties[i], state);
        }
    }
    
    static class XMLGrammarPoolImplExtension extends XMLGrammarPoolImpl
    {
        public XMLGrammarPoolImplExtension() {
        }
        
        public XMLGrammarPoolImplExtension(final int initialCapacity) {
            super(initialCapacity);
        }
        
        int getGrammarCount() {
            return this.fGrammarCount;
        }
    }
    
    static class XMLGrammarPoolWrapper implements XMLGrammarPool
    {
        private XMLGrammarPool fGrammarPool;
        
        @Override
        public Grammar[] retrieveInitialGrammarSet(final String grammarType) {
            return this.fGrammarPool.retrieveInitialGrammarSet(grammarType);
        }
        
        @Override
        public void cacheGrammars(final String grammarType, final Grammar[] grammars) {
            this.fGrammarPool.cacheGrammars(grammarType, grammars);
        }
        
        @Override
        public Grammar retrieveGrammar(final XMLGrammarDescription desc) {
            return this.fGrammarPool.retrieveGrammar(desc);
        }
        
        @Override
        public void lockPool() {
            this.fGrammarPool.lockPool();
        }
        
        @Override
        public void unlockPool() {
            this.fGrammarPool.unlockPool();
        }
        
        @Override
        public void clear() {
            this.fGrammarPool.clear();
        }
        
        void setGrammarPool(final XMLGrammarPool grammarPool) {
            this.fGrammarPool = grammarPool;
        }
        
        XMLGrammarPool getGrammarPool() {
            return this.fGrammarPool;
        }
    }
}
