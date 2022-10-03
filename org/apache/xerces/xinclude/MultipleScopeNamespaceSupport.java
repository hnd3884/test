package org.apache.xerces.xinclude;

import org.apache.xerces.util.XMLSymbols;
import java.util.Enumeration;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.util.NamespaceSupport;

public class MultipleScopeNamespaceSupport extends NamespaceSupport
{
    protected int[] fScope;
    protected int fCurrentScope;
    
    public MultipleScopeNamespaceSupport() {
        this.fScope = new int[8];
        this.fCurrentScope = 0;
        this.fScope[0] = 0;
    }
    
    public MultipleScopeNamespaceSupport(final NamespaceContext namespaceContext) {
        super(namespaceContext);
        this.fScope = new int[8];
        this.fCurrentScope = 0;
        this.fScope[0] = 0;
    }
    
    public Enumeration getAllPrefixes() {
        int n = 0;
        if (this.fPrefixes.length < this.fNamespace.length / 2) {
            this.fPrefixes = new String[this.fNamespaceSize];
        }
        int n2 = 1;
        for (int i = this.fContext[this.fScope[this.fCurrentScope]]; i <= this.fNamespaceSize - 2; i += 2) {
            final String s = this.fNamespace[i];
            for (int j = 0; j < n; ++j) {
                if (this.fPrefixes[j] == s) {
                    n2 = 0;
                    break;
                }
            }
            if (n2 != 0) {
                this.fPrefixes[n++] = s;
            }
            n2 = 1;
        }
        return new Prefixes(this.fPrefixes, n);
    }
    
    public int getScopeForContext(final int i) {
        int fCurrentScope;
        for (fCurrentScope = this.fCurrentScope; i < this.fScope[fCurrentScope]; --fCurrentScope) {}
        return fCurrentScope;
    }
    
    public String getPrefix(final String s) {
        return this.getPrefix(s, this.fNamespaceSize, this.fContext[this.fScope[this.fCurrentScope]]);
    }
    
    public String getURI(final String s) {
        return this.getURI(s, this.fNamespaceSize, this.fContext[this.fScope[this.fCurrentScope]]);
    }
    
    public String getPrefix(final String s, final int n) {
        return this.getPrefix(s, this.fContext[n + 1], this.fContext[this.fScope[this.getScopeForContext(n)]]);
    }
    
    public String getURI(final String s, final int n) {
        return this.getURI(s, this.fContext[n + 1], this.fContext[this.fScope[this.getScopeForContext(n)]]);
    }
    
    public String getPrefix(final String s, final int n, final int n2) {
        if (s == NamespaceContext.XML_URI) {
            return XMLSymbols.PREFIX_XML;
        }
        if (s == NamespaceContext.XMLNS_URI) {
            return XMLSymbols.PREFIX_XMLNS;
        }
        for (int i = n; i > n2; i -= 2) {
            if (this.fNamespace[i - 1] == s && this.getURI(this.fNamespace[i - 2]) == s) {
                return this.fNamespace[i - 2];
            }
        }
        return null;
    }
    
    public String getURI(final String s, final int n, final int n2) {
        if (s == XMLSymbols.PREFIX_XML) {
            return NamespaceContext.XML_URI;
        }
        if (s == XMLSymbols.PREFIX_XMLNS) {
            return NamespaceContext.XMLNS_URI;
        }
        for (int i = n; i > n2; i -= 2) {
            if (this.fNamespace[i - 2] == s) {
                return this.fNamespace[i - 1];
            }
        }
        return null;
    }
    
    public void reset() {
        this.fCurrentContext = this.fScope[this.fCurrentScope];
        this.fNamespaceSize = this.fContext[this.fCurrentContext];
    }
    
    public void pushScope() {
        if (this.fCurrentScope + 1 == this.fScope.length) {
            final int[] fScope = new int[this.fScope.length * 2];
            System.arraycopy(this.fScope, 0, fScope, 0, this.fScope.length);
            this.fScope = fScope;
        }
        this.pushContext();
        this.fScope[++this.fCurrentScope] = this.fCurrentContext;
    }
    
    public void popScope() {
        this.fCurrentContext = this.fScope[this.fCurrentScope--];
        this.popContext();
    }
}
