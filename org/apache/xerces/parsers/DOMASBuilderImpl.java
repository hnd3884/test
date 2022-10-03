package org.apache.xerces.parsers;

import java.util.Vector;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.xni.XNIException;
import org.w3c.dom.ls.LSInput;
import org.apache.xerces.dom3.as.DOMASException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.dom3.as.ASModel;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.dom.ASModelImpl;
import org.apache.xerces.impl.xs.XSGrammarBucket;
import org.apache.xerces.dom3.as.DOMASBuilder;

public class DOMASBuilderImpl extends DOMParserImpl implements DOMASBuilder
{
    protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected XSGrammarBucket fGrammarBucket;
    protected ASModelImpl fAbstractSchema;
    
    public DOMASBuilderImpl() {
        super(new XMLGrammarCachingConfiguration());
    }
    
    public DOMASBuilderImpl(final XMLGrammarCachingConfiguration xmlGrammarCachingConfiguration) {
        super(xmlGrammarCachingConfiguration);
    }
    
    public DOMASBuilderImpl(final SymbolTable symbolTable) {
        super(new XMLGrammarCachingConfiguration(symbolTable));
    }
    
    public DOMASBuilderImpl(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        super(new XMLGrammarCachingConfiguration(symbolTable, xmlGrammarPool));
    }
    
    public ASModel getAbstractSchema() {
        return this.fAbstractSchema;
    }
    
    public void setAbstractSchema(final ASModel asModel) {
        this.fAbstractSchema = (ASModelImpl)asModel;
        XMLGrammarPool xmlGrammarPool = (XMLGrammarPool)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
        if (xmlGrammarPool == null) {
            xmlGrammarPool = new XMLGrammarPoolImpl();
            this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", xmlGrammarPool);
        }
        if (this.fAbstractSchema != null) {
            this.initGrammarPool(this.fAbstractSchema, xmlGrammarPool);
        }
    }
    
    public ASModel parseASURI(final String s) throws DOMASException, Exception {
        return this.parseASInputSource(new XMLInputSource(null, s, null));
    }
    
    public ASModel parseASInputSource(final LSInput lsInput) throws DOMASException, Exception {
        final XMLInputSource dom2xmlInputSource = this.dom2xmlInputSource(lsInput);
        try {
            return this.parseASInputSource(dom2xmlInputSource);
        }
        catch (final XNIException ex) {
            throw ex.getException();
        }
    }
    
    ASModel parseASInputSource(final XMLInputSource xmlInputSource) throws Exception {
        if (this.fGrammarBucket == null) {
            this.fGrammarBucket = new XSGrammarBucket();
        }
        this.initGrammarBucket();
        final XMLGrammarCachingConfiguration xmlGrammarCachingConfiguration = (XMLGrammarCachingConfiguration)this.fConfiguration;
        xmlGrammarCachingConfiguration.lockGrammarPool();
        final SchemaGrammar xmlSchema = xmlGrammarCachingConfiguration.parseXMLSchema(xmlInputSource);
        xmlGrammarCachingConfiguration.unlockGrammarPool();
        ASModelImpl asModelImpl = null;
        if (xmlSchema != null) {
            asModelImpl = new ASModelImpl();
            this.fGrammarBucket.putGrammar(xmlSchema, true);
            this.addGrammars(asModelImpl, this.fGrammarBucket);
        }
        return asModelImpl;
    }
    
    private void initGrammarBucket() {
        this.fGrammarBucket.reset();
        if (this.fAbstractSchema != null) {
            this.initGrammarBucketRecurse(this.fAbstractSchema);
        }
    }
    
    private void initGrammarBucketRecurse(final ASModelImpl asModelImpl) {
        if (asModelImpl.getGrammar() != null) {
            this.fGrammarBucket.putGrammar(asModelImpl.getGrammar());
        }
        for (int i = 0; i < asModelImpl.getInternalASModels().size(); ++i) {
            this.initGrammarBucketRecurse((ASModelImpl)asModelImpl.getInternalASModels().elementAt(i));
        }
    }
    
    private void addGrammars(final ASModelImpl asModelImpl, final XSGrammarBucket xsGrammarBucket) {
        final SchemaGrammar[] grammars = xsGrammarBucket.getGrammars();
        for (int i = 0; i < grammars.length; ++i) {
            final ASModelImpl asModelImpl2 = new ASModelImpl();
            asModelImpl2.setGrammar(grammars[i]);
            asModelImpl.addASModel(asModelImpl2);
        }
    }
    
    private void initGrammarPool(final ASModelImpl asModelImpl, final XMLGrammarPool xmlGrammarPool) {
        final Grammar[] array2;
        final Grammar[] array = array2 = new Grammar[] { null };
        final int n = 0;
        final SchemaGrammar grammar = asModelImpl.getGrammar();
        array2[n] = grammar;
        if (grammar != null) {
            xmlGrammarPool.cacheGrammars(array[0].getGrammarDescription().getGrammarType(), array);
        }
        final Vector internalASModels = asModelImpl.getInternalASModels();
        for (int i = 0; i < internalASModels.size(); ++i) {
            this.initGrammarPool((ASModelImpl)internalASModels.elementAt(i), xmlGrammarPool);
        }
    }
}
