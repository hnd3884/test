package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import java.util.Enumeration;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;

public class MultipleScopeNamespaceSupport extends NamespaceSupport
{
    protected int[] fScope;
    protected int fCurrentScope;
    
    public MultipleScopeNamespaceSupport() {
        this.fScope = new int[8];
        this.fCurrentScope = 0;
        this.fScope[0] = 0;
    }
    
    public MultipleScopeNamespaceSupport(final NamespaceContext context) {
        super(context);
        this.fScope = new int[8];
        this.fCurrentScope = 0;
        this.fScope[0] = 0;
    }
    
    @Override
    public Enumeration getAllPrefixes() {
        int count = 0;
        if (this.fPrefixes.length < this.fNamespace.length / 2) {
            final String[] prefixes = new String[this.fNamespaceSize];
            this.fPrefixes = prefixes;
        }
        String prefix = null;
        boolean unique = true;
        for (int i = this.fContext[this.fScope[this.fCurrentScope]]; i <= this.fNamespaceSize - 2; i += 2) {
            prefix = this.fNamespace[i];
            for (int k = 0; k < count; ++k) {
                if (this.fPrefixes[k] == prefix) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                this.fPrefixes[count++] = prefix;
            }
            unique = true;
        }
        return new Prefixes(this.fPrefixes, count);
    }
    
    public int getScopeForContext(final int context) {
        int scope;
        for (scope = this.fCurrentScope; context < this.fScope[scope]; --scope) {}
        return scope;
    }
    
    @Override
    public String getPrefix(final String uri) {
        return this.getPrefix(uri, this.fNamespaceSize, this.fContext[this.fScope[this.fCurrentScope]]);
    }
    
    @Override
    public String getURI(final String prefix) {
        return this.getURI(prefix, this.fNamespaceSize, this.fContext[this.fScope[this.fCurrentScope]]);
    }
    
    public String getPrefix(final String uri, final int context) {
        return this.getPrefix(uri, this.fContext[context + 1], this.fContext[this.fScope[this.getScopeForContext(context)]]);
    }
    
    public String getURI(final String prefix, final int context) {
        return this.getURI(prefix, this.fContext[context + 1], this.fContext[this.fScope[this.getScopeForContext(context)]]);
    }
    
    public String getPrefix(final String uri, final int start, final int end) {
        if (uri == NamespaceContext.XML_URI) {
            return XMLSymbols.PREFIX_XML;
        }
        if (uri == NamespaceContext.XMLNS_URI) {
            return XMLSymbols.PREFIX_XMLNS;
        }
        for (int i = start; i > end; i -= 2) {
            if (this.fNamespace[i - 1] == uri && this.getURI(this.fNamespace[i - 2]) == uri) {
                return this.fNamespace[i - 2];
            }
        }
        return null;
    }
    
    public String getURI(final String prefix, final int start, final int end) {
        if (prefix == XMLSymbols.PREFIX_XML) {
            return NamespaceContext.XML_URI;
        }
        if (prefix == XMLSymbols.PREFIX_XMLNS) {
            return NamespaceContext.XMLNS_URI;
        }
        for (int i = start; i > end; i -= 2) {
            if (this.fNamespace[i - 2] == prefix) {
                return this.fNamespace[i - 1];
            }
        }
        return null;
    }
    
    @Override
    public void reset() {
        this.fCurrentContext = this.fScope[this.fCurrentScope];
        this.fNamespaceSize = this.fContext[this.fCurrentContext];
    }
    
    public void pushScope() {
        if (this.fCurrentScope + 1 == this.fScope.length) {
            final int[] contextarray = new int[this.fScope.length * 2];
            System.arraycopy(this.fScope, 0, contextarray, 0, this.fScope.length);
            this.fScope = contextarray;
        }
        this.pushContext();
        this.fScope[++this.fCurrentScope] = this.fCurrentContext;
    }
    
    public void popScope() {
        this.fCurrentContext = this.fScope[this.fCurrentScope--];
        this.popContext();
    }
}
