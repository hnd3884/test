package org.apache.xerces.parsers;

import java.util.Enumeration;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.grammars.XMLGrammarLoader;
import org.apache.xerces.impl.XMLEntityManager;
import java.util.Locale;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.SymbolTable;
import java.util.Hashtable;

public class XMLGrammarPreparser
{
    private static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final Hashtable KNOWN_LOADERS;
    private static final String[] RECOGNIZED_PROPERTIES;
    protected final SymbolTable fSymbolTable;
    protected final XMLErrorReporter fErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected XMLGrammarPool fGrammarPool;
    protected Locale fLocale;
    private final Hashtable fLoaders;
    private int fModCount;
    
    public XMLGrammarPreparser() {
        this(new SymbolTable());
    }
    
    public XMLGrammarPreparser(final SymbolTable fSymbolTable) {
        this.fModCount = 1;
        this.fSymbolTable = fSymbolTable;
        this.fLoaders = new Hashtable();
        this.fErrorReporter = new XMLErrorReporter();
        this.setLocale(Locale.getDefault());
        this.fEntityResolver = new XMLEntityManager();
    }
    
    public boolean registerPreparser(final String s, final XMLGrammarLoader xmlGrammarLoader) {
        if (xmlGrammarLoader != null) {
            this.fLoaders.put(s, new XMLGrammarLoaderContainer(xmlGrammarLoader));
            return true;
        }
        if (XMLGrammarPreparser.KNOWN_LOADERS.containsKey(s)) {
            final String s2 = XMLGrammarPreparser.KNOWN_LOADERS.get(s);
            try {
                this.fLoaders.put(s, new XMLGrammarLoaderContainer((XMLGrammarLoader)ObjectFactory.newInstance(s2, ObjectFactory.findClassLoader(), true)));
            }
            catch (final Exception ex) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    public Grammar preparseGrammar(final String s, final XMLInputSource xmlInputSource) throws XNIException, IOException {
        if (this.fLoaders.containsKey(s)) {
            final XMLGrammarLoaderContainer xmlGrammarLoaderContainer = this.fLoaders.get(s);
            final XMLGrammarLoader loader = xmlGrammarLoaderContainer.loader;
            if (xmlGrammarLoaderContainer.modCount != this.fModCount) {
                loader.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
                loader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fEntityResolver);
                loader.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
                if (this.fGrammarPool != null) {
                    try {
                        loader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
                    }
                    catch (final Exception ex) {}
                }
                xmlGrammarLoaderContainer.modCount = this.fModCount;
            }
            return loader.loadGrammar(xmlInputSource);
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
    
    public void setErrorHandler(final XMLErrorHandler xmlErrorHandler) {
        this.fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", xmlErrorHandler);
    }
    
    public XMLErrorHandler getErrorHandler() {
        return this.fErrorReporter.getErrorHandler();
    }
    
    public void setEntityResolver(final XMLEntityResolver fEntityResolver) {
        if (this.fEntityResolver != fEntityResolver) {
            if (++this.fModCount < 0) {
                this.clearModCounts();
            }
            this.fEntityResolver = fEntityResolver;
        }
    }
    
    public XMLEntityResolver getEntityResolver() {
        return this.fEntityResolver;
    }
    
    public void setGrammarPool(final XMLGrammarPool fGrammarPool) {
        if (this.fGrammarPool != fGrammarPool) {
            if (++this.fModCount < 0) {
                this.clearModCounts();
            }
            this.fGrammarPool = fGrammarPool;
        }
    }
    
    public XMLGrammarPool getGrammarPool() {
        return this.fGrammarPool;
    }
    
    public XMLGrammarLoader getLoader(final String s) {
        final XMLGrammarLoaderContainer xmlGrammarLoaderContainer = this.fLoaders.get(s);
        return (xmlGrammarLoaderContainer != null) ? xmlGrammarLoaderContainer.loader : null;
    }
    
    public void setFeature(final String s, final boolean b) {
        final Enumeration elements = this.fLoaders.elements();
        while (elements.hasMoreElements()) {
            final XMLGrammarLoader loader = ((XMLGrammarLoaderContainer)elements.nextElement()).loader;
            try {
                loader.setFeature(s, b);
            }
            catch (final Exception ex) {}
        }
        if (s.equals("http://apache.org/xml/features/continue-after-fatal-error")) {
            this.fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", b);
        }
    }
    
    public void setProperty(final String s, final Object o) {
        final Enumeration elements = this.fLoaders.elements();
        while (elements.hasMoreElements()) {
            final XMLGrammarLoader loader = ((XMLGrammarLoaderContainer)elements.nextElement()).loader;
            try {
                loader.setProperty(s, o);
            }
            catch (final Exception ex) {}
        }
    }
    
    public boolean getFeature(final String s, final String s2) {
        return this.fLoaders.get(s).loader.getFeature(s2);
    }
    
    public Object getProperty(final String s, final String s2) {
        return this.fLoaders.get(s).loader.getProperty(s2);
    }
    
    private void clearModCounts() {
        final Enumeration elements = this.fLoaders.elements();
        while (elements.hasMoreElements()) {
            ((XMLGrammarLoaderContainer)elements.nextElement()).modCount = 0;
        }
        this.fModCount = 1;
    }
    
    static {
        (KNOWN_LOADERS = new Hashtable()).put("http://www.w3.org/2001/XMLSchema", "org.apache.xerces.impl.xs.XMLSchemaLoader");
        XMLGrammarPreparser.KNOWN_LOADERS.put("http://www.w3.org/TR/REC-xml", "org.apache.xerces.impl.dtd.XMLDTDLoader");
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool" };
    }
    
    static class XMLGrammarLoaderContainer
    {
        public final XMLGrammarLoader loader;
        public int modCount;
        
        public XMLGrammarLoaderContainer(final XMLGrammarLoader loader) {
            this.modCount = 0;
            this.loader = loader;
        }
    }
}
