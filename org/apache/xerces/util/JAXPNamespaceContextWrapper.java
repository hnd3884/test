package org.apache.xerces.util;

import java.util.Collections;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Enumeration;
import java.util.Vector;
import java.util.List;
import org.apache.xerces.xni.NamespaceContext;

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
    
    public void setNamespaceContext(final javax.xml.namespace.NamespaceContext fNamespaceContext) {
        this.fNamespaceContext = fNamespaceContext;
    }
    
    public javax.xml.namespace.NamespaceContext getNamespaceContext() {
        return this.fNamespaceContext;
    }
    
    public void setSymbolTable(final SymbolTable fSymbolTable) {
        this.fSymbolTable = fSymbolTable;
    }
    
    public SymbolTable getSymbolTable() {
        return this.fSymbolTable;
    }
    
    public void setDeclaredPrefixes(final List fPrefixes) {
        this.fPrefixes = fPrefixes;
    }
    
    public List getDeclaredPrefixes() {
        return this.fPrefixes;
    }
    
    public String getURI(final String s) {
        if (this.fNamespaceContext != null) {
            final String namespaceURI = this.fNamespaceContext.getNamespaceURI(s);
            if (namespaceURI != null && !"".equals(namespaceURI)) {
                return (this.fSymbolTable != null) ? this.fSymbolTable.addSymbol(namespaceURI) : namespaceURI.intern();
            }
        }
        return null;
    }
    
    public String getPrefix(String s) {
        if (this.fNamespaceContext != null) {
            if (s == null) {
                s = "";
            }
            String prefix = this.fNamespaceContext.getPrefix(s);
            if (prefix == null) {
                prefix = "";
            }
            return (this.fSymbolTable != null) ? this.fSymbolTable.addSymbol(prefix) : prefix.intern();
        }
        return null;
    }
    
    public Enumeration getAllPrefixes() {
        return Collections.enumeration(new TreeSet<Object>(this.fAllPrefixes));
    }
    
    public void pushContext() {
        if (this.fCurrentContext + 1 == this.fContext.length) {
            final int[] fContext = new int[this.fContext.length * 2];
            System.arraycopy(this.fContext, 0, fContext, 0, this.fContext.length);
            this.fContext = fContext;
        }
        this.fContext[++this.fCurrentContext] = this.fAllPrefixes.size();
        if (this.fPrefixes != null) {
            this.fAllPrefixes.addAll(this.fPrefixes);
        }
    }
    
    public void popContext() {
        this.fAllPrefixes.setSize(this.fContext[this.fCurrentContext--]);
    }
    
    public boolean declarePrefix(final String s, final String s2) {
        return true;
    }
    
    public boolean deletePrefix(final String s) {
        return true;
    }
    
    public int getDeclaredPrefixCount() {
        return (this.fPrefixes != null) ? this.fPrefixes.size() : 0;
    }
    
    public String getDeclaredPrefixAt(final int n) {
        return this.fPrefixes.get(n);
    }
    
    public void reset() {
        this.fCurrentContext = 0;
        this.fContext[this.fCurrentContext] = 0;
        this.fAllPrefixes.clear();
    }
}
