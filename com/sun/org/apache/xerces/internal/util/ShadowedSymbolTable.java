package com.sun.org.apache.xerces.internal.util;

public final class ShadowedSymbolTable extends SymbolTable
{
    protected SymbolTable fSymbolTable;
    
    public ShadowedSymbolTable(final SymbolTable symbolTable) {
        this.fSymbolTable = symbolTable;
    }
    
    @Override
    public String addSymbol(final String symbol) {
        if (this.fSymbolTable.containsSymbol(symbol)) {
            return this.fSymbolTable.addSymbol(symbol);
        }
        return super.addSymbol(symbol);
    }
    
    @Override
    public String addSymbol(final char[] buffer, final int offset, final int length) {
        if (this.fSymbolTable.containsSymbol(buffer, offset, length)) {
            return this.fSymbolTable.addSymbol(buffer, offset, length);
        }
        return super.addSymbol(buffer, offset, length);
    }
    
    @Override
    public int hash(final String symbol) {
        return this.fSymbolTable.hash(symbol);
    }
    
    @Override
    public int hash(final char[] buffer, final int offset, final int length) {
        return this.fSymbolTable.hash(buffer, offset, length);
    }
}
