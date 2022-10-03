package com.sun.xml.internal.fastinfoset.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public final class NamespaceContextImplementation implements NamespaceContext
{
    private static int DEFAULT_SIZE;
    private String[] prefixes;
    private String[] namespaceURIs;
    private int namespacePosition;
    private int[] contexts;
    private int contextPosition;
    private int currentContext;
    
    public NamespaceContextImplementation() {
        this.prefixes = new String[NamespaceContextImplementation.DEFAULT_SIZE];
        this.namespaceURIs = new String[NamespaceContextImplementation.DEFAULT_SIZE];
        this.contexts = new int[NamespaceContextImplementation.DEFAULT_SIZE];
        this.prefixes[0] = "xml";
        this.namespaceURIs[0] = "http://www.w3.org/XML/1998/namespace";
        this.prefixes[1] = "xmlns";
        this.namespaceURIs[1] = "http://www.w3.org/2000/xmlns/";
        final int n = 2;
        this.namespacePosition = n;
        this.currentContext = n;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            final String declaredPrefix = this.prefixes[i];
            if (declaredPrefix.equals(prefix)) {
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
            if (declaredNamespaceURI.equals(namespaceURI)) {
                final String declaredPrefix = this.prefixes[i];
                boolean isOutOfScope = false;
                for (int j = i + 1; j < this.namespacePosition; ++j) {
                    if (declaredPrefix.equals(this.prefixes[j])) {
                        isOutOfScope = true;
                        break;
                    }
                }
                if (!isOutOfScope) {
                    return declaredPrefix;
                }
            }
        }
        return null;
    }
    
    public String getNonDefaultPrefix(final String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            final String declaredNamespaceURI = this.namespaceURIs[i];
            if (declaredNamespaceURI.equals(namespaceURI) && this.prefixes[i].length() > 0) {
                final String declaredPrefix = this.prefixes[i];
                ++i;
                while (i < this.namespacePosition) {
                    if (declaredPrefix.equals(this.prefixes[i])) {
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
        final List l = new ArrayList();
    Label_0103:
        for (int i = this.namespacePosition - 1; i >= 0; --i) {
            final String declaredNamespaceURI = this.namespaceURIs[i];
            if (declaredNamespaceURI.equals(namespaceURI)) {
                final String declaredPrefix = this.prefixes[i];
                for (int j = i + 1; j < this.namespacePosition; ++j) {
                    if (declaredPrefix.equals(this.prefixes[j])) {
                        continue Label_0103;
                    }
                }
                l.add(declaredPrefix);
            }
        }
        return l.iterator();
    }
    
    public String getPrefix(final int index) {
        return this.prefixes[index];
    }
    
    public String getNamespaceURI(final int index) {
        return this.namespaceURIs[index];
    }
    
    public int getCurrentContextStartIndex() {
        return this.currentContext;
    }
    
    public int getCurrentContextEndIndex() {
        return this.namespacePosition;
    }
    
    public boolean isCurrentContextEmpty() {
        return this.currentContext == this.namespacePosition;
    }
    
    public void declarePrefix(String prefix, String namespaceURI) {
        prefix = prefix.intern();
        namespaceURI = namespaceURI.intern();
        if (prefix == "xml" || prefix == "xmlns") {
            return;
        }
        for (int i = this.currentContext; i < this.namespacePosition; ++i) {
            final String declaredPrefix = this.prefixes[i];
            if (declaredPrefix == prefix) {
                this.prefixes[i] = prefix;
                this.namespaceURIs[i] = namespaceURI;
                return;
            }
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
        this.contexts[this.contextPosition++] = (this.currentContext = this.namespacePosition);
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
            final int n = contexts[contextPosition];
            this.currentContext = n;
            this.namespacePosition = n;
        }
    }
    
    public void reset() {
        final int n = 2;
        this.namespacePosition = n;
        this.currentContext = n;
    }
    
    static {
        NamespaceContextImplementation.DEFAULT_SIZE = 8;
    }
}
