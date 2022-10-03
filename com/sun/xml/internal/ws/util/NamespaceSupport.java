package com.sun.xml.internal.ws.util;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.EmptyStackException;

public final class NamespaceSupport
{
    public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
    private static final Iterable<String> EMPTY_ENUMERATION;
    private Context[] contexts;
    private Context currentContext;
    private int contextPos;
    
    public NamespaceSupport() {
        this.reset();
    }
    
    public NamespaceSupport(final NamespaceSupport that) {
        this.contexts = new Context[that.contexts.length];
        this.currentContext = null;
        this.contextPos = that.contextPos;
        Context currentParent = null;
        for (int i = 0; i < that.contexts.length; ++i) {
            final Context thatContext = that.contexts[i];
            if (thatContext == null) {
                this.contexts[i] = null;
            }
            else {
                final Context thisContext = new Context(thatContext, currentParent);
                this.contexts[i] = thisContext;
                if (that.currentContext == thatContext) {
                    this.currentContext = thisContext;
                }
                currentParent = thisContext;
            }
        }
    }
    
    public void reset() {
        this.contexts = new Context[32];
        this.contextPos = 0;
        this.contexts[this.contextPos] = (this.currentContext = new Context());
        this.currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
    }
    
    public void pushContext() {
        final int max = this.contexts.length;
        ++this.contextPos;
        if (this.contextPos >= max) {
            final Context[] newContexts = new Context[max * 2];
            System.arraycopy(this.contexts, 0, newContexts, 0, max);
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
        --this.contextPos;
        if (this.contextPos < 0) {
            throw new EmptyStackException();
        }
        this.currentContext = this.contexts[this.contextPos];
    }
    
    public void slideContextUp() {
        --this.contextPos;
        this.currentContext = this.contexts[this.contextPos];
    }
    
    public void slideContextDown() {
        ++this.contextPos;
        if (this.contexts[this.contextPos] == null) {
            this.contexts[this.contextPos] = this.contexts[this.contextPos - 1];
        }
        this.currentContext = this.contexts[this.contextPos];
    }
    
    public boolean declarePrefix(final String prefix, final String uri) {
        if ((prefix.equals("xml") && !uri.equals("http://www.w3.org/XML/1998/namespace")) || prefix.equals("xmlns")) {
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
    
    public Iterable<String> getPrefixes() {
        return this.currentContext.getPrefixes();
    }
    
    public String getPrefix(final String uri) {
        return this.currentContext.getPrefix(uri);
    }
    
    public Iterator getPrefixes(final String uri) {
        final List prefixes = new ArrayList();
        for (final String prefix : this.getPrefixes()) {
            if (uri.equals(this.getURI(prefix))) {
                prefixes.add(prefix);
            }
        }
        return prefixes.iterator();
    }
    
    public Iterable<String> getDeclaredPrefixes() {
        return this.currentContext.getDeclaredPrefixes();
    }
    
    static {
        EMPTY_ENUMERATION = new ArrayList<String>();
    }
    
    static final class Context
    {
        HashMap prefixTable;
        HashMap uriTable;
        HashMap elementNameTable;
        HashMap attributeNameTable;
        String defaultNS;
        private ArrayList declarations;
        private boolean tablesDirty;
        private Context parent;
        
        Context() {
            this.defaultNS = null;
            this.declarations = null;
            this.tablesDirty = false;
            this.parent = null;
            this.copyTables();
        }
        
        Context(final Context that, final Context newParent) {
            this.defaultNS = null;
            this.declarations = null;
            this.tablesDirty = false;
            this.parent = null;
            if (that == null) {
                this.copyTables();
                return;
            }
            if (newParent != null && !that.tablesDirty) {
                this.prefixTable = (HashMap)((that.prefixTable == that.parent.prefixTable) ? newParent.prefixTable : that.prefixTable.clone());
                this.uriTable = (HashMap)((that.uriTable == that.parent.uriTable) ? newParent.uriTable : that.uriTable.clone());
                this.elementNameTable = (HashMap)((that.elementNameTable == that.parent.elementNameTable) ? newParent.elementNameTable : that.elementNameTable.clone());
                this.attributeNameTable = (HashMap)((that.attributeNameTable == that.parent.attributeNameTable) ? newParent.attributeNameTable : that.attributeNameTable.clone());
                this.defaultNS = ((that.defaultNS == that.parent.defaultNS) ? newParent.defaultNS : that.defaultNS);
            }
            else {
                this.prefixTable = (HashMap)that.prefixTable.clone();
                this.uriTable = (HashMap)that.uriTable.clone();
                this.elementNameTable = (HashMap)that.elementNameTable.clone();
                this.attributeNameTable = (HashMap)that.attributeNameTable.clone();
                this.defaultNS = that.defaultNS;
            }
            this.tablesDirty = that.tablesDirty;
            this.parent = newParent;
            this.declarations = ((that.declarations == null) ? null : ((ArrayList)that.declarations.clone()));
        }
        
        void setParent(final Context parent) {
            this.parent = parent;
            this.declarations = null;
            this.prefixTable = parent.prefixTable;
            this.uriTable = parent.uriTable;
            this.elementNameTable = parent.elementNameTable;
            this.attributeNameTable = parent.attributeNameTable;
            this.defaultNS = parent.defaultNS;
            this.tablesDirty = false;
        }
        
        void declarePrefix(String prefix, String uri) {
            if (!this.tablesDirty) {
                this.copyTables();
            }
            if (this.declarations == null) {
                this.declarations = new ArrayList();
            }
            prefix = prefix.intern();
            uri = uri.intern();
            if ("".equals(prefix)) {
                if ("".equals(uri)) {
                    this.defaultNS = null;
                }
                else {
                    this.defaultNS = uri;
                }
            }
            else {
                this.prefixTable.put(prefix, uri);
                this.uriTable.put(uri, prefix);
            }
            this.declarations.add(prefix);
        }
        
        String[] processName(final String qName, final boolean isAttribute) {
            Map table;
            if (isAttribute) {
                table = this.elementNameTable;
            }
            else {
                table = this.attributeNameTable;
            }
            String[] name = table.get(qName);
            if (name != null) {
                return name;
            }
            name = new String[3];
            final int index = qName.indexOf(58);
            if (index == -1) {
                if (isAttribute || this.defaultNS == null) {
                    name[0] = "";
                }
                else {
                    name[0] = this.defaultNS;
                }
                name[2] = (name[1] = qName.intern());
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
                if (uri == null) {
                    return null;
                }
                name[0] = uri;
                name[1] = local.intern();
                name[2] = qName.intern();
            }
            table.put(name[2], name);
            this.tablesDirty = true;
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
            if (this.uriTable == null) {
                return null;
            }
            return this.uriTable.get(uri);
        }
        
        Iterable<String> getDeclaredPrefixes() {
            if (this.declarations == null) {
                return NamespaceSupport.EMPTY_ENUMERATION;
            }
            return this.declarations;
        }
        
        Iterable<String> getPrefixes() {
            if (this.prefixTable == null) {
                return NamespaceSupport.EMPTY_ENUMERATION;
            }
            return this.prefixTable.keySet();
        }
        
        private void copyTables() {
            if (this.prefixTable != null) {
                this.prefixTable = (HashMap)this.prefixTable.clone();
            }
            else {
                this.prefixTable = new HashMap();
            }
            if (this.uriTable != null) {
                this.uriTable = (HashMap)this.uriTable.clone();
            }
            else {
                this.uriTable = new HashMap();
            }
            this.elementNameTable = new HashMap();
            this.attributeNameTable = new HashMap();
            this.tablesDirty = true;
        }
    }
}
