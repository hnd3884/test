package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import java.io.Reader;
import java.io.StringReader;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import java.io.EOFException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;

public class XMLDTDLoader extends XMLDTDProcessor implements XMLGrammarLoader
{
    protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String BALANCE_SYNTAX_TREES = "http://apache.org/xml/features/validation/balance-syntax-trees";
    private static final String[] LOADER_RECOGNIZED_FEATURES;
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    public static final String LOCALE = "http://apache.org/xml/properties/locale";
    private static final String[] LOADER_RECOGNIZED_PROPERTIES;
    private boolean fStrictURI;
    private boolean fBalanceSyntaxTrees;
    protected XMLEntityResolver fEntityResolver;
    protected XMLDTDScannerImpl fDTDScanner;
    protected XMLEntityManager fEntityManager;
    protected Locale fLocale;
    
    public XMLDTDLoader() {
        this(new SymbolTable());
    }
    
    public XMLDTDLoader(final SymbolTable symbolTable) {
        this(symbolTable, null);
    }
    
    public XMLDTDLoader(final SymbolTable symbolTable, final XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null, new XMLEntityManager());
    }
    
    XMLDTDLoader(final SymbolTable symbolTable, final XMLGrammarPool grammarPool, XMLErrorReporter errorReporter, final XMLEntityResolver entityResolver) {
        this.fStrictURI = false;
        this.fBalanceSyntaxTrees = false;
        this.fSymbolTable = symbolTable;
        this.fGrammarPool = grammarPool;
        if (errorReporter == null) {
            errorReporter = new XMLErrorReporter();
            errorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", new DefaultErrorHandler());
        }
        this.fErrorReporter = errorReporter;
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            final XMLMessageFormatter xmft = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
        }
        this.fEntityResolver = entityResolver;
        if (this.fEntityResolver instanceof XMLEntityManager) {
            this.fEntityManager = (XMLEntityManager)this.fEntityResolver;
        }
        else {
            this.fEntityManager = new XMLEntityManager();
        }
        this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/error-reporter", errorReporter);
        (this.fDTDScanner = this.createDTDScanner(this.fSymbolTable, this.fErrorReporter, this.fEntityManager)).setDTDHandler(this);
        this.fDTDScanner.setDTDContentModelHandler(this);
        this.reset();
    }
    
    @Override
    public String[] getRecognizedFeatures() {
        return XMLDTDLoader.LOADER_RECOGNIZED_FEATURES.clone();
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        if (featureId.equals("http://xml.org/sax/features/validation")) {
            this.fValidation = state;
        }
        else if (featureId.equals("http://apache.org/xml/features/validation/warn-on-duplicate-attdef")) {
            this.fWarnDuplicateAttdef = state;
        }
        else if (featureId.equals("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef")) {
            this.fWarnOnUndeclaredElemdef = state;
        }
        else if (featureId.equals("http://apache.org/xml/features/scanner/notify-char-refs")) {
            this.fDTDScanner.setFeature(featureId, state);
        }
        else if (featureId.equals("http://apache.org/xml/features/standard-uri-conformant")) {
            this.fStrictURI = state;
        }
        else {
            if (!featureId.equals("http://apache.org/xml/features/validation/balance-syntax-trees")) {
                throw new XMLConfigurationException(Status.NOT_RECOGNIZED, featureId);
            }
            this.fBalanceSyntaxTrees = state;
        }
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return XMLDTDLoader.LOADER_RECOGNIZED_PROPERTIES.clone();
    }
    
    @Override
    public Object getProperty(final String propertyId) throws XMLConfigurationException {
        if (propertyId.equals("http://apache.org/xml/properties/internal/symbol-table")) {
            return this.fSymbolTable;
        }
        if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
            return this.fErrorReporter;
        }
        if (propertyId.equals("http://apache.org/xml/properties/internal/error-handler")) {
            return this.fErrorReporter.getErrorHandler();
        }
        if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
            return this.fEntityResolver;
        }
        if (propertyId.equals("http://apache.org/xml/properties/locale")) {
            return this.getLocale();
        }
        if (propertyId.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            return this.fGrammarPool;
        }
        if (propertyId.equals("http://apache.org/xml/properties/internal/validator/dtd")) {
            return this.fValidator;
        }
        throw new XMLConfigurationException(Status.NOT_RECOGNIZED, propertyId);
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if (propertyId.equals("http://apache.org/xml/properties/internal/symbol-table")) {
            this.fSymbolTable = (SymbolTable)value;
            this.fDTDScanner.setProperty(propertyId, value);
            this.fEntityManager.setProperty(propertyId, value);
        }
        else if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
            this.fErrorReporter = (XMLErrorReporter)value;
            if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
                final XMLMessageFormatter xmft = new XMLMessageFormatter();
                this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xmft);
                this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xmft);
            }
            this.fDTDScanner.setProperty(propertyId, value);
            this.fEntityManager.setProperty(propertyId, value);
        }
        else if (propertyId.equals("http://apache.org/xml/properties/internal/error-handler")) {
            this.fErrorReporter.setProperty(propertyId, value);
        }
        else if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
            this.fEntityResolver = (XMLEntityResolver)value;
            this.fEntityManager.setProperty(propertyId, value);
        }
        else if (propertyId.equals("http://apache.org/xml/properties/locale")) {
            this.setLocale((Locale)value);
        }
        else {
            if (!propertyId.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
                throw new XMLConfigurationException(Status.NOT_RECOGNIZED, propertyId);
            }
            this.fGrammarPool = (XMLGrammarPool)value;
        }
    }
    
    @Override
    public boolean getFeature(final String featureId) throws XMLConfigurationException {
        if (featureId.equals("http://xml.org/sax/features/validation")) {
            return this.fValidation;
        }
        if (featureId.equals("http://apache.org/xml/features/validation/warn-on-duplicate-attdef")) {
            return this.fWarnDuplicateAttdef;
        }
        if (featureId.equals("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef")) {
            return this.fWarnOnUndeclaredElemdef;
        }
        if (featureId.equals("http://apache.org/xml/features/scanner/notify-char-refs")) {
            return this.fDTDScanner.getFeature(featureId);
        }
        if (featureId.equals("http://apache.org/xml/features/standard-uri-conformant")) {
            return this.fStrictURI;
        }
        if (featureId.equals("http://apache.org/xml/features/validation/balance-syntax-trees")) {
            return this.fBalanceSyntaxTrees;
        }
        throw new XMLConfigurationException(Status.NOT_RECOGNIZED, featureId);
    }
    
    @Override
    public void setLocale(final Locale locale) {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }
    
    @Override
    public Locale getLocale() {
        return this.fLocale;
    }
    
    @Override
    public void setErrorHandler(final XMLErrorHandler errorHandler) {
        this.fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", errorHandler);
    }
    
    @Override
    public XMLErrorHandler getErrorHandler() {
        return this.fErrorReporter.getErrorHandler();
    }
    
    @Override
    public void setEntityResolver(final XMLEntityResolver entityResolver) {
        this.fEntityResolver = entityResolver;
        this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", entityResolver);
    }
    
    @Override
    public XMLEntityResolver getEntityResolver() {
        return this.fEntityResolver;
    }
    
    @Override
    public Grammar loadGrammar(final XMLInputSource source) throws IOException, XNIException {
        this.reset();
        final String eid = XMLEntityManager.expandSystemId(source.getSystemId(), source.getBaseSystemId(), this.fStrictURI);
        final XMLDTDDescription desc = new XMLDTDDescription(source.getPublicId(), source.getSystemId(), source.getBaseSystemId(), eid, null);
        if (!this.fBalanceSyntaxTrees) {
            this.fDTDGrammar = new DTDGrammar(this.fSymbolTable, desc);
        }
        else {
            this.fDTDGrammar = new BalancedDTDGrammar(this.fSymbolTable, desc);
        }
        (this.fGrammarBucket = new DTDGrammarBucket()).setStandalone(false);
        this.fGrammarBucket.setActiveGrammar(this.fDTDGrammar);
        try {
            this.fDTDScanner.setInputSource(source);
            this.fDTDScanner.scanDTDExternalSubset(true);
        }
        catch (final EOFException ex) {}
        finally {
            this.fEntityManager.closeReaders();
        }
        if (this.fDTDGrammar != null && this.fGrammarPool != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[] { this.fDTDGrammar });
        }
        return this.fDTDGrammar;
    }
    
    public void loadGrammarWithContext(final XMLDTDValidator validator, final String rootName, final String publicId, final String systemId, final String baseSystemId, final String internalSubset) throws IOException, XNIException {
        final DTDGrammarBucket grammarBucket = validator.getGrammarBucket();
        final DTDGrammar activeGrammar = grammarBucket.getActiveGrammar();
        if (activeGrammar != null && !activeGrammar.isImmutable()) {
            this.fGrammarBucket = grammarBucket;
            this.fEntityManager.setScannerVersion(this.getScannerVersion());
            this.reset();
            try {
                if (internalSubset != null) {
                    final StringBuffer buffer = new StringBuffer(internalSubset.length() + 2);
                    buffer.append(internalSubset).append("]>");
                    final XMLInputSource is = new XMLInputSource(null, baseSystemId, null, new StringReader(buffer.toString()), null);
                    this.fEntityManager.startDocumentEntity(is);
                    this.fDTDScanner.scanDTDInternalSubset(true, false, systemId != null);
                }
                if (systemId != null) {
                    final XMLDTDDescription desc = new XMLDTDDescription(publicId, systemId, baseSystemId, null, rootName);
                    final XMLInputSource source = this.fEntityManager.resolveEntity(desc);
                    this.fDTDScanner.setInputSource(source);
                    this.fDTDScanner.scanDTDExternalSubset(true);
                }
            }
            catch (final EOFException ex) {}
            finally {
                this.fEntityManager.closeReaders();
            }
        }
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.fDTDScanner.reset();
        this.fEntityManager.reset();
        this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
    }
    
    protected XMLDTDScannerImpl createDTDScanner(final SymbolTable symbolTable, final XMLErrorReporter errorReporter, final XMLEntityManager entityManager) {
        return new XMLDTDScannerImpl(symbolTable, errorReporter, entityManager);
    }
    
    protected short getScannerVersion() {
        return 1;
    }
    
    static {
        LOADER_RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", "http://apache.org/xml/features/scanner/notify-char-refs", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/validation/balance-syntax-trees" };
        LOADER_RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/locale" };
    }
}
