package com.sun.org.apache.xerces.internal.parsers;

import java.util.Collections;
import java.util.Iterator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import java.util.Map;

public class XMLGrammarPreparser
{
    private static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final Map<String, String> KNOWN_LOADERS;
    private static final String[] RECOGNIZED_PROPERTIES;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected XMLGrammarPool fGrammarPool;
    protected Locale fLocale;
    private Map<String, XMLGrammarLoader> fLoaders;
    
    public XMLGrammarPreparser() {
        this(new SymbolTable());
    }
    
    public XMLGrammarPreparser(final SymbolTable symbolTable) {
        this.fSymbolTable = symbolTable;
        this.fLoaders = new HashMap<String, XMLGrammarLoader>();
        this.fErrorReporter = new XMLErrorReporter();
        this.setLocale(Locale.getDefault());
        this.fEntityResolver = new XMLEntityManager();
    }
    
    public boolean registerPreparser(final String grammarType, final XMLGrammarLoader loader) {
        if (loader != null) {
            this.fLoaders.put(grammarType, loader);
            return true;
        }
        if (XMLGrammarPreparser.KNOWN_LOADERS.containsKey(grammarType)) {
            final String loaderName = XMLGrammarPreparser.KNOWN_LOADERS.get(grammarType);
            try {
                final XMLGrammarLoader gl = (XMLGrammarLoader)ObjectFactory.newInstance(loaderName, true);
                this.fLoaders.put(grammarType, gl);
            }
            catch (final Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    public Grammar preparseGrammar(final String type, final XMLInputSource is) throws XNIException, IOException {
        if (this.fLoaders.containsKey(type)) {
            final XMLGrammarLoader gl = this.fLoaders.get(type);
            gl.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
            gl.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fEntityResolver);
            gl.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
            if (this.fGrammarPool != null) {
                try {
                    gl.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
                }
                catch (final Exception ex) {}
            }
            return gl.loadGrammar(is);
        }
        return null;
    }
    
    public void setLocale(final Locale locale) {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }
    
    public Locale getLocale() {
        return this.fLocale;
    }
    
    public void setErrorHandler(final XMLErrorHandler errorHandler) {
        this.fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", errorHandler);
    }
    
    public XMLErrorHandler getErrorHandler() {
        return this.fErrorReporter.getErrorHandler();
    }
    
    public void setEntityResolver(final XMLEntityResolver entityResolver) {
        this.fEntityResolver = entityResolver;
    }
    
    public XMLEntityResolver getEntityResolver() {
        return this.fEntityResolver;
    }
    
    public void setGrammarPool(final XMLGrammarPool grammarPool) {
        this.fGrammarPool = grammarPool;
    }
    
    public XMLGrammarPool getGrammarPool() {
        return this.fGrammarPool;
    }
    
    public XMLGrammarLoader getLoader(final String type) {
        return this.fLoaders.get(type);
    }
    
    public void setFeature(final String featureId, final boolean value) {
        for (final Map.Entry<String, XMLGrammarLoader> entry : this.fLoaders.entrySet()) {
            try {
                final XMLGrammarLoader gl = entry.getValue();
                gl.setFeature(featureId, value);
            }
            catch (final Exception ex) {}
        }
        if (featureId.equals("http://apache.org/xml/features/continue-after-fatal-error")) {
            this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", value);
        }
    }
    
    public void setProperty(final String propId, final Object value) {
        for (final Map.Entry<String, XMLGrammarLoader> entry : this.fLoaders.entrySet()) {
            try {
                final XMLGrammarLoader gl = entry.getValue();
                gl.setProperty(propId, value);
            }
            catch (final Exception ex) {}
        }
    }
    
    public boolean getFeature(final String type, final String featureId) {
        final XMLGrammarLoader gl = this.fLoaders.get(type);
        return gl.getFeature(featureId);
    }
    
    public Object getProperty(final String type, final String propertyId) {
        final XMLGrammarLoader gl = this.fLoaders.get(type);
        return gl.getProperty(propertyId);
    }
    
    static {
        final Map<String, String> loaders = new HashMap<String, String>();
        loaders.put("http://www.w3.org/2001/XMLSchema", "com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader");
        loaders.put("http://www.w3.org/TR/REC-xml", "com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDLoader");
        KNOWN_LOADERS = Collections.unmodifiableMap((Map<? extends String, ? extends String>)loaders);
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool" };
    }
}
