package org.apache.xerces.parsers;

import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.util.SynchronizedSymbolTable;

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
    
    public XMLGrammarCachingConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this(symbolTable, xmlGrammarPool, null);
    }
    
    public XMLGrammarCachingConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool, final XMLComponentManager xmlComponentManager) {
        super(symbolTable, xmlGrammarPool, xmlComponentManager);
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
    
    public Grammar parseGrammar(final String s, final String s2) throws XNIException, IOException {
        return this.parseGrammar(s, new XMLInputSource(null, s2, null));
    }
    
    public Grammar parseGrammar(final String s, final XMLInputSource xmlInputSource) throws XNIException, IOException {
        if (s.equals("http://www.w3.org/2001/XMLSchema")) {
            return this.parseXMLSchema(xmlInputSource);
        }
        if (s.equals("http://www.w3.org/TR/REC-xml")) {
            return this.parseDTD(xmlInputSource);
        }
        return null;
    }
    
    protected void checkFeature(final String s) throws XMLConfigurationException {
        super.checkFeature(s);
    }
    
    protected void checkProperty(final String s) throws XMLConfigurationException {
        super.checkProperty(s);
    }
    
    SchemaGrammar parseXMLSchema(final XMLInputSource xmlInputSource) throws IOException {
        final XMLEntityResolver entityResolver = this.getEntityResolver();
        if (entityResolver != null) {
            this.fSchemaLoader.setEntityResolver(entityResolver);
        }
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
        }
        this.fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        final String s = "http://apache.org/xml/properties/";
        final String string = s + "schema/external-schemaLocation";
        this.fSchemaLoader.setProperty(string, this.getProperty(string));
        final String string2 = s + "schema/external-noNamespaceSchemaLocation";
        this.fSchemaLoader.setProperty(string2, this.getProperty(string2));
        final String s2 = "http://java.sun.com/xml/jaxp/properties/schemaSource";
        this.fSchemaLoader.setProperty(s2, this.getProperty(s2));
        this.fSchemaLoader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", this.getFeature("http://apache.org/xml/features/validation/schema-full-checking"));
        final SchemaGrammar schemaGrammar = (SchemaGrammar)this.fSchemaLoader.loadGrammar(xmlInputSource);
        if (schemaGrammar != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", new Grammar[] { schemaGrammar });
        }
        return schemaGrammar;
    }
    
    DTDGrammar parseDTD(final XMLInputSource xmlInputSource) throws IOException {
        final XMLEntityResolver entityResolver = this.getEntityResolver();
        if (entityResolver != null) {
            this.fDTDLoader.setEntityResolver(entityResolver);
        }
        this.fDTDLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        final DTDGrammar dtdGrammar = (DTDGrammar)this.fDTDLoader.loadGrammar(xmlInputSource);
        if (dtdGrammar != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[] { dtdGrammar });
        }
        return dtdGrammar;
    }
    
    static {
        fStaticSymbolTable = new SynchronizedSymbolTable(2039);
        fStaticGrammarPool = new XMLGrammarPoolImpl();
    }
}
