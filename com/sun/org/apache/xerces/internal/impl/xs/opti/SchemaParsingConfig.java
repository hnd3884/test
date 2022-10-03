package com.sun.org.apache.xerces.internal.impl.xs.opti;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.impl.XMLEntityHandler;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import jdk.xml.internal.JdkXmlUtils;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLVersionDetector;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11NSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
import com.sun.org.apache.xerces.internal.parsers.BasicParserConfiguration;

public class SchemaParsingConfig extends BasicParserConfiguration implements XMLPullParserConfiguration
{
    protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
    protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
    protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
    protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
    protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
    protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    protected static final String LOCALE = "http://apache.org/xml/properties/locale";
    private static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
    protected final DTDDVFactory fDatatypeValidatorFactory;
    protected final XMLNSDocumentScannerImpl fNamespaceScanner;
    protected final XMLDTDScannerImpl fDTDScanner;
    protected DTDDVFactory fXML11DatatypeFactory;
    protected XML11NSDocumentScannerImpl fXML11NSDocScanner;
    protected XML11DTDScannerImpl fXML11DTDScanner;
    protected DTDDVFactory fCurrentDVFactory;
    protected XMLDocumentScanner fCurrentScanner;
    protected XMLDTDScanner fCurrentDTDScanner;
    protected XMLGrammarPool fGrammarPool;
    protected final XMLVersionDetector fVersionDetector;
    protected final XMLErrorReporter fErrorReporter;
    protected final XMLEntityManager fEntityManager;
    protected XMLInputSource fInputSource;
    protected final ValidationManager fValidationManager;
    protected XMLLocator fLocator;
    protected boolean fParseInProgress;
    protected boolean fConfigUpdated;
    private boolean f11Initialized;
    
    public SchemaParsingConfig() {
        this(null, null, null);
    }
    
    public SchemaParsingConfig(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public SchemaParsingConfig(final SymbolTable symbolTable, final XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    }
    
    public SchemaParsingConfig(final SymbolTable symbolTable, final XMLGrammarPool grammarPool, final XMLComponentManager parentSettings) {
        super(symbolTable, parentSettings);
        this.fXML11DatatypeFactory = null;
        this.fXML11NSDocScanner = null;
        this.fXML11DTDScanner = null;
        this.fParseInProgress = false;
        this.fConfigUpdated = false;
        this.f11Initialized = false;
        final String[] recognizedFeatures = { "http://apache.org/xml/features/internal/parser-settings", "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://apache.org/xml/features/scanner/notify-char-refs", "http://apache.org/xml/features/generate-synthetic-annotations", "jdk.xml.overrideDefaultParser" };
        this.addRecognizedFeatures(recognizedFeatures);
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/allow-java-encodings", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/scanner/notify-builtin-refs", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/scanner/notify-char-refs", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/generate-synthetic-annotations", Boolean.FALSE);
        this.fFeatures.put("jdk.xml.overrideDefaultParser", JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
        final String[] recognizedProperties = { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/namespace-binder", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/properties/locale" };
        this.addRecognizedProperties(recognizedProperties);
        this.fGrammarPool = grammarPool;
        if (this.fGrammarPool != null) {
            this.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
        }
        this.fEntityManager = new XMLEntityManager();
        this.fProperties.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
        this.addComponent(this.fEntityManager);
        (this.fErrorReporter = new XMLErrorReporter()).setDocumentLocator(this.fEntityManager.getEntityScanner());
        this.fProperties.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        this.addComponent(this.fErrorReporter);
        this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
        this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
        this.addRecognizedParamsAndSetDefaults(this.fNamespaceScanner);
        this.fDTDScanner = new XMLDTDScannerImpl();
        this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
        this.addRecognizedParamsAndSetDefaults(this.fDTDScanner);
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
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            final XSMessageFormatter xmft2 = new XSMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xmft2);
        }
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (final XNIException ex) {}
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
        this.fNamespaceScanner.setFeature(featureId, state);
        this.fDTDScanner.setFeature(featureId, state);
        if (this.f11Initialized) {
            try {
                this.fXML11DTDScanner.setFeature(featureId, state);
            }
            catch (final Exception ex) {}
            try {
                this.fXML11NSDocScanner.setFeature(featureId, state);
            }
            catch (final Exception ex2) {}
        }
        super.setFeature(featureId, state);
    }
    
    @Override
    public PropertyState getPropertyState(final String propertyId) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
            return PropertyState.is(this.getLocale());
        }
        return super.getPropertyState(propertyId);
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        this.fConfigUpdated = true;
        if ("http://apache.org/xml/properties/locale".equals(propertyId)) {
            this.setLocale((Locale)value);
        }
        this.fNamespaceScanner.setProperty(propertyId, value);
        this.fDTDScanner.setProperty(propertyId, value);
        if (this.f11Initialized) {
            try {
                this.fXML11DTDScanner.setProperty(propertyId, value);
            }
            catch (final Exception ex) {}
            try {
                this.fXML11NSDocScanner.setProperty(propertyId, value);
            }
            catch (final Exception ex2) {}
        }
        super.setProperty(propertyId, value);
    }
    
    @Override
    public void setLocale(final Locale locale) throws XNIException {
        super.setLocale(locale);
        this.fErrorReporter.setLocale(locale);
    }
    
    @Override
    public void setInputSource(final XMLInputSource inputSource) throws XMLConfigurationException, IOException {
        this.fInputSource = inputSource;
    }
    
    @Override
    public boolean parse(final boolean complete) throws XNIException, IOException {
        if (this.fInputSource != null) {
            try {
                this.fValidationManager.reset();
                this.fVersionDetector.reset(this);
                this.reset();
                final short version = this.fVersionDetector.determineDocVersion(this.fInputSource);
                if (version == 1) {
                    this.configurePipeline();
                    this.resetXML10();
                }
                else {
                    if (version != 2) {
                        return false;
                    }
                    this.initXML11Components();
                    this.configureXML11Pipeline();
                    this.resetXML11();
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
    
    public void reset() throws XNIException {
        super.reset();
    }
    
    protected void configurePipeline() {
        if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory) {
            this.setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory = this.fDatatypeValidatorFactory);
        }
        if (this.fCurrentScanner != this.fNamespaceScanner) {
            this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fCurrentScanner = this.fNamespaceScanner);
        }
        this.fNamespaceScanner.setDocumentHandler(this.fDocumentHandler);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.setDocumentSource(this.fNamespaceScanner);
        }
        this.fLastComponent = this.fNamespaceScanner;
        if (this.fCurrentDTDScanner != this.fDTDScanner) {
            this.setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner = this.fDTDScanner);
        }
        this.fDTDScanner.setDTDHandler(this.fDTDHandler);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.setDTDSource(this.fDTDScanner);
        }
        this.fDTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDScanner);
        }
    }
    
    protected void configureXML11Pipeline() {
        if (this.fCurrentDVFactory != this.fXML11DatatypeFactory) {
            this.setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fCurrentDVFactory = this.fXML11DatatypeFactory);
        }
        if (this.fCurrentScanner != this.fXML11NSDocScanner) {
            this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fCurrentScanner = this.fXML11NSDocScanner);
        }
        this.fXML11NSDocScanner.setDocumentHandler(this.fDocumentHandler);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.setDocumentSource(this.fXML11NSDocScanner);
        }
        this.fLastComponent = this.fXML11NSDocScanner;
        if (this.fCurrentDTDScanner != this.fXML11DTDScanner) {
            this.setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fCurrentDTDScanner = this.fXML11DTDScanner);
        }
        this.fXML11DTDScanner.setDTDHandler(this.fDTDHandler);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.setDTDSource(this.fXML11DTDScanner);
        }
        this.fXML11DTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.setDTDContentModelSource(this.fXML11DTDScanner);
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
        if (propertyId.startsWith("http://java.sun.com/xml/jaxp/properties/")) {
            final int suffixLength = propertyId.length() - "http://java.sun.com/xml/jaxp/properties/".length();
            if (suffixLength == "schemaSource".length() && propertyId.endsWith("schemaSource")) {
                return PropertyState.RECOGNIZED;
            }
        }
        return super.checkProperty(propertyId);
    }
    
    private void addRecognizedParamsAndSetDefaults(final XMLComponent component) {
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
    
    protected final void resetXML10() throws XNIException {
        this.fNamespaceScanner.reset(this);
        this.fDTDScanner.reset(this);
    }
    
    protected final void resetXML11() throws XNIException {
        this.fXML11NSDocScanner.reset(this);
        this.fXML11DTDScanner.reset(this);
    }
    
    public void resetNodePool() {
    }
    
    private void initXML11Components() {
        if (!this.f11Initialized) {
            this.fXML11DatatypeFactory = DTDDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl");
            this.addRecognizedParamsAndSetDefaults(this.fXML11DTDScanner = new XML11DTDScannerImpl());
            this.addRecognizedParamsAndSetDefaults(this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl());
            this.f11Initialized = true;
        }
    }
}
