package org.apache.xerces.util;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import org.apache.xerces.xni.NamespaceContext;

public class NamespaceSupport implements NamespaceContext
{
    protected String[] fNamespace;
    protected int fNamespaceSize;
    protected int[] fContext;
    protected int fCurrentContext;
    protected String[] fPrefixes;
    
    public NamespaceSupport() {
        this.fNamespace = new String[32];
        this.fContext = new int[8];
        this.fPrefixes = new String[16];
    }
    
    public NamespaceSupport(final NamespaceContext namespaceContext) {
        this.fNamespace = new String[32];
        this.fContext = new int[8];
        this.fPrefixes = new String[16];
        this.pushContext();
        final Enumeration allPrefixes = namespaceContext.getAllPrefixes();
        while (allPrefixes.hasMoreElements()) {
            final String s = allPrefixes.nextElement();
            this.declarePrefix(s, namespaceContext.getURI(s));
        }
    }
    
    public void reset() {
        this.fNamespaceSize = 0;
        this.fCurrentContext = 0;
        this.fContext[this.fCurrentContext] = this.fNamespaceSize;
        this.fNamespace[this.fNamespaceSize++] = XMLSymbols.PREFIX_XML;
        this.fNamespace[this.fNamespaceSize++] = NamespaceContext.XML_URI;
        this.fNamespace[this.fNamespaceSize++] = XMLSymbols.PREFIX_XMLNS;
        this.fNamespace[this.fNamespaceSize++] = NamespaceContext.XMLNS_URI;
        ++this.fCurrentContext;
    }
    
    public void pushContext() {
        if (this.fCurrentContext + 1 == this.fContext.length) {
            final int[] fContext = new int[this.fContext.length * 2];
            System.arraycopy(this.fContext, 0, fContext, 0, this.fContext.length);
            this.fContext = fContext;
        }
        this.fContext[++this.fCurrentContext] = this.fNamespaceSize;
    }
    
    public void popContext() {
        this.fNamespaceSize = this.fContext[this.fCurrentContext--];
    }
    
    public boolean declarePrefix(final String s, final String s2) {
        if (s == XMLSymbols.PREFIX_XML || s == XMLSymbols.PREFIX_XMLNS) {
            return false;
        }
        for (int i = this.fNamespaceSize; i > this.fContext[this.fCurrentContext]; i -= 2) {
            if (this.fNamespace[i - 2] == s) {
                this.fNamespace[i - 1] = s2;
                return true;
            }
        }
        if (this.fNamespaceSize == this.fNamespace.length) {
            final String[] fNamespace = new String[this.fNamespaceSize * 2];
            System.arraycopy(this.fNamespace, 0, fNamespace, 0, this.fNamespaceSize);
            this.fNamespace = fNamespace;
        }
        this.fNamespace[this.fNamespaceSize++] = s;
        this.fNamespace[this.fNamespaceSize++] = s2;
        return true;
    }
    
    public boolean deletePrefix(final String s) {
        if (s == XMLSymbols.PREFIX_XML || s == XMLSymbols.PREFIX_XMLNS) {
            return false;
        }
        boolean b = false;
        final String[] fNamespace = new String[this.fNamespaceSize];
        int n = 0;
        for (int i = 0; i < this.fNamespaceSize; i += 2) {
            if (this.fNamespace[i] == s) {
                b = true;
            }
            else {
                fNamespace[n++] = this.fNamespace[i];
                fNamespace[n++] = this.fNamespace[i + 1];
            }
        }
        if (b) {
            this.fNamespaceSize -= 2;
            this.fNamespace = fNamespace;
            return true;
        }
        return false;
    }
    
    public String getURI(final String s) {
        for (int i = this.fNamespaceSize; i > 0; i -= 2) {
            if (this.fNamespace[i - 2] == s) {
                return this.fNamespace[i - 1];
            }
        }
        return null;
    }
    
    public String getPrefix(final String s) {
        for (int i = this.fNamespaceSize; i > 0; i -= 2) {
            if (this.fNamespace[i - 1] == s && this.getURI(this.fNamespace[i - 2]) == s) {
                return this.fNamespace[i - 2];
            }
        }
        return null;
    }
    
    public int getDeclaredPrefixCount() {
        return (this.fNamespaceSize - this.fContext[this.fCurrentContext]) / 2;
    }
    
    public String getDeclaredPrefixAt(final int n) {
        return this.fNamespace[this.fContext[this.fCurrentContext] + n * 2];
    }
    
    public Enumeration getAllPrefixes() {
        int n = 0;
        if (this.fPrefixes.length < this.fNamespace.length / 2) {
            this.fPrefixes = new String[this.fNamespaceSize];
        }
        int n2 = 1;
        for (int i = 2; i < this.fNamespaceSize - 2; i += 2) {
            final String s = this.fNamespace[i + 2];
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
    
    public boolean containsPrefix(final String s) {
        for (int i = this.fNamespaceSize; i > 0; i -= 2) {
            if (this.fNamespace[i - 2] == s) {
                return true;
            }
        }
        return false;
    }
    
    public String[] getNamespaceBindingInfo() {
        return this.fNamespace;
    }
    
    protected final class Prefixes implements Enumeration
    {
        private String[] prefixes;
        private int counter;
        private int size;
        
        public Prefixes(final String[] prefixes, final int size) {
            this.counter = 0;
            this.size = 0;
            this.prefixes = prefixes;
            this.size = size;
        }
        
        public boolean hasMoreElements() {
            return this.counter < this.size;
        }
        
        public Object nextElement() {
            if (this.counter < this.size) {
                return NamespaceSupport.this.fPrefixes[this.counter++];
            }
            throw new NoSuchElementException("Illegal access to Namespace prefixes enumeration.");
        }
        
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < this.size; ++i) {
                sb.append(this.prefixes[i]);
                sb.append(' ');
            }
            return sb.toString();
        }
    }
}
