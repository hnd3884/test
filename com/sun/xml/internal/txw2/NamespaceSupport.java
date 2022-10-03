package com.sun.xml.internal.txw2;

import java.util.Hashtable;
import java.util.Vector;
import java.util.EmptyStackException;
import java.util.Enumeration;

final class NamespaceSupport
{
    public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
    public static final String NSDECL = "http://www.w3.org/xmlns/2000/";
    private static final Enumeration EMPTY_ENUMERATION;
    private Context[] contexts;
    private Context currentContext;
    private int contextPos;
    private boolean namespaceDeclUris;
    
    public NamespaceSupport() {
        this.reset();
    }
    
    public void reset() {
        this.contexts = new Context[32];
        this.namespaceDeclUris = false;
        this.contextPos = 0;
        this.contexts[this.contextPos] = (this.currentContext = new Context());
        this.currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
    }
    
    public void pushContext() {
        int max = this.contexts.length;
        ++this.contextPos;
        if (this.contextPos >= max) {
            final Context[] newContexts = new Context[max * 2];
            System.arraycopy(this.contexts, 0, newContexts, 0, max);
            max *= 2;
            this.contexts = newContexts;
        }
        this.currentContext = this.contexts[this.contextPos];
        if (this.currentContext == null) {
            this.contexts[this.contextPos] = (this.currentContext = new Context());
        }
        if (this.contextPos > 0) {
            this.currentContext.setParent(this.contexts[this.contextPos - 1]);
        }
    }
    
    public void popContext() {
        this.contexts[this.contextPos].clear();
        --this.contextPos;
        if (this.contextPos < 0) {
            throw new EmptyStackException();
        }
        this.currentContext = this.contexts[this.contextPos];
    }
    
    public boolean declarePrefix(final String prefix, final String uri) {
        if (prefix.equals("xml") || prefix.equals("xmlns")) {
            return false;
        }
        this.currentContext.declarePrefix(prefix, uri);
        return true;
    }
    
    public String[] processName(final String qName, final String[] parts, final boolean isAttribute) {
        final String[] myParts = this.currentContext.processName(qName, isAttribute);
        if (myParts == null) {
            return null;
        }
        parts[0] = myParts[0];
        parts[1] = myParts[1];
        parts[2] = myParts[2];
        return parts;
    }
    
    public String getURI(final String prefix) {
        return this.currentContext.getURI(prefix);
    }
    
    public Enumeration getPrefixes() {
        return this.currentContext.getPrefixes();
    }
    
    public String getPrefix(final String uri) {
        return this.currentContext.getPrefix(uri);
    }
    
    public Enumeration getPrefixes(final String uri) {
        final Vector prefixes = new Vector();
        final Enumeration allPrefixes = this.getPrefixes();
        while (allPrefixes.hasMoreElements()) {
            final String prefix = allPrefixes.nextElement();
            if (uri.equals(this.getURI(prefix))) {
                prefixes.addElement(prefix);
            }
        }
        return prefixes.elements();
    }
    
    public Enumeration getDeclaredPrefixes() {
        return this.currentContext.getDeclaredPrefixes();
    }
    
    public void setNamespaceDeclUris(final boolean value) {
        if (this.contextPos != 0) {
            throw new IllegalStateException();
        }
        if (value == this.namespaceDeclUris) {
            return;
        }
        this.namespaceDeclUris = value;
        if (value) {
            this.currentContext.declarePrefix("xmlns", "http://www.w3.org/xmlns/2000/");
        }
        else {
            this.contexts[this.contextPos] = (this.currentContext = new Context());
            this.currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
        }
    }
    
    public boolean isNamespaceDeclUris() {
        return this.namespaceDeclUris;
    }
    
    static {
        EMPTY_ENUMERATION = new Vector().elements();
    }
    
    final class Context
    {
        Hashtable prefixTable;
        Hashtable uriTable;
        Hashtable elementNameTable;
        Hashtable attributeNameTable;
        String defaultNS;
        private Vector declarations;
        private boolean declSeen;
        private Context parent;
        
        Context() {
            this.defaultNS = "";
            this.declarations = null;
            this.declSeen = false;
            this.parent = null;
            this.copyTables();
        }
        
        void setParent(final Context parent) {
            this.parent = parent;
            this.declarations = null;
            this.prefixTable = parent.prefixTable;
            this.uriTable = parent.uriTable;
            this.elementNameTable = parent.elementNameTable;
            this.attributeNameTable = parent.attributeNameTable;
            this.defaultNS = parent.defaultNS;
            this.declSeen = false;
        }
        
        void clear() {
            this.parent = null;
            this.prefixTable = null;
            this.uriTable = null;
            this.elementNameTable = null;
            this.attributeNameTable = null;
            this.defaultNS = "";
        }
        
        void declarePrefix(String prefix, String uri) {
            if (!this.declSeen) {
                this.copyTables();
            }
            if (this.declarations == null) {
                this.declarations = new Vector();
            }
            prefix = prefix.intern();
            uri = uri.intern();
            if ("".equals(prefix)) {
                this.defaultNS = uri;
            }
            else {
                this.prefixTable.put(prefix, uri);
                this.uriTable.put(uri, prefix);
            }
            this.declarations.addElement(prefix);
        }
        
        String[] processName(final String qName, final boolean isAttribute) {
            Hashtable table;
            if (isAttribute) {
                table = this.attributeNameTable;
            }
            else {
                table = this.elementNameTable;
            }
            String[] name = table.get(qName);
            if (name != null) {
                return name;
            }
            name = new String[] { null, null, qName.intern() };
            final int index = qName.indexOf(58);
            if (index == -1) {
                if (isAttribute) {
                    if (qName == "xmlns" && NamespaceSupport.this.namespaceDeclUris) {
                        name[0] = "http://www.w3.org/xmlns/2000/";
                    }
                    else {
                        name[0] = "";
                    }
                }
                else {
                    name[0] = this.defaultNS;
                }
                name[1] = name[2];
            }
            else {
                final String prefix = qName.substring(0, index);
                final String local = qName.substring(index + 1);
                String uri;
                if ("".equals(prefix)) {
                    uri = this.defaultNS;
                }
                else {
                    uri = this.prefixTable.get(prefix);
                }
                if (uri == null || (!isAttribute && "xmlns".equals(prefix))) {
                    return null;
                }
                name[0] = uri;
                name[1] = local.intern();
            }
            table.put(name[2], name);
            return name;
        }
        
        String getURI(final String prefix) {
            if ("".equals(prefix)) {
                return this.defaultNS;
            }
            if (this.prefixTable == null) {
                return null;
            }
            return this.prefixTable.get(prefix);
        }
        
        String getPrefix(final String uri) {
            if (this.uriTable != null) {
                final String uriPrefix = this.uriTable.get(uri);
                if (uriPrefix == null) {
                    return null;
                }
                final String verifyNamespace = this.prefixTable.get(uriPrefix);
                if (uri.equals(verifyNamespace)) {
                    return uriPrefix;
                }
            }
            return null;
        }
        
        Enumeration getDeclaredPrefixes() {
            if (this.declarations == null) {
                return NamespaceSupport.EMPTY_ENUMERATION;
            }
            return this.declarations.elements();
        }
        
        Enumeration getPrefixes() {
            if (this.prefixTable == null) {
                return NamespaceSupport.EMPTY_ENUMERATION;
            }
            return this.prefixTable.keys();
        }
        
        private void copyTables() {
            if (this.prefixTable != null) {
                this.prefixTable = (Hashtable)this.prefixTable.clone();
            }
            else {
                this.prefixTable = new Hashtable();
            }
            if (this.uriTable != null) {
                this.uriTable = (Hashtable)this.uriTable.clone();
            }
            else {
                this.uriTable = new Hashtable();
            }
            this.elementNameTable = new Hashtable();
            this.attributeNameTable = new Hashtable();
            this.declSeen = true;
        }
    }
}
