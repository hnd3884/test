package org.apache.xerces.util;

public final class SynchronizedSymbolTable extends SymbolTable
{
    protected SymbolTable fSymbolTable;
    
    public SynchronizedSymbolTable(final SymbolTable fSymbolTable) {
        this.fSymbolTable = fSymbolTable;
    }
    
    public SynchronizedSymbolTable() {
        this.fSymbolTable = new SymbolTable();
    }
    
    public SynchronizedSymbolTable(final int n) {
        this.fSymbolTable = new SymbolTable(n);
    }
    
    public String addSymbol(final String s) {
        synchronized (this.fSymbolTable) {
            return this.fSymbolTable.addSymbol(s);
        }
    }
    
    public String addSymbol(final char[] array, final int n, final int n2) {
        synchronized (this.fSymbolTable) {
            return this.fSymbolTable.addSymbol(array, n, n2);
        }
    }
    
    public boolean containsSymbol(final String s) {
        synchronized (this.fSymbolTable) {
            return this.fSymbolTable.containsSymbol(s);
        }
    }
    
    public boolean containsSymbol(final char[] array, final int n, final int n2) {
        synchronized (this.fSymbolTable) {
            return this.fSymbolTable.containsSymbol(array, n, n2);
        }
    }
}
