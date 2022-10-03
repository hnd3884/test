package org.apache.xerces.impl.xs.opti;

import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.impl.XMLEntityHandler;
import java.io.IOException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.XNIException;
import java.util.Locale;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.XMLVersionDetector;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLDTDScanner;
import org.apache.xerces.xni.parser.XMLDocumentScanner;
import org.apache.xerces.impl.XML11DTDScannerImpl;
import org.apache.xerces.impl.XML11NSDocumentScannerImpl;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLNSDocumentScannerImpl;
import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.xni.parser.XMLPullParserConfiguration;
import org.apache.xerces.parsers.BasicParserConfiguration;

public class SchemaParsingConfig extends BasicParserConfiguration implements XMLPullParserConfiguration
{
    protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "org.apache.xerces.impl.dv.dtd.XML11DTDDVFactoryImpl";
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
    protected static final String DATATYPE_XML_VERSION = "http://apache.org/xml/properties/validation/schema/datatype-xml-version";
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
    
    public SchemaParsingConfig(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this(symbolTable, xmlGrammarPool, null);
    }
    
    public SchemaParsingConfig(final SymbolTable symbolTable, final XMLGrammarPool fGrammarPool, final XMLComponentManager xmlComponentManager) {
        super(symbolTable, xmlComponentManager);
        this.fXML11DatatypeFactory = null;
        this.fXML11NSDocScanner = null;
        this.fXML11DTDScanner = null;
        this.fParseInProgress = false;
        this.fConfigUpdated = false;
        this.f11Initialized = false;
        this.addRecognizedFeatures(new String[] { "http://apache.org/xml/features/internal/parser-settings", "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://apache.org/xml/features/scanner/notify-char-refs", "http://apache.org/xml/features/generate-synthetic-annotations" });
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/allow-java-encodings", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/scanner/notify-builtin-refs", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/scanner/notify-char-refs", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/generate-synthetic-annotations", Boolean.FALSE);
        this.addRecognizedProperties(new String[] { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/namespace-binder", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/validation/schema/datatype-xml-version" });
        this.fGrammarPool = fGrammarPool;
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
            final XMLMessageFormatter xmlMessageFormatter = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmlMessageFormatter);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmlMessageFormatter);
        }
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
        }
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (final XNIException ex) {}
    }
    
    public boolean getFeature(final String s) throws XMLConfigurationException {
        if (s.equals("http://apache.org/xml/features/internal/parser-settings")) {
            return this.fConfigUpdated;
        }
        return super.getFeature(s);
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
        this.fConfigUpdated = true;
        this.fNamespaceScanner.setFeature(s, b);
        this.fDTDScanner.setFeature(s, b);
        if (this.f11Initialized) {
            try {
                this.fXML11DTDScanner.setFeature(s, b);
            }
            catch (final Exception ex) {}
            try {
                this.fXML11NSDocScanner.setFeature(s, b);
            }
            catch (final Exception ex2) {}
        }
        super.setFeature(s, b);
    }
    
    public Object getProperty(final String s) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/locale".equals(s)) {
            return this.getLocale();
        }
        return super.getProperty(s);
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        this.fConfigUpdated = true;
        if ("http://apache.org/xml/properties/locale".equals(s)) {
            this.setLocale((Locale)o);
        }
        this.fNamespaceScanner.setProperty(s, o);
        this.fDTDScanner.setProperty(s, o);
        if (this.f11Initialized) {
            try {
                this.fXML11DTDScanner.setProperty(s, o);
            }
            catch (final Exception ex) {}
            try {
                this.fXML11NSDocScanner.setProperty(s, o);
            }
            catch (final Exception ex2) {}
        }
        super.setProperty(s, o);
    }
    
    public void setLocale(final Locale locale) throws XNIException {
        super.setLocale(locale);
        this.fErrorReporter.setLocale(locale);
    }
    
    public void setInputSource(final XMLInputSource fInputSource) throws XMLConfigurationException, IOException {
        this.fInputSource = fInputSource;
    }
    
    public boolean parse(final boolean b) throws XNIException, IOException {
        if (this.fInputSource != null) {
            try {
                this.fValidationManager.reset();
                this.fVersionDetector.reset(this);
                this.reset();
                final short determineDocVersion = this.fVersionDetector.determineDocVersion(this.fInputSource);
                if (determineDocVersion == 1) {
                    this.configurePipeline();
                    this.resetXML10();
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
    
    public void reset() throws XNIException {
        super.reset();
    }
    
    protected void configurePipeline() {
        this.setProperty("http://apache.org/xml/properties/validation/schema/datatype-xml-version", "1.0");
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
        this.setProperty("http://apache.org/xml/properties/validation/schema/datatype-xml-version", "1.1");
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
        }
        super.checkFeature(s);
    }
    
    protected void checkProperty(final String s) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/properties/") && s.length() - "http://apache.org/xml/properties/".length() == "internal/dtd-scanner".length() && s.endsWith("internal/dtd-scanner")) {
            return;
        }
        if (s.startsWith("http://java.sun.com/xml/jaxp/properties/") && s.length() - "http://java.sun.com/xml/jaxp/properties/".length() == "schemaSource".length() && s.endsWith("schemaSource")) {
            return;
        }
        super.checkProperty(s);
    }
    
    private void addRecognizedParamsAndSetDefaults(final XMLComponent xmlComponent) {
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
            this.fXML11DatatypeFactory = DTDDVFactory.getInstance("org.apache.xerces.impl.dv.dtd.XML11DTDDVFactoryImpl");
            this.addRecognizedParamsAndSetDefaults(this.fXML11DTDScanner = new XML11DTDScannerImpl());
            this.addRecognizedParamsAndSetDefaults(this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl());
            this.f11Initialized = true;
        }
    }
}
