package org.apache.xerces.parsers;

import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public class XMLDocumentParser extends AbstractXMLDocumentParser
{
    public XMLDocumentParser() {
        super((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
    }
    
    public XMLDocumentParser(final XMLParserConfiguration xmlParserConfiguration) {
        super(xmlParserConfiguration);
    }
    
    public XMLDocumentParser(final SymbolTable symbolTable) {
        super((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
    }
    
    public XMLDocumentParser(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        super((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", xmlGrammarPool);
    }
}
