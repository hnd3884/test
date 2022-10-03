package com.sun.org.apache.xerces.internal.util;

import java.util.Collections;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Enumeration;
import java.util.Vector;
import java.util.List;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;

public final class JAXPNamespaceContextWrapper implements NamespaceContext
{
    private javax.xml.namespace.NamespaceContext fNamespaceContext;
    private SymbolTable fSymbolTable;
    private List fPrefixes;
    private final Vector fAllPrefixes;
    private int[] fContext;
    private int fCurrentContext;
    
    public JAXPNamespaceContextWrapper(final SymbolTable symbolTable) {
        this.fAllPrefixes = new Vector();
        this.fContext = new int[8];
        this.setSymbolTable(symbolTable);
    }
    
    public void setNamespaceContext(final javax.xml.namespace.NamespaceContext context) {
        this.fNamespaceContext = context;
    }
    
    public javax.xml.namespace.NamespaceContext getNamespaceContext() {
        return this.fNamespaceContext;
    }
    
    public void setSymbolTable(final SymbolTable symbolTable) {
        this.fSymbolTable = symbolTable;
    }
    
    public SymbolTable getSymbolTable() {
        return this.fSymbolTable;
    }
    
    public void setDeclaredPrefixes(final List prefixes) {
        this.fPrefixes = prefixes;
    }
    
    public List getDeclaredPrefixes() {
        return this.fPrefixes;
    }
    
    @Override
    public String getURI(final String prefix) {
        if (this.fNamespaceContext != null) {
            final String uri = this.fNamespaceContext.getNamespaceURI(prefix);
            if (uri != null && !"".equals(uri)) {
                return (this.fSymbolTable != null) ? this.fSymbolTable.addSymbol(uri) : uri.intern();
            }
        }
        return null;
    }
    
    @Override
    public String getPrefix(String uri) {
        if (this.fNamespaceContext != null) {
            if (uri == null) {
                uri = "";
            }
            String prefix = this.fNamespaceContext.getPrefix(uri);
            if (prefix == null) {
                prefix = "";
            }
            return (this.fSymbolTable != null) ? this.fSymbolTable.addSymbol(prefix) : prefix.intern();
        }
        return null;
    }
    
    @Override
    public Enumeration getAllPrefixes() {
        return Collections.enumeration(new TreeSet<Object>(this.fAllPrefixes));
    }
    
    @Override
    public void pushContext() {
        if (this.fCurrentContext + 1 == this.fContext.length) {
            final int[] contextarray = new int[this.fContext.length * 2];
            System.arraycopy(this.fContext, 0, contextarray, 0, this.fContext.length);
            this.fContext = contextarray;
        }
        this.fContext[++this.fCurrentContext] = this.fAllPrefixes.size();
        if (this.fPrefixes != null) {
            this.fAllPrefixes.addAll(this.fPrefixes);
        }
    }
    
    @Override
    public void popContext() {
        this.fAllPrefixes.setSize(this.fContext[this.fCurrentContext--]);
    }
    
    @Override
    public boolean declarePrefix(final String prefix, final String uri) {
        return true;
    }
    
    @Override
    public int getDeclaredPrefixCount() {
        return (this.fPrefixes != null) ? this.fPrefixes.size() : 0;
    }
    
    @Override
    public String getDeclaredPrefixAt(final int index) {
        return this.fPrefixes.get(index);
    }
    
    @Override
    public void reset() {
        this.fCurrentContext = 0;
        this.fContext[this.fCurrentContext] = 0;
        this.fAllPrefixes.clear();
    }
}
