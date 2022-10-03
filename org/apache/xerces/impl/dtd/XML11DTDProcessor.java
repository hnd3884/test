package org.apache.xerces.impl.dtd;

import org.apache.xerces.impl.XML11DTDScannerImpl;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;

public class XML11DTDProcessor extends XMLDTDLoader
{
    public XML11DTDProcessor() {
    }
    
    public XML11DTDProcessor(final SymbolTable symbolTable) {
        super(symbolTable);
    }
    
    public XML11DTDProcessor(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        super(symbolTable, xmlGrammarPool);
    }
    
    XML11DTDProcessor(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool, final XMLErrorReporter xmlErrorReporter, final XMLEntityResolver xmlEntityResolver) {
        super(symbolTable, xmlGrammarPool, xmlErrorReporter, xmlEntityResolver);
    }
    
    protected boolean isValidNmtoken(final String s) {
        return XML11Char.isXML11ValidNmtoken(s);
    }
    
    protected boolean isValidName(final String s) {
        return XML11Char.isXML11ValidName(s);
    }
    
    protected XMLDTDScannerImpl createDTDScanner(final SymbolTable symbolTable, final XMLErrorReporter xmlErrorReporter, final XMLEntityManager xmlEntityManager) {
        return new XML11DTDScannerImpl(symbolTable, xmlErrorReporter, xmlEntityManager);
    }
    
    protected short getScannerVersion() {
        return 2;
    }
}
