package org.apache.xerces.parsers;

import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.SoftReferenceSymbolTable;

public class SoftReferenceSymbolTableConfiguration extends XIncludeAwareParserConfiguration
{
    public SoftReferenceSymbolTableConfiguration() {
        this(new SoftReferenceSymbolTable(), null, null);
    }
    
    public SoftReferenceSymbolTableConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public SoftReferenceSymbolTableConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this(symbolTable, xmlGrammarPool, null);
    }
    
    public SoftReferenceSymbolTableConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool, final XMLComponentManager xmlComponentManager) {
        super(symbolTable, xmlGrammarPool, xmlComponentManager);
    }
}
