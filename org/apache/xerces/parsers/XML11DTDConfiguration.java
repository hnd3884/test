package org.apache.xerces.parsers;

import org.apache.xerces.impl.dtd.XMLDTDValidatorFilter;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.impl.XMLEntityHandler;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import java.io.IOException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.impl.dtd.XMLNSDTDValidator;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.xni.parser.XMLComponent;
import java.util.HashMap;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLDocumentScanner;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.impl.dtd.XML11DTDProcessor;
import org.apache.xerces.impl.XML11DTDScannerImpl;
import org.apache.xerces.impl.dtd.XML11DTDValidator;
import org.apache.xerces.impl.dtd.XML11NSDTDValidator;
import org.apache.xerces.impl.XML11DocumentScannerImpl;
import org.apache.xerces.impl.XML11NSDocumentScannerImpl;
import org.apache.xerces.impl.dtd.XMLDTDProcessor;
import org.apache.xerces.xni.parser.XMLDTDScanner;
import org.apache.xerces.impl.dtd.XMLDTDValidator;
import org.apache.xerces.impl.XMLDocumentScannerImpl;
import org.apache.xerces.impl.XMLNSDocumentScannerImpl;
import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import java.util.ArrayList;
import java.util.Locale;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.impl.XMLVersionDetector;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLPullParserConfiguration;
import org.apache.xerces.util.ParserConfigurationSettings;

public class XML11DTDConfiguration extends ParserConfigurationSettings implements XMLPullParserConfiguration, XML11Configurable
{
    protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "org.apache.xerces.impl.dv.dtd.XML11DTDDVFactoryImpl";
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
    
    public XML11DTDConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this(symbolTable, xmlGrammarPool, null);
    }
    
    public XML11DTDConfiguration(SymbolTable fSymbolTable, final XMLGrammarPool fGrammarPool, final XMLComponentManager xmlComponentManager) {
        super(xmlComponentManager);
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
        this.fRecognizedFeatures = new ArrayList();
        this.fRecognizedProperties = new ArrayList();
        this.fFeatures = new HashMap();
        this.fProperties = new HashMap();
        this.addRecognizedFeatures(new String[] { "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/internal/parser-settings" });
        this.fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
        this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        this.addRecognizedProperties(new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/dtd-processor", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage" });
        if (fSymbolTable == null) {
            fSymbolTable = new SymbolTable();
        }
        this.fSymbolTable = fSymbolTable;
        this.fProperties.put("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
        this.fGrammarPool = fGrammarPool;
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
            final XMLMessageFormatter xmlMessageFormatter = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmlMessageFormatter);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmlMessageFormatter);
        }
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (final XNIException ex) {}
        this.fConfigUpdated = false;
    }
    
    public void setInputSource(final XMLInputSource fInputSource) throws XMLConfigurationException, IOException {
        this.fInputSource = fInputSource;
    }
    
    public void setLocale(final Locale locale) throws XNIException {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }
    
    public void setDocumentHandler(final XMLDocumentHandler fDocumentHandler) {
        this.fDocumentHandler = fDocumentHandler;
        if (this.fLastComponent != null) {
            this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fLastComponent);
            }
        }
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    public void setDTDHandler(final XMLDTDHandler fdtdHandler) {
        this.fDTDHandler = fdtdHandler;
    }
    
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    public void setDTDContentModelHandler(final XMLDTDContentModelHandler fdtdContentModelHandler) {
        this.fDTDContentModelHandler = fdtdContentModelHandler;
    }
    
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }
    
    public void setEntityResolver(final XMLEntityResolver xmlEntityResolver) {
        this.fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", xmlEntityResolver);
    }
    
    public XMLEntityResolver getEntityResolver() {
        return this.fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
    }
    
    public void setErrorHandler(final XMLErrorHandler xmlErrorHandler) {
        this.fProperties.put("http://apache.org/xml/properties/internal/error-handler", xmlErrorHandler);
    }
    
    public XMLErrorHandler getErrorHandler() {
        return this.fProperties.get("http://apache.org/xml/properties/internal/error-handler");
    }
    
    public void cleanup() {
        this.fEntityManager.closeReaders();
    }
    
    public void parse(final XMLInputSource inputSource) throws XNIException, IOException {
        if (this.fParseInProgress) {
            throw new XNIException("FWK005 parse may not be called while parsing.");
        }
        this.fParseInProgress = true;
        try {
            this.setInputSource(inputSource);
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
    
    public boolean parse(final boolean b) throws XNIException, IOException {
        if (this.fInputSource != null) {
            try {
                this.fValidationManager.reset();
                this.fVersionDetector.reset(this);
                this.resetCommon();
                final short determineDocVersion = this.fVersionDetector.determineDocVersion(this.fInputSource);
                if (determineDocVersion == 1) {
                    this.configurePipeline();
                    this.reset();
                }
                else {
                    if (determineDocVersion != 2) {
                        return false;
                    }
                    this.initXML11Components();
                    this.configureXML11Pipeline();
                    this.resetXML11();
                }
                this.fConfigUpdated = false;
                this.fVersionDetector.startDocumentParsing((XMLEntityHandler)this.fCurrentScanner, determineDocVersion);
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
            return this.fCurrentScanner.scanDocument(b);
        }
        catch (final XNIException ex5) {
            throw ex5;
        }
        catch (final IOException ex6) {
            throw ex6;
        }
        catch (final RuntimeException ex7) {
            throw ex7;
        }
        catch (final Exception ex8) {
            throw new XNIException(ex8);
        }
    }
    
    public boolean getFeature(final String s) throws XMLConfigurationException {
        if (s.equals("http://apache.org/xml/features/internal/parser-settings")) {
            return this.fConfigUpdated;
        }
        return super.getFeature(s);
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
        this.fConfigUpdated = true;
        for (int size = this.fComponents.size(), i = 0; i < size; ++i) {
            ((XMLComponent)this.fComponents.get(i)).setFeature(s, b);
        }
        for (int size2 = this.fCommonComponents.size(), j = 0; j < size2; ++j) {
            ((XMLComponent)this.fCommonComponents.get(j)).setFeature(s, b);
        }
        for (int size3 = this.fXML11Components.size(), k = 0; k < size3; ++k) {
            final XMLComponent xmlComponent = this.fXML11Components.get(k);
            try {
                xmlComponent.setFeature(s, b);
            }
            catch (final Exception ex) {}
        }
        super.setFeature(s, b);
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        this.fConfigUpdated = true;
        for (int size = this.fComponents.size(), i = 0; i < size; ++i) {
            ((XMLComponent)this.fComponents.get(i)).setProperty(s, o);
        }
        for (int size2 = this.fCommonComponents.size(), j = 0; j < size2; ++j) {
            ((XMLComponent)this.fCommonComponents.get(j)).setProperty(s, o);
        }
        for (int size3 = this.fXML11Components.size(), k = 0; k < size3; ++k) {
            final XMLComponent xmlComponent = this.fXML11Components.get(k);
            try {
                xmlComponent.setProperty(s, o);
            }
            catch (final Exception ex) {}
        }
        super.setProperty(s, o);
    }
    
    public Locale getLocale() {
        return this.fLocale;
    }
    
    protected void reset() throws XNIException {
        for (int size = this.fComponents.size(), i = 0; i < size; ++i) {
            ((XMLComponent)this.fComponents.get(i)).reset(this);
        }
    }
    
    protected void resetCommon() throws XNIException {
        for (int size = this.fCommonComponents.size(), i = 0; i < size; ++i) {
            ((XMLComponent)this.fCommonComponents.get(i)).reset(this);
        }
    }
    
    protected void resetXML11() throws XNIException {
        for (int size = this.fXML11Components.size(), i = 0; i < size; ++i) {
            ((XMLComponent)this.fXML11Components.get(i)).reset(this);
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
    
    protected void checkFeature(final String s) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/features/")) {
            final int n = s.length() - "http://apache.org/xml/features/".length();
            if (n == "validation/dynamic".length() && s.endsWith("validation/dynamic")) {
                return;
            }
            if (n == "validation/default-attribute-values".length() && s.endsWith("validation/default-attribute-values")) {
                throw new XMLConfigurationException((short)1, s);
            }
            if (n == "validation/validate-content-models".length() && s.endsWith("validation/validate-content-models")) {
                throw new XMLConfigurationException((short)1, s);
            }
            if (n == "nonvalidating/load-dtd-grammar".length() && s.endsWith("nonvalidating/load-dtd-grammar")) {
                return;
            }
            if (n == "nonvalidating/load-external-dtd".length() && s.endsWith("nonvalidating/load-external-dtd")) {
                return;
            }
            if (n == "validation/validate-datatypes".length() && s.endsWith("validation/validate-datatypes")) {
                throw new XMLConfigurationException((short)1, s);
            }
            if (n == "internal/parser-settings".length() && s.endsWith("internal/parser-settings")) {
                throw new XMLConfigurationException((short)1, s);
            }
        }
        super.checkFeature(s);
    }
    
    protected void checkProperty(final String s) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/properties/") && s.length() - "http://apache.org/xml/properties/".length() == "internal/dtd-scanner".length() && s.endsWith("internal/dtd-scanner")) {
            return;
        }
        if (s.startsWith("http://xml.org/sax/properties/") && s.length() - "http://xml.org/sax/properties/".length() == "xml-string".length() && s.endsWith("xml-string")) {
            throw new XMLConfigurationException((short)1, s);
        }
        super.checkProperty(s);
    }
    
    protected void addComponent(final XMLComponent xmlComponent) {
        if (this.fComponents.contains(xmlComponent)) {
            return;
        }
        this.fComponents.add(xmlComponent);
        this.addRecognizedParamsAndSetDefaults(xmlComponent);
    }
    
    protected void addCommonComponent(final XMLComponent xmlComponent) {
        if (this.fCommonComponents.contains(xmlComponent)) {
            return;
        }
        this.fCommonComponents.add(xmlComponent);
        this.addRecognizedParamsAndSetDefaults(xmlComponent);
    }
    
    protected void addXML11Component(final XMLComponent xmlComponent) {
        if (this.fXML11Components.contains(xmlComponent)) {
            return;
        }
        this.fXML11Components.add(xmlComponent);
        this.addRecognizedParamsAndSetDefaults(xmlComponent);
    }
    
    protected void addRecognizedParamsAndSetDefaults(final XMLComponent xmlComponent) {
        final String[] recognizedFeatures = xmlComponent.getRecognizedFeatures();
        this.addRecognizedFeatures(recognizedFeatures);
        final String[] recognizedProperties = xmlComponent.getRecognizedProperties();
        this.addRecognizedProperties(recognizedProperties);
        if (recognizedFeatures != null) {
            for (int i = 0; i < recognizedFeatures.length; ++i) {
                final String s = recognizedFeatures[i];
                final Boolean featureDefault = xmlComponent.getFeatureDefault(s);
                if (featureDefault != null && !this.fFeatures.containsKey(s)) {
                    this.fFeatures.put(s, featureDefault);
                    this.fConfigUpdated = true;
                }
            }
        }
        if (recognizedProperties != null) {
            for (int j = 0; j < recognizedProperties.length; ++j) {
                final String s2 = recognizedProperties[j];
                final Object propertyDefault = xmlComponent.getPropertyDefault(s2);
                if (propertyDefault != null && !this.fProperties.containsKey(s2)) {
                    this.fProperties.put(s2, propertyDefault);
                    this.fConfigUpdated = true;
                }
            }
        }
    }
    
    private void initXML11Components() {
        if (!this.f11Initialized) {
            this.fXML11DatatypeFactory = DTDDVFactory.getInstance("org.apache.xerces.impl.dv.dtd.XML11DTDDVFactoryImpl");
            this.addXML11Component(this.fXML11DTDScanner = new XML11DTDScannerImpl());
            this.addXML11Component(this.fXML11DTDProcessor = new XML11DTDProcessor());
            this.addXML11Component(this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl());
            this.addXML11Component(this.fXML11NSDTDValidator = new XML11NSDTDValidator());
            this.f11Initialized = true;
        }
    }
}
