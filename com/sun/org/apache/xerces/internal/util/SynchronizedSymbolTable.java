package com.sun.org.apache.xerces.internal.util;

public final class SynchronizedSymbolTable extends SymbolTable
{
    protected SymbolTable fSymbolTable;
    
    public SynchronizedSymbolTable(final SymbolTable symbolTable) {
        this.fSymbolTable = symbolTable;
    }
    
    public SynchronizedSymbolTable() {
        this.fSymbolTable = new SymbolTable();
    }
    
    public SynchronizedSymbolTable(final int size) {
        this.fSymbolTable = new SymbolTable(size);
    }
    
    @Override
    public String addSymbol(final String symbol) {
        synchronized (this.fSymbolTable) {
            return this.fSymbolTable.addSymbol(symbol);
        }
    }
    
    @Override
    public String addSymbol(final char[] buffer, final int offset, final int length) {
        synchronized (this.fSymbolTable) {
            return this.fSymbolTable.addSymbol(buffer, offset, length);
        }
    }
    
    @Override
    public boolean containsSymbol(final String symbol) {
        synchronized (this.fSymbolTable) {
            return this.fSymbolTable.containsSymbol(symbol);
        }
    }
    
    @Override
    public boolean containsSymbol(final char[] buffer, final int offset, final int length) {
        synchronized (this.fSymbolTable) {
            return this.fSymbolTable.containsSymbol(buffer, offset, length);
        }
    }
}
