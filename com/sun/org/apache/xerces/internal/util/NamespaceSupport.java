package com.sun.org.apache.xerces.internal.util;

import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;

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
    
    public NamespaceSupport(final NamespaceContext context) {
        this.fNamespace = new String[32];
        this.fContext = new int[8];
        this.fPrefixes = new String[16];
        this.pushContext();
        final Enumeration prefixes = context.getAllPrefixes();
        while (prefixes.hasMoreElements()) {
            final String prefix = prefixes.nextElement();
            final String uri = context.getURI(prefix);
            this.declarePrefix(prefix, uri);
        }
    }
    
    @Override
    public void reset() {
        this.fNamespaceSize = 0;
        this.fCurrentContext = 0;
        this.fNamespace[this.fNamespaceSize++] = XMLSymbols.PREFIX_XML;
        this.fNamespace[this.fNamespaceSize++] = NamespaceContext.XML_URI;
        this.fNamespace[this.fNamespaceSize++] = XMLSymbols.PREFIX_XMLNS;
        this.fNamespace[this.fNamespaceSize++] = NamespaceContext.XMLNS_URI;
        this.fContext[this.fCurrentContext] = this.fNamespaceSize;
    }
    
    @Override
    public void pushContext() {
        if (this.fCurrentContext + 1 == this.fContext.length) {
            final int[] contextarray = new int[this.fContext.length * 2];
            System.arraycopy(this.fContext, 0, contextarray, 0, this.fContext.length);
            this.fContext = contextarray;
        }
        this.fContext[++this.fCurrentContext] = this.fNamespaceSize;
    }
    
    @Override
    public void popContext() {
        this.fNamespaceSize = this.fContext[this.fCurrentContext--];
    }
    
    @Override
    public boolean declarePrefix(final String prefix, final String uri) {
        if (prefix == XMLSymbols.PREFIX_XML || prefix == XMLSymbols.PREFIX_XMLNS) {
            return false;
        }
        for (int i = this.fNamespaceSize; i > this.fContext[this.fCurrentContext]; i -= 2) {
            if (this.fNamespace[i - 2] == prefix) {
                this.fNamespace[i - 1] = uri;
                return true;
            }
        }
        if (this.fNamespaceSize == this.fNamespace.length) {
            final String[] namespacearray = new String[this.fNamespaceSize * 2];
            System.arraycopy(this.fNamespace, 0, namespacearray, 0, this.fNamespaceSize);
            this.fNamespace = namespacearray;
        }
        this.fNamespace[this.fNamespaceSize++] = prefix;
        this.fNamespace[this.fNamespaceSize++] = uri;
        return true;
    }
    
    @Override
    public String getURI(final String prefix) {
        for (int i = this.fNamespaceSize; i > 0; i -= 2) {
            if (this.fNamespace[i - 2] == prefix) {
                return this.fNamespace[i - 1];
            }
        }
        return null;
    }
    
    @Override
    public String getPrefix(final String uri) {
        for (int i = this.fNamespaceSize; i > 0; i -= 2) {
            if (this.fNamespace[i - 1] == uri && this.getURI(this.fNamespace[i - 2]) == uri) {
                return this.fNamespace[i - 2];
            }
        }
        return null;
    }
    
    @Override
    public int getDeclaredPrefixCount() {
        return (this.fNamespaceSize - this.fContext[this.fCurrentContext]) / 2;
    }
    
    @Override
    public String getDeclaredPrefixAt(final int index) {
        return this.fNamespace[this.fContext[this.fCurrentContext] + index * 2];
    }
    
    public Iterator getPrefixes() {
        int count = 0;
        if (this.fPrefixes.length < this.fNamespace.length / 2) {
            final String[] prefixes = new String[this.fNamespaceSize];
            this.fPrefixes = prefixes;
        }
        String prefix = null;
        boolean unique = true;
        for (int i = 2; i < this.fNamespaceSize - 2; i += 2) {
            prefix = this.fNamespace[i + 2];
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
        return new IteratorPrefixes(this.fPrefixes, count);
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
        for (int i = 2; i < this.fNamespaceSize - 2; i += 2) {
            prefix = this.fNamespace[i + 2];
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
    
    public Vector getPrefixes(final String uri) {
        final int count = 0;
        final String prefix = null;
        final boolean unique = true;
        final Vector prefixList = new Vector();
        for (int i = this.fNamespaceSize; i > 0; i -= 2) {
            if (this.fNamespace[i - 1] == uri && !prefixList.contains(this.fNamespace[i - 2])) {
                prefixList.add(this.fNamespace[i - 2]);
            }
        }
        return prefixList;
    }
    
    public boolean containsPrefix(final String prefix) {
        for (int i = this.fNamespaceSize; i > 0; i -= 2) {
            if (this.fNamespace[i - 2] == prefix) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsPrefixInCurrentContext(final String prefix) {
        for (int i = this.fContext[this.fCurrentContext]; i < this.fNamespaceSize; i += 2) {
            if (this.fNamespace[i] == prefix) {
                return true;
            }
        }
        return false;
    }
    
    protected final class IteratorPrefixes implements Iterator
    {
        private String[] prefixes;
        private int counter;
        private int size;
        
        public IteratorPrefixes(final String[] prefixes, final int size) {
            this.counter = 0;
            this.size = 0;
            this.prefixes = prefixes;
            this.size = size;
        }
        
        @Override
        public boolean hasNext() {
            return this.counter < this.size;
        }
        
        @Override
        public Object next() {
            if (this.counter < this.size) {
                return NamespaceSupport.this.fPrefixes[this.counter++];
            }
            throw new NoSuchElementException("Illegal access to Namespace prefixes enumeration.");
        }
        
        @Override
        public String toString() {
            final StringBuffer buf = new StringBuffer();
            for (int i = 0; i < this.size; ++i) {
                buf.append(this.prefixes[i]);
                buf.append(" ");
            }
            return buf.toString();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
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
        
        @Override
        public boolean hasMoreElements() {
            return this.counter < this.size;
        }
        
        @Override
        public Object nextElement() {
            if (this.counter < this.size) {
                return NamespaceSupport.this.fPrefixes[this.counter++];
            }
            throw new NoSuchElementException("Illegal access to Namespace prefixes enumeration.");
        }
        
        @Override
        public String toString() {
            final StringBuffer buf = new StringBuffer();
            for (int i = 0; i < this.size; ++i) {
                buf.append(this.prefixes[i]);
                buf.append(" ");
            }
            return buf.toString();
        }
    }
}
