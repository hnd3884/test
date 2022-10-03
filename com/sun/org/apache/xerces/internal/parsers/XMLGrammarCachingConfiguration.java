package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDLoader;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

public class XMLGrammarCachingConfiguration extends XIncludeAwareParserConfiguration
{
    public static final int BIG_PRIME = 2039;
    protected static final SynchronizedSymbolTable fStaticSymbolTable;
    protected static final XMLGrammarPoolImpl fStaticGrammarPool;
    protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected XMLSchemaLoader fSchemaLoader;
    protected XMLDTDLoader fDTDLoader;
    
    public XMLGrammarCachingConfiguration() {
        this(XMLGrammarCachingConfiguration.fStaticSymbolTable, XMLGrammarCachingConfiguration.fStaticGrammarPool, null);
    }
    
    public XMLGrammarCachingConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, XMLGrammarCachingConfiguration.fStaticGrammarPool, null);
    }
    
    public XMLGrammarCachingConfiguration(final SymbolTable symbolTable, final XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    }
    
    public XMLGrammarCachingConfiguration(final SymbolTable symbolTable, final XMLGrammarPool grammarPool, final XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);
        (this.fSchemaLoader = new XMLSchemaLoader(this.fSymbolTable)).setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
        this.fDTDLoader = new XMLDTDLoader(this.fSymbolTable, this.fGrammarPool);
    }
    
    public void lockGrammarPool() {
        this.fGrammarPool.lockPool();
    }
    
    public void clearGrammarPool() {
        this.fGrammarPool.clear();
    }
    
    public void unlockGrammarPool() {
        this.fGrammarPool.unlockPool();
    }
    
    public Grammar parseGrammar(final String type, final String uri) throws XNIException, IOException {
        final XMLInputSource source = new XMLInputSource(null, uri, null);
        return this.parseGrammar(type, source);
    }
    
    public Grammar parseGrammar(final String type, final XMLInputSource is) throws XNIException, IOException {
        if (type.equals("http://www.w3.org/2001/XMLSchema")) {
            return this.parseXMLSchema(is);
        }
        if (type.equals("http://www.w3.org/TR/REC-xml")) {
            return this.parseDTD(is);
        }
        return null;
    }
    
    SchemaGrammar parseXMLSchema(final XMLInputSource is) throws IOException {
        final XMLEntityResolver resolver = this.getEntityResolver();
        if (resolver != null) {
            this.fSchemaLoader.setEntityResolver(resolver);
        }
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
        }
        this.fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        final String propPrefix = "http://apache.org/xml/properties/";
        String propName = propPrefix + "schema/external-schemaLocation";
        this.fSchemaLoader.setProperty(propName, this.getProperty(propName));
        propName = propPrefix + "schema/external-noNamespaceSchemaLocation";
        this.fSchemaLoader.setProperty(propName, this.getProperty(propName));
        propName = "http://java.sun.com/xml/jaxp/properties/schemaSource";
        this.fSchemaLoader.setProperty(propName, this.getProperty(propName));
        this.fSchemaLoader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", this.getFeature("http://apache.org/xml/features/validation/schema-full-checking"));
        final SchemaGrammar grammar = (SchemaGrammar)this.fSchemaLoader.loadGrammar(is);
        if (grammar != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", new Grammar[] { grammar });
        }
        return grammar;
    }
    
    DTDGrammar parseDTD(final XMLInputSource is) throws IOException {
        final XMLEntityResolver resolver = this.getEntityResolver();
        if (resolver != null) {
            this.fDTDLoader.setEntityResolver(resolver);
        }
        this.fDTDLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        final DTDGrammar grammar = (DTDGrammar)this.fDTDLoader.loadGrammar(is);
        if (grammar != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[] { grammar });
        }
        return grammar;
    }
    
    static {
        fStaticSymbolTable = new SynchronizedSymbolTable(2039);
        fStaticGrammarPool = new XMLGrammarPoolImpl();
    }
}
