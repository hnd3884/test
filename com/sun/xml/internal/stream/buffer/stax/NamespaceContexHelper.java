package com.sun.xml.internal.stream.buffer.stax;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;

public final class NamespaceContexHelper implements NamespaceContextEx
{
    private static int DEFAULT_SIZE;
    private String[] prefixes;
    private String[] namespaceURIs;
    private int namespacePosition;
    private int[] contexts;
    private int contextPosition;
    
    public NamespaceContexHelper() {
        this.prefixes = new String[NamespaceContexHelper.DEFAULT_SIZE];
        this.namespaceURIs = new String[NamespaceContexHelper.DEFAULT_SIZE];
        this.contexts = new int[NamespaceContexHelper.DEFAULT_SIZE];
        this.prefixes[0] = "xml";
        this.namespaceURIs[0] = "http://www.w3.org/XML/1998/namespace";
        this.prefixes[1] = "xmlns";
        this.namespaceURIs[1] = "http://www.w3.org/2000/xmlns/";
        this.namespacePosition = 2;
    }
    
    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        prefix = prefix.intern();
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            final String declaredPrefix = this.prefixes[i];
            if (declaredPrefix == prefix) {
                return this.namespaceURIs[i];
            }
        }
        return "";
    }
    
    @Override
    public String getPrefix(final String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            final String declaredNamespaceURI = this.namespaceURIs[i];
            if (declaredNamespaceURI == namespaceURI || declaredNamespaceURI.equals(namespaceURI)) {
                final String declaredPrefix = this.prefixes[i];
                ++i;
                while (i < this.namespacePosition) {
                    if (declaredPrefix == this.prefixes[i]) {
                        return null;
                    }
                    ++i;
                }
                return declaredPrefix;
            }
        }
        return null;
    }
    
    @Override
    public Iterator getPrefixes(final String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        final List<String> l = new ArrayList<String>();
    Label_0106:
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            final String declaredNamespaceURI = this.namespaceURIs[i];
            if (declaredNamespaceURI == namespaceURI || declaredNamespaceURI.equals(namespaceURI)) {
                final String declaredPrefix = this.prefixes[i];
                for (int j = i + 1; j < this.namespacePosition; ++j) {
                    if (declaredPrefix == this.prefixes[j]) {
                        continue Label_0106;
                    }
                }
                l.add(declaredPrefix);
            }
        }
        return l.iterator();
    }
    
    @Override
    public Iterator<Binding> iterator() {
        if (this.namespacePosition == 2) {
            return Collections.EMPTY_LIST.iterator();
        }
        final List<Binding> namespaces = new ArrayList<Binding>(this.namespacePosition);
        for (int i = this.namespacePosition - 1; i >= 2; --i) {
            final String declaredPrefix = this.prefixes[i];
            for (int j = i + 1; j < this.namespacePosition && declaredPrefix != this.prefixes[j]; ++j) {
                namespaces.add(new NamespaceBindingImpl(i));
            }
        }
        return namespaces.iterator();
    }
    
    public void declareDefaultNamespace(final String namespaceURI) {
        this.declareNamespace("", namespaceURI);
    }
    
    public void declareNamespace(String prefix, String namespaceURI) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        prefix = prefix.intern();
        if (prefix == "xml" || prefix == "xmlns") {
            return;
        }
        if (namespaceURI != null) {
            namespaceURI = namespaceURI.intern();
        }
        if (this.namespacePosition == this.namespaceURIs.length) {
            this.resizeNamespaces();
        }
        this.prefixes[this.namespacePosition] = prefix;
        this.namespaceURIs[this.namespacePosition++] = namespaceURI;
    }
    
    private void resizeNamespaces() {
        final int newLength = this.namespaceURIs.length * 3 / 2 + 1;
        final String[] newPrefixes = new String[newLength];
        System.arraycopy(this.prefixes, 0, newPrefixes, 0, this.prefixes.length);
        this.prefixes = newPrefixes;
        final String[] newNamespaceURIs = new String[newLength];
        System.arraycopy(this.namespaceURIs, 0, newNamespaceURIs, 0, this.namespaceURIs.length);
        this.namespaceURIs = newNamespaceURIs;
    }
    
    public void pushContext() {
        if (this.contextPosition == this.contexts.length) {
            this.resizeContexts();
        }
        this.contexts[this.contextPosition++] = this.namespacePosition;
    }
    
    private void resizeContexts() {
        final int[] newContexts = new int[this.contexts.length * 3 / 2 + 1];
        System.arraycopy(this.contexts, 0, newContexts, 0, this.contexts.length);
        this.contexts = newContexts;
    }
    
    public void popContext() {
        if (this.contextPosition > 0) {
            final int[] contexts = this.contexts;
            final int contextPosition = this.contextPosition - 1;
            this.contextPosition = contextPosition;
            this.namespacePosition = contexts[contextPosition];
        }
    }
    
    public void resetContexts() {
        this.namespacePosition = 2;
    }
    
    static {
        NamespaceContexHelper.DEFAULT_SIZE = 8;
    }
    
    private final class NamespaceBindingImpl implements Binding
    {
        int index;
        
        NamespaceBindingImpl(final int index) {
            this.index = index;
        }
        
        @Override
        public String getPrefix() {
            return NamespaceContexHelper.this.prefixes[this.index];
        }
        
        @Override
        public String getNamespaceURI() {
            return NamespaceContexHelper.this.namespaceURIs[this.index];
        }
    }
}
