package org.apache.xerces.parsers;

import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public class SAXParser extends AbstractSAXParser
{
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    private static final String[] RECOGNIZED_FEATURES;
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final String[] RECOGNIZED_PROPERTIES;
    
    public SAXParser(final XMLParserConfiguration xmlParserConfiguration) {
        super(xmlParserConfiguration);
    }
    
    public SAXParser() {
        this(null, null);
    }
    
    public SAXParser(final SymbolTable symbolTable) {
        this(symbolTable, null);
    }
    
    public SAXParser(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        super((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fConfiguration.addRecognizedFeatures(SAXParser.RECOGNIZED_FEATURES);
        this.fConfiguration.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
        this.fConfiguration.addRecognizedProperties(SAXParser.RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
        }
        if (xmlGrammarPool != null) {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", xmlGrammarPool);
        }
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://apache.org/xml/features/scanner/notify-builtin-refs" };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool" };
    }
}
