package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import jdk.xml.internal.JdkXmlUtils;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;

public class NonValidatingConfiguration extends BasicParserConfiguration implements XMLPullParserConfiguration
{
    protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
    protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
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
    protected static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
    protected XMLGrammarPool fGrammarPool;
    protected DTDDVFactory fDatatypeValidatorFactory;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityManager fEntityManager;
    protected XMLDocumentScanner fScanner;
    protected XMLInputSource fInputSource;
    protected XMLDTDScanner fDTDScanner;
    protected ValidationManager fValidationManager;
    private XMLNSDocumentScannerImpl fNamespaceScanner;
    private XMLDocumentScannerImpl fNonNSScanner;
    protected boolean fConfigUpdated;
    protected XMLLocator fLocator;
    protected boolean fParseInProgress;
    
    public NonValidatingConfiguration() {
        this(null, null, null);
    }
    
    public NonValidatingConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public NonValidatingConfiguration(final SymbolTable symbolTable, final XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    }
    
    public NonValidatingConfiguration(final SymbolTable symbolTable, final XMLGrammarPool grammarPool, final XMLComponentManager parentSettings) {
        super(symbolTable, parentSettings);
        this.fConfigUpdated = false;
        this.fParseInProgress = false;
        final String[] recognizedFeatures = { "http://apache.org/xml/features/internal/parser-settings", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/continue-after-fatal-error", "jdk.xml.overrideDefaultParser" };
        this.addRecognizedFeatures(recognizedFeatures);
        this.fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
        this.fFeatures.put("jdk.xml.overrideDefaultParser", JdkXmlUtils.OVERRIDE_PARSER_DEFAULT);
        final String[] recognizedProperties = { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/namespace-binder", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
        this.addRecognizedProperties(recognizedProperties);
        this.fGrammarPool = grammarPool;
        if (this.fGrammarPool != null) {
            this.fProperties.put("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
        }
        this.fEntityManager = this.createEntityManager();
        this.fProperties.put("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager);
        this.addComponent(this.fEntityManager);
        (this.fErrorReporter = this.createErrorReporter()).setDocumentLocator(this.fEntityManager.getEntityScanner());
        this.fProperties.put("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        this.addComponent(this.fErrorReporter);
        this.fDTDScanner = this.createDTDScanner();
        if (this.fDTDScanner != null) {
            this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
            if (this.fDTDScanner instanceof XMLComponent) {
                this.addComponent((XMLComponent)this.fDTDScanner);
            }
        }
        this.fDatatypeValidatorFactory = this.createDatatypeValidatorFactory();
        if (this.fDatatypeValidatorFactory != null) {
            this.fProperties.put("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
        }
        this.fValidationManager = this.createValidationManager();
        if (this.fValidationManager != null) {
            this.fProperties.put("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
        }
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            final XMLMessageFormatter xmft = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
        }
        this.fConfigUpdated = false;
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (final XNIException ex) {}
        this.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", new XMLSecurityPropertyManager());
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        this.fConfigUpdated = true;
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
        super.setProperty(propertyId, value);
    }
    
    @Override
    public void setLocale(final Locale locale) throws XNIException {
        super.setLocale(locale);
        this.fErrorReporter.setLocale(locale);
    }
    
    @Override
    public FeatureState getFeatureState(final String featureId) throws XMLConfigurationException {
        if (featureId.equals("http://apache.org/xml/features/internal/parser-settings")) {
            return FeatureState.is(this.fConfigUpdated);
        }
        return super.getFeatureState(featureId);
    }
    
    @Override
    public void setInputSource(final XMLInputSource inputSource) throws XMLConfigurationException, IOException {
        this.fInputSource = inputSource;
    }
    
    @Override
    public boolean parse(final boolean complete) throws XNIException, IOException {
        if (this.fInputSource != null) {
            try {
                this.reset();
                this.fScanner.setInputSource(this.fInputSource);
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
            return this.fScanner.scanDocument(complete);
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
    
    @Override
    protected void reset() throws XNIException {
        if (this.fValidationManager != null) {
            this.fValidationManager.reset();
        }
        this.configurePipeline();
        super.reset();
    }
    
    protected void configurePipeline() {
        if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
            if (this.fNamespaceScanner == null) {
                this.addComponent(this.fNamespaceScanner = new XMLNSDocumentScannerImpl());
            }
            this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNamespaceScanner);
            this.fNamespaceScanner.setDTDValidator(null);
            this.fScanner = this.fNamespaceScanner;
        }
        else {
            if (this.fNonNSScanner == null) {
                this.addComponent(this.fNonNSScanner = new XMLDocumentScannerImpl());
            }
            this.fProperties.put("http://apache.org/xml/properties/internal/document-scanner", this.fNonNSScanner);
            this.fScanner = this.fNonNSScanner;
        }
        this.fScanner.setDocumentHandler(this.fDocumentHandler);
        this.fLastComponent = this.fScanner;
        if (this.fDTDScanner != null) {
            this.fDTDScanner.setDTDHandler(this.fDTDHandler);
            this.fDTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
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
    
    protected XMLEntityManager createEntityManager() {
        return new XMLEntityManager();
    }
    
    protected XMLErrorReporter createErrorReporter() {
        return new XMLErrorReporter();
    }
    
    protected XMLDocumentScanner createDocumentScanner() {
        return null;
    }
    
    protected XMLDTDScanner createDTDScanner() {
        return new XMLDTDScannerImpl();
    }
    
    protected DTDDVFactory createDatatypeValidatorFactory() {
        return DTDDVFactory.getInstance();
    }
    
    protected ValidationManager createValidationManager() {
        return new ValidationManager();
    }
}
