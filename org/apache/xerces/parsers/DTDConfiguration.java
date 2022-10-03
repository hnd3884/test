package org.apache.xerces.parsers;

import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLDocumentScannerImpl;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import java.io.IOException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.XNIException;
import java.util.Locale;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.XMLNamespaceBinder;
import org.apache.xerces.impl.dtd.XMLDTDValidator;
import org.apache.xerces.impl.dtd.XMLDTDProcessor;
import org.apache.xerces.xni.parser.XMLDTDScanner;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLDocumentScanner;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLPullParserConfiguration;

public class DTDConfiguration extends BasicParserConfiguration implements XMLPullParserConfiguration
{
    protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
    protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
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
    protected static final String LOCALE = "http://apache.org/xml/properties/locale";
    protected static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
    protected XMLGrammarPool fGrammarPool;
    protected DTDDVFactory fDatatypeValidatorFactory;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityManager fEntityManager;
    protected XMLDocumentScanner fScanner;
    protected XMLInputSource fInputSource;
    protected XMLDTDScanner fDTDScanner;
    protected XMLDTDProcessor fDTDProcessor;
    protected XMLDTDValidator fDTDValidator;
    protected XMLNamespaceBinder fNamespaceBinder;
    protected ValidationManager fValidationManager;
    protected XMLLocator fLocator;
    protected boolean fParseInProgress;
    
    public DTDConfiguration() {
        this(null, null, null);
    }
    
    public DTDConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public DTDConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this(symbolTable, xmlGrammarPool, null);
    }
    
    public DTDConfiguration(final SymbolTable symbolTable, final XMLGrammarPool fGrammarPool, final XMLComponentManager xmlComponentManager) {
        super(symbolTable, xmlComponentManager);
        this.fParseInProgress = false;
        this.addRecognizedFeatures(new String[] { "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd" });
        this.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
        this.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
        this.addRecognizedProperties(new String[] { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/dtd-processor", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/namespace-binder", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/locale" });
        this.fGrammarPool = fGrammarPool;
        if (this.fGrammarPool != null) {
            this.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
        }
        this.setProperty("http://apache.org/xml/properties/internal/entity-manager", this.fEntityManager = this.createEntityManager());
        this.addComponent(this.fEntityManager);
        (this.fErrorReporter = this.createErrorReporter()).setDocumentLocator(this.fEntityManager.getEntityScanner());
        this.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        this.addComponent(this.fErrorReporter);
        this.setProperty("http://apache.org/xml/properties/internal/document-scanner", this.fScanner = this.createDocumentScanner());
        if (this.fScanner instanceof XMLComponent) {
            this.addComponent((XMLComponent)this.fScanner);
        }
        this.fDTDScanner = this.createDTDScanner();
        if (this.fDTDScanner != null) {
            this.setProperty("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
            if (this.fDTDScanner instanceof XMLComponent) {
                this.addComponent((XMLComponent)this.fDTDScanner);
            }
        }
        this.fDTDProcessor = this.createDTDProcessor();
        if (this.fDTDProcessor != null) {
            this.setProperty("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
            this.addComponent(this.fDTDProcessor);
        }
        this.fDTDValidator = this.createDTDValidator();
        if (this.fDTDValidator != null) {
            this.setProperty("http://apache.org/xml/properties/internal/validator/dtd", this.fDTDValidator);
            this.addComponent(this.fDTDValidator);
        }
        this.fNamespaceBinder = this.createNamespaceBinder();
        if (this.fNamespaceBinder != null) {
            this.setProperty("http://apache.org/xml/properties/internal/namespace-binder", this.fNamespaceBinder);
            this.addComponent(this.fNamespaceBinder);
        }
        this.fDatatypeValidatorFactory = this.createDatatypeValidatorFactory();
        if (this.fDatatypeValidatorFactory != null) {
            this.setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", this.fDatatypeValidatorFactory);
        }
        this.fValidationManager = this.createValidationManager();
        if (this.fValidationManager != null) {
            this.setProperty("http://apache.org/xml/properties/internal/validation-manager", this.fValidationManager);
        }
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            final XMLMessageFormatter xmlMessageFormatter = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmlMessageFormatter);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmlMessageFormatter);
        }
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (final XNIException ex) {}
    }
    
    public Object getProperty(final String s) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/locale".equals(s)) {
            return this.getLocale();
        }
        return super.getProperty(s);
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if ("http://apache.org/xml/properties/locale".equals(s)) {
            this.setLocale((Locale)o);
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
            return this.fScanner.scanDocument(b);
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
    
    protected void reset() throws XNIException {
        if (this.fValidationManager != null) {
            this.fValidationManager.reset();
        }
        this.configurePipeline();
        super.reset();
    }
    
    protected void configurePipeline() {
        if (this.fDTDValidator != null) {
            this.fScanner.setDocumentHandler(this.fDTDValidator);
            if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
                this.fDTDValidator.setDocumentHandler(this.fNamespaceBinder);
                this.fDTDValidator.setDocumentSource(this.fScanner);
                this.fNamespaceBinder.setDocumentHandler(this.fDocumentHandler);
                this.fNamespaceBinder.setDocumentSource(this.fDTDValidator);
                this.fLastComponent = this.fNamespaceBinder;
            }
            else {
                this.fDTDValidator.setDocumentHandler(this.fDocumentHandler);
                this.fDTDValidator.setDocumentSource(this.fScanner);
                this.fLastComponent = this.fDTDValidator;
            }
        }
        else if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
            this.fScanner.setDocumentHandler(this.fNamespaceBinder);
            this.fNamespaceBinder.setDocumentHandler(this.fDocumentHandler);
            this.fNamespaceBinder.setDocumentSource(this.fScanner);
            this.fLastComponent = this.fNamespaceBinder;
        }
        else {
            this.fScanner.setDocumentHandler(this.fDocumentHandler);
            this.fLastComponent = this.fScanner;
        }
        this.configureDTDPipeline();
    }
    
    protected void configureDTDPipeline() {
        if (this.fDTDScanner != null) {
            this.fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", this.fDTDScanner);
            if (this.fDTDProcessor != null) {
                this.fProperties.put("http://apache.org/xml/properties/internal/dtd-processor", this.fDTDProcessor);
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
            }
            else {
                this.fDTDScanner.setDTDHandler(this.fDTDHandler);
                if (this.fDTDHandler != null) {
                    this.fDTDHandler.setDTDSource(this.fDTDScanner);
                }
                this.fDTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
                if (this.fDTDContentModelHandler != null) {
                    this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDScanner);
                }
            }
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
        super.checkProperty(s);
    }
    
    protected XMLEntityManager createEntityManager() {
        return new XMLEntityManager();
    }
    
    protected XMLErrorReporter createErrorReporter() {
        return new XMLErrorReporter();
    }
    
    protected XMLDocumentScanner createDocumentScanner() {
        return new XMLDocumentScannerImpl();
    }
    
    protected XMLDTDScanner createDTDScanner() {
        return new XMLDTDScannerImpl();
    }
    
    protected XMLDTDProcessor createDTDProcessor() {
        return new XMLDTDProcessor();
    }
    
    protected XMLDTDValidator createDTDValidator() {
        return new XMLDTDValidator();
    }
    
    protected XMLNamespaceBinder createNamespaceBinder() {
        return new XMLNamespaceBinder();
    }
    
    protected DTDDVFactory createDatatypeValidatorFactory() {
        return DTDDVFactory.getInstance();
    }
    
    protected ValidationManager createValidationManager() {
        return new ValidationManager();
    }
}
