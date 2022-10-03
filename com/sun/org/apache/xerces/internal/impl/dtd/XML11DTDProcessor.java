package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.util.SymbolTable;

public class XML11DTDProcessor extends XMLDTDLoader
{
    public XML11DTDProcessor() {
    }
    
    public XML11DTDProcessor(final SymbolTable symbolTable) {
        super(symbolTable);
    }
    
    public XML11DTDProcessor(final SymbolTable symbolTable, final XMLGrammarPool grammarPool) {
        super(symbolTable, grammarPool);
    }
    
    XML11DTDProcessor(final SymbolTable symbolTable, final XMLGrammarPool grammarPool, final XMLErrorReporter errorReporter, final XMLEntityResolver entityResolver) {
        super(symbolTable, grammarPool, errorReporter, entityResolver);
    }
    
    @Override
    protected boolean isValidNmtoken(final String nmtoken) {
        return XML11Char.isXML11ValidNmtoken(nmtoken);
    }
    
    @Override
    protected boolean isValidName(final String name) {
        return XML11Char.isXML11ValidName(name);
    }
    
    @Override
    protected XMLDTDScannerImpl createDTDScanner(final SymbolTable symbolTable, final XMLErrorReporter errorReporter, final XMLEntityManager entityManager) {
        return new XML11DTDScannerImpl(symbolTable, errorReporter, entityManager);
    }
    
    @Override
    protected short getScannerVersion() {
        return 2;
    }
}
