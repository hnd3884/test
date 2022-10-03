package org.apache.xerces.util;

public final class ShadowedSymbolTable extends SymbolTable
{
    protected SymbolTable fSymbolTable;
    
    public ShadowedSymbolTable(final SymbolTable fSymbolTable) {
        this.fSymbolTable = fSymbolTable;
    }
    
    public String addSymbol(final String s) {
        if (this.fSymbolTable.containsSymbol(s)) {
            return this.fSymbolTable.addSymbol(s);
        }
        return super.addSymbol(s);
    }
    
    public String addSymbol(final char[] array, final int n, final int n2) {
        if (this.fSymbolTable.containsSymbol(array, n, n2)) {
            return this.fSymbolTable.addSymbol(array, n, n2);
        }
        return super.addSymbol(array, n, n2);
    }
    
    public int hash(final String s) {
        return this.fSymbolTable.hash(s);
    }
    
    public int hash(final char[] array, final int n, final int n2) {
        return this.fSymbolTable.hash(array, n, n2);
    }
}
