package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.impl.XMLEntityHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLNSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDProcessor;
import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11NSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.XML11DocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11NSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDProcessor;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import java.util.ArrayList;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.impl.XMLVersionDetector;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;

public class XML11DTDConfiguration extends ParserConfigurationSettings implements XMLPullParserConfiguration, XML11Configurable
{
    protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
    protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
    protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String DTD_PROCESSOR = "http://apache.org/xml/properties/internal/dtd-processor";
    protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
    protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
    protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    protected static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
    protected SymbolTable fSymbolTable;
    protected XMLInputSource fInputSource;
    protected ValidationManager fValidationManager;
    protected XMLVersionDetector fVersionDetector;
    protected XMLLocator fLocator;
    protected Locale fLocale;
    protected ArrayList fComponents;
    protected ArrayList fXML11Components;
    protected ArrayList fCommonComponents;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDContentModelHandler fDTDContentModelHandler;
    protected XMLDocumentSource fLastComponent;
    protected boolean fParseInProgress;
    protected boolean fConfigUpdated;
    protected DTDDVFactory fDatatypeValidatorFactory;
    protected XMLNSDocumentScannerImpl fNamespaceScanner;
    protected XMLDocumentScannerImpl fNonNSScanner;
    protected XMLDTDValidator fDTDValidator;
    protected XMLDTDValidator fNonNSDTDValidator;
    protected XMLDTDScanner fDTDScanner;
    protected XMLDTDProcessor fDTDProcessor;
    protected DTDDVFactory fXML11DatatypeFactory;
    protected XML11NSDocumentScannerImpl fXML11NSDocScanner;
    protected XML11DocumentScannerImpl fXML11DocScanner;
    protected XML11NSDTDValidator fXML11NSDTDValidator;
    protected XML11DTDValidator fXML11DTDValidator;
    protected XML11DTDScannerImpl fXML11DTDScanner;
    protected XML11DTDProcessor fXML11DTDProcessor;
    protected XMLGrammarPool fGrammarPool;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityManager fEntityManager;
    protected XMLDocumentScanner fCurrentScanner;
    protected DTDDVFactory fCurrentDVFactory;
    protected XMLDTDScanner fCurrentDTDScanner;
    private boolean f11Initialized;
    
    public XML11DTDConfiguration() {
        this(null, null, null);
    }
    
    public XML11DTDConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public XML11DTDConfiguration(final SymbolTable symbolTable, final XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    }
    
    public XML11DTDConfiguration(SymbolTable symbolTable, final XMLGrammarPool grammarPool, final XMLComponentManager parentSettings) {
        super(parentSettings);
        this.fXML11Components = null;
        this.fCommonComponents = null;
        this.fParseInProgress = false;
        this.fConfigUpdated = false;
        this.fXML11DatatypeFactory = null;
        this.fXML11NSDocScanner = null;
        this.fXML11DocScanner = null;
        this.fXML11NSDTDValidator = null;
        this.fXML11DTDValidator = null;
        this.fXML11DTDScanner = null;
        this.fXML11DTDProcessor = null;
        this.f11Initialized = false;
        this.fComponents = new ArrayList();
        this.fXML11Components = new ArrayList();
        this.fCommonComponents = new ArrayList();
        this.fFeatures = new HashMap<String, Boolean>();
        this.fProperties = new HashMap<String, Object>();
        final String[] recognizedFeatures = { "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/internal/parser-settings" };
        this.addRecognizedFeatures(recognizedFeatures);
        this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
        this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        final String[] recognizedProperties = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/dtd-processor", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage" };
        this.addRecognizedProperties(recognizedProperties);
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }
        this.fSymbolTable = symbolTable;
        this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
        this.fGrammarPool = grammarPool;
        if (this.fGrammarPool != null) {
            this.fProperties.put("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
        }
        this.fEntityManager = new XMLEntityManager();
        this.fProperties.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
        this.addCommonComponent(this.fEntityManager);
        (this.fErrorReporter = new XMLErrorReporter()).setDocumentLocator(this.fEntityManager.getEntityScanner());
        this.fProperties.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        this.addCommonComponent(this.fErrorReporter);
        this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
        this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
        this.addComponent(this.fNamespaceScanner);
        this.fDTDScanner = new XMLDTDScannerImpl();
        this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
        this.addComponent((XMLComponent)this.fDTDScanner);
        this.fDTDProcessor = new XMLDTDProcessor();
        this.fProperties.put("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
        this.addComponent(this.fDTDProcessor);
        this.fDTDValidator = new XMLNSDTDValidator();
        this.fProperties.put("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
        this.addComponent(this.fDTDValidator);
        this.fDatatypeValidatorFactory = DTDDVFactory.getInstance();
        this.fProperties.put("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
        this.fValidationManager = new ValidationManager();
        this.fProperties.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
        this.fVersionDetector = new XMLVersionDetector();
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            final XMLMessageFormatter xmft = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
        }
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (final XNIException ex) {}
        this.fConfigUpdated = false;
    }
    
    @Override
    public void setInputSource(final XMLInputSource inputSource) throws XMLConfigurationException, IOException {
        this.fInputSource = inputSource;
    }
    
    @Override
    public void setLocale(final Locale locale) throws XNIException {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }
    
    @Override
    public void setDocumentHandler(final XMLDocumentHandler documentHandler) {
        this.fDocumentHandler = documentHandler;
        if (this.fLastComponent != null) {
            this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fLastComponent);
            }
        }
    }
    
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    @Override
    public void setDTDHandler(final XMLDTDHandler dtdHandler) {
        this.fDTDHandler = dtdHandler;
    }
    
    @Override
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    @Override
    public void setDTDContentModelHandler(final XMLDTDContentModelHandler handler) {
        this.fDTDContentModelHandler = handler;
    }
    
    @Override
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }
    
    @Override
    public void setEntityResolver(final XMLEntityResolver resolver) {
        this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", resolver);
    }
    
    @Override
    public XMLEntityResolver getEntityResolver() {
        return this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
    }
    
    @Override
    public void setErrorHandler(final XMLErrorHandler errorHandler) {
        this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", errorHandler);
    }
    
    @Override
    public XMLErrorHandler getErrorHandler() {
        return this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
    }
    
    @Override
    public void cleanup() {
        this.fEntityManager.closeReaders();
    }
    
    @Override
    public void parse(final XMLInputSource source) throws XNIException, IOException {
        if (this.fParseInProgress) {
            throw new XNIException("FWK005 parse may not be called while parsing.");
        }
        this.fParseInProgress = true;
        try {
            this.setInputSource(source);
            this.parse(true);
        }
        catch (final XNIException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new XNIException(ex4);
        }
        finally {
            this.fParseInProgress = false;
            this.cleanup();
        }
    }
    
    @Override
    public boolean parse(final boolean complete) throws XNIException, IOException {
        if (this.fInputSource != null) {
            try {
                this.fValidationManager.reset();
                this.fVersionDetector.reset(this);
                this.resetCommon();
                final short version = this.fVersionDetector.determineDocVersion(this.fInputSource);
                if (version == 2) {
                    this.initXML11Components();
                    this.configureXML11Pipeline();
                    this.resetXML11();
                }
                else {
                    this.configurePipeline();
                    this.reset();
                }
                this.fConfigUpdated = false;
                this.fVersionDetector.startDocumentParsing((XMLEntityHandler)this.fCurrentScanner, version);
                this.fInputSource = null;
            }
            catch (final XNIException ex) {
                throw ex;
            }
            catch (final IOException ex2) {
                throw ex2;
            }
            catch (final RuntimeException ex3) {
                throw ex3;
            }
            catch (final Exception ex4) {
                throw new XNIException(ex4);
            }
        }
        try {
            return this.fCurrentScanner.scanDocument(complete);
        }
        catch (final XNIException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new XNIException(ex4);
        }
    }
    
    @Override
    public FeatureState getFeatureState(final String featureId) throws XMLConfigurationException {
        if (featureId.equals("http://apache.org/xml/features/internal/parser-settings")) {
            return FeatureState.is(this.fConfigUpdated);
        }
        return super.getFeatureState(featureId);
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        this.fConfigUpdated = true;
        for (int count = this.fComponents.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fComponents.get(i);
            c.setFeature(featureId, state);
        }
        for (int count = this.fCommonComponents.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fCommonComponents.get(i);
            c.setFeature(featureId, state);
        }
        for (int count = this.fXML11Components.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fXML11Components.get(i);
            try {
                c.setFeature(featureId, state);
            }
            catch (final Exception ex) {}
        }
        super.setFeature(featureId, state);
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        this.fConfigUpdated = true;
        for (int count = this.fComponents.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fComponents.get(i);
            c.setProperty(propertyId, value);
        }
        for (int count = this.fCommonComponents.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fCommonComponents.get(i);
            c.setProperty(propertyId, value);
        }
        for (int count = this.fXML11Components.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fXML11Components.get(i);
            try {
                c.setProperty(propertyId, value);
            }
            catch (final Exception ex) {}
        }
        super.setProperty(propertyId, value);
    }
    
    @Override
    public Locale getLocale() {
        return this.fLocale;
    }
    
    protected void reset() throws XNIException {
        for (int count = this.fComponents.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fComponents.get(i);
            c.reset(this);
        }
    }
    
    protected void resetCommon() throws XNIException {
        for (int count = this.fCommonComponents.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fCommonComponents.get(i);
            c.reset(this);
        }
    }
    
    protected void resetXML11() throws XNIException {
        for (int count = this.fXML11Components.size(), i = 0; i < count; ++i) {
            final XMLComponent c = this.fXML11Components.get(i);
            c.reset(this);
        }
    }
    
    protected void configureXML11Pipeline() {
        if (this.fCurrentDVFactory != this.fXML11DatatypeFactory) {
            this.setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory = this.fXML11DatatypeFactory);
        }
        if (this.fCurrentDTDScanner != this.fXML11DTDScanner) {
            this.setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner = this.fXML11DTDScanner);
            this.setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fXML11DTDProcessor);
        }
        this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
        this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
        this.fXML11DTDProcessor.setDTDHandler(this.fDTDHandler);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.setDTDSource(this.fXML11DTDProcessor);
        }
        this.fXML11DTDScanner.setDTDContentModelHandler(this.fXML11DTDProcessor);
        this.fXML11DTDProcessor.setDTDContentModelSource(this.fXML11DTDScanner);
        this.fXML11DTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.setDTDContentModelSource(this.fXML11DTDProcessor);
        }
        if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
            if (this.fCurrentScanner != this.fXML11NSDocScanner) {
                this.fCurrentScanner = this.fXML11NSDocScanner;
                this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11NSDocScanner);
                this.setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fXML11NSDTDValidator);
            }
            this.fXML11NSDocScanner.setDTDValidator(this.fXML11NSDTDValidator);
            this.fXML11NSDocScanner.setDocumentHandler(this.fXML11NSDTDValidator);
            this.fXML11NSDTDValidator.setDocumentSource(this.fXML11NSDocScanner);
            this.fXML11NSDTDValidator.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fXML11NSDTDValidator);
            }
            this.fLastComponent = this.fXML11NSDTDValidator;
        }
        else {
            if (this.fXML11DocScanner == null) {
                this.addXML11Component(this.fXML11DocScanner = new XML11DocumentScannerImpl());
                this.addXML11Component(this.fXML11DTDValidator = new XML11DTDValidator());
            }
            if (this.fCurrentScanner != this.fXML11DocScanner) {
                this.fCurrentScanner = this.fXML11DocScanner;
                this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fXML11DocScanner);
                this.setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fXML11DTDValidator);
            }
            this.fXML11DocScanner.setDocumentHandler(this.fXML11DTDValidator);
            this.fXML11DTDValidator.setDocumentSource(this.fXML11DocScanner);
            this.fXML11DTDValidator.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fXML11DTDValidator);
            }
            this.fLastComponent = this.fXML11DTDValidator;
        }
    }
    
    protected void configurePipeline() {
        if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory) {
            this.setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory = this.fDatatypeValidatorFactory);
        }
        if (this.fCurrentDTDScanner != this.fDTDScanner) {
            this.setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner = this.fDTDScanner);
            this.setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
        }
        this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
        this.fDTDProcessor.setDTDSource(this.fDTDScanner);
        this.fDTDProcessor.setDTDHandler(this.fDTDHandler);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.setDTDSource(this.fDTDProcessor);
        }
        this.fDTDScanner.setDTDContentModelHandler(this.fDTDProcessor);
        this.fDTDProcessor.setDTDContentModelSource(this.fDTDScanner);
        this.fDTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDProcessor);
        }
        if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
            if (this.fCurrentScanner != this.fNamespaceScanner) {
                this.fCurrentScanner = this.fNamespaceScanner;
                this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
                this.setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
            }
            this.fNamespaceScanner.setDTDValidator(this.fDTDValidator);
            this.fNamespaceScanner.setDocumentHandler(this.fDTDValidator);
            this.fDTDValidator.setDocumentSource(this.fNamespaceScanner);
            this.fDTDValidator.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fDTDValidator);
            }
            this.fLastComponent = this.fDTDValidator;
        }
        else {
            if (this.fNonNSScanner == null) {
                this.fNonNSScanner = new XMLDocumentScannerImpl();
                this.fNonNSDTDValidator = new XMLDTDValidator();
                this.addComponent(this.fNonNSScanner);
                this.addComponent(this.fNonNSDTDValidator);
            }
            if (this.fCurrentScanner != this.fNonNSScanner) {
                this.fCurrentScanner = this.fNonNSScanner;
                this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fNonNSScanner);
                this.setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fNonNSDTDValidator);
            }
            this.fNonNSScanner.setDocumentHandler(this.fNonNSDTDValidator);
            this.fNonNSDTDValidator.setDocumentSource(this.fNonNSScanner);
            this.fNonNSDTDValidator.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fNonNSDTDValidator);
            }
            this.fLastComponent = this.fNonNSDTDValidator;
        }
    }
    
    @Override
    protected FeatureState checkFeature(final String featureId) throws XMLConfigurationException {
        if (featureId.startsWith("http://apache.org/xml/features/")) {
            final int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
            if (suffixLength == "validation/dynamic".length() && featureId.endsWith("validation/dynamic")) {
                return FeatureState.RECOGNIZED;
            }
            if (suffixLength == "validation/default-attribute-values".length() && featureId.endsWith("validation/default-attribute-values")) {
                return FeatureState.NOT_SUPPORTED;
            }
            if (suffixLength == "validation/validate-content-models".length() && featureId.endsWith("validation/validate-content-models")) {
                return FeatureState.NOT_SUPPORTED;
            }
            if (suffixLength == "nonvalidating/load-dtd-grammar".length() && featureId.endsWith("nonvalidating/load-dtd-grammar")) {
                return FeatureState.RECOGNIZED;
            }
            if (suffixLength == "nonvalidating/load-external-dtd".length() && featureId.endsWith("nonvalidating/load-external-dtd")) {
                return FeatureState.RECOGNIZED;
            }
            if (suffixLength == "validation/validate-datatypes".length() && featureId.endsWith("validation/validate-datatypes")) {
                return FeatureState.NOT_SUPPORTED;
            }
            if (suffixLength == "internal/parser-settings".length() && featureId.endsWith("internal/parser-settings")) {
                return FeatureState.NOT_SUPPORTED;
            }
        }
        return super.checkFeature(featureId);
    }
    
    @Override
    protected PropertyState checkProperty(final String propertyId) throws XMLConfigurationException {
        if (propertyId.startsWith("http://apache.org/xml/properties/")) {
            final int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
            if (suffixLength == "internal/dtd-scanner".length() && propertyId.endsWith("internal/dtd-scanner")) {
                return PropertyState.RECOGNIZED;
            }
        }
        if (propertyId.startsWith("http://xml.org/sax/properties/")) {
            final int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
            if (suffixLength == "xml-string".length() && propertyId.endsWith("xml-string")) {
                return PropertyState.NOT_SUPPORTED;
            }
        }
        return super.checkProperty(propertyId);
    }
    
    protected void addComponent(final XMLComponent component) {
        if (this.fComponents.contains(component)) {
            return;
        }
        this.fComponents.add(component);
        this.addRecognizedParamsAndSetDefaults(component);
    }
    
    protected void addCommonComponent(final XMLComponent component) {
        if (this.fCommonComponents.contains(component)) {
            return;
        }
        this.fCommonComponents.add(component);
        this.addRecognizedParamsAndSetDefaults(component);
    }
    
    protected void addXML11Component(final XMLComponent component) {
        if (this.fXML11Components.contains(component)) {
            return;
        }
        this.fXML11Components.add(component);
        this.addRecognizedParamsAndSetDefaults(component);
    }
    
    protected void addRecognizedParamsAndSetDefaults(final XMLComponent component) {
        final String[] recognizedFeatures = component.getRecognizedFeatures();
        this.addRecognizedFeatures(recognizedFeatures);
        final String[] recognizedProperties = component.getRecognizedProperties();
        this.addRecognizedProperties(recognizedProperties);
        if (recognizedFeatures != null) {
            for (int i = 0; i < recognizedFeatures.length; ++i) {
                final String featureId = recognizedFeatures[i];
                final Boolean state = component.getFeatureDefault(featureId);
                if (state != null && !this.fFeatures.containsKey(featureId)) {
                    this.fFeatures.put(featureId, state);
                    this.fConfigUpdated = true;
                }
            }
        }
        if (recognizedProperties != null) {
            for (int i = 0; i < recognizedProperties.length; ++i) {
                final String propertyId = recognizedProperties[i];
                final Object value = component.getPropertyDefault(propertyId);
                if (value != null && !this.fProperties.containsKey(propertyId)) {
                    this.fProperties.put(propertyId, value);
                    this.fConfigUpdated = true;
                }
            }
        }
    }
    
    private void initXML11Components() {
        if (!this.f11Initialized) {
            this.fXML11DatatypeFactory = DTDDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl");
            this.addXML11Component(this.fXML11DTDScanner = new XML11DTDScannerImpl());
            this.addXML11Component(this.fXML11DTDProcessor = new XML11DTDProcessor());
            this.addXML11Component(this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl());
            this.addXML11Component(this.fXML11NSDTDValidator = new XML11NSDTDValidator());
            this.f11Initialized = true;
        }
    }
}
