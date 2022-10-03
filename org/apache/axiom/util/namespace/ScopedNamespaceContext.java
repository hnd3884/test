package org.apache.axiom.util.namespace;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class ScopedNamespaceContext extends AbstractNamespaceContext
{
    String[] prefixArray;
    String[] uriArray;
    int bindings;
    private int[] scopeIndexes;
    private int scopes;
    
    public ScopedNamespaceContext() {
        this.prefixArray = new String[16];
        this.uriArray = new String[16];
        this.scopeIndexes = new int[16];
    }
    
    public void setPrefix(final String prefix, final String namespaceURI) {
        if (prefix == null || namespaceURI == null) {
            throw new IllegalArgumentException("prefix and namespaceURI may not be null");
        }
        if (this.bindings == this.prefixArray.length) {
            final int len = this.prefixArray.length;
            final int newLen = len * 2;
            final String[] newPrefixArray = new String[newLen];
            System.arraycopy(this.prefixArray, 0, newPrefixArray, 0, len);
            final String[] newUriArray = new String[newLen];
            System.arraycopy(this.uriArray, 0, newUriArray, 0, len);
            this.prefixArray = newPrefixArray;
            this.uriArray = newUriArray;
        }
        this.prefixArray[this.bindings] = prefix;
        this.uriArray[this.bindings] = namespaceURI;
        ++this.bindings;
    }
    
    public void startScope() {
        if (this.scopes == this.scopeIndexes.length) {
            final int[] newScopeIndexes = new int[this.scopeIndexes.length * 2];
            System.arraycopy(this.scopeIndexes, 0, newScopeIndexes, 0, this.scopeIndexes.length);
            this.scopeIndexes = newScopeIndexes;
        }
        this.scopeIndexes[this.scopes++] = this.bindings;
    }
    
    public void endScope() {
        final int[] scopeIndexes = this.scopeIndexes;
        final int scopes = this.scopes - 1;
        this.scopes = scopes;
        this.bindings = scopeIndexes[scopes];
    }
    
    public int getBindingsCount() {
        return this.bindings;
    }
    
    public int getFirstBindingInCurrentScope() {
        return (this.scopes == 0) ? 0 : this.scopeIndexes[this.scopes - 1];
    }
    
    public String getPrefix(final int index) {
        return this.prefixArray[index];
    }
    
    public String getNamespaceURI(final int index) {
        return this.uriArray[index];
    }
    
    @Override
    protected String doGetNamespaceURI(final String prefix) {
        for (int i = this.bindings - 1; i >= 0; --i) {
            if (prefix.equals(this.prefixArray[i])) {
                return this.uriArray[i];
            }
        }
        return "";
    }
    
    @Override
    protected String doGetPrefix(final String namespaceURI) {
    Label_0070:
        for (int i = this.bindings - 1; i >= 0; --i) {
            if (namespaceURI.equals(this.uriArray[i])) {
                final String prefix = this.prefixArray[i];
                for (int j = i + 1; j < this.bindings; ++j) {
                    if (prefix.equals(this.prefixArray[j])) {
                        continue Label_0070;
                    }
                }
                return prefix;
            }
        }
        return null;
    }
    
    @Override
    protected Iterator doGetPrefixes(final String namespaceURI) {
        return new Iterator() {
            private int binding = ScopedNamespaceContext.this.bindings;
            private String next;
            
            public boolean hasNext() {
                if (this.next == null) {
                Label_0007:
                    while (--this.binding >= 0) {
                        if (namespaceURI.equals(ScopedNamespaceContext.this.uriArray[this.binding])) {
                            final String prefix = ScopedNamespaceContext.this.prefixArray[this.binding];
                            for (int j = this.binding + 1; j < ScopedNamespaceContext.this.bindings; ++j) {
                                if (prefix.equals(ScopedNamespaceContext.this.prefixArray[j])) {
                                    continue Label_0007;
                                }
                            }
                            this.next = prefix;
                            break;
                        }
                    }
                }
                return this.next != null;
            }
            
            public Object next() {
                if (this.hasNext()) {
                    final String result = this.next;
                    this.next = null;
                    return result;
                }
                throw new NoSuchElementException();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
