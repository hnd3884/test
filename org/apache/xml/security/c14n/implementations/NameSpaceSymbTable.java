package org.apache.xml.security.c14n.implementations;

import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class NameSpaceSymbTable
{
    SymbMap symb;
    int nameSpaces;
    List level;
    boolean cloned;
    static final String XMLNS = "xmlns";
    static final SymbMap initialMap;
    
    public NameSpaceSymbTable() {
        this.nameSpaces = 0;
        this.cloned = true;
        this.level = new ArrayList(10);
        this.symb = (SymbMap)NameSpaceSymbTable.initialMap.clone();
    }
    
    public void getUnrenderedNodes(final Collection collection) {
        final Iterator iterator = this.symb.entrySet().iterator();
        while (iterator.hasNext()) {
            final NameSpaceSymbEntry nameSpaceSymbEntry = (NameSpaceSymbEntry)iterator.next();
            if (!nameSpaceSymbEntry.rendered && nameSpaceSymbEntry.n != null) {
                final NameSpaceSymbEntry nameSpaceSymbEntry2 = (NameSpaceSymbEntry)nameSpaceSymbEntry.clone();
                this.needsClone();
                this.symb.put(nameSpaceSymbEntry2.prefix, nameSpaceSymbEntry2);
                nameSpaceSymbEntry2.lastrendered = nameSpaceSymbEntry2.uri;
                nameSpaceSymbEntry2.rendered = true;
                collection.add(nameSpaceSymbEntry2.n);
            }
        }
    }
    
    public void outputNodePush() {
        ++this.nameSpaces;
        this.push();
    }
    
    public void outputNodePop() {
        --this.nameSpaces;
        this.pop();
    }
    
    public void push() {
        this.level.add(null);
        this.cloned = false;
    }
    
    public void pop() {
        final int n = this.level.size() - 1;
        final Object remove = this.level.remove(n);
        if (remove != null) {
            this.symb = (SymbMap)remove;
            if (n == 0) {
                this.cloned = false;
            }
            else {
                this.cloned = (this.level.get(n - 1) != this.symb);
            }
        }
        else {
            this.cloned = false;
        }
    }
    
    final void needsClone() {
        if (!this.cloned) {
            this.level.set(this.level.size() - 1, this.symb);
            this.symb = (SymbMap)this.symb.clone();
            this.cloned = true;
        }
    }
    
    public Attr getMapping(final String s) {
        final NameSpaceSymbEntry value = this.symb.get(s);
        if (value == null) {
            return null;
        }
        if (value.rendered) {
            return null;
        }
        final NameSpaceSymbEntry nameSpaceSymbEntry = (NameSpaceSymbEntry)value.clone();
        this.needsClone();
        this.symb.put(s, nameSpaceSymbEntry);
        nameSpaceSymbEntry.rendered = true;
        nameSpaceSymbEntry.level = this.nameSpaces;
        nameSpaceSymbEntry.lastrendered = nameSpaceSymbEntry.uri;
        return nameSpaceSymbEntry.n;
    }
    
    public Attr getMappingWithoutRendered(final String s) {
        final NameSpaceSymbEntry value = this.symb.get(s);
        if (value == null) {
            return null;
        }
        if (value.rendered) {
            return null;
        }
        return value.n;
    }
    
    public boolean addMapping(final String s, final String s2, final Attr attr) {
        final NameSpaceSymbEntry value = this.symb.get(s);
        if (value != null && s2.equals(value.uri)) {
            return false;
        }
        final NameSpaceSymbEntry nameSpaceSymbEntry = new NameSpaceSymbEntry(s2, attr, false, s);
        this.needsClone();
        this.symb.put(s, nameSpaceSymbEntry);
        if (value != null) {
            nameSpaceSymbEntry.lastrendered = value.lastrendered;
            if (value.lastrendered != null && value.lastrendered.equals(s2)) {
                nameSpaceSymbEntry.rendered = true;
            }
        }
        return true;
    }
    
    public Node addMappingAndRender(final String s, final String s2, final Attr attr) {
        final NameSpaceSymbEntry value = this.symb.get(s);
        if (value != null && s2.equals(value.uri)) {
            if (!value.rendered) {
                final NameSpaceSymbEntry nameSpaceSymbEntry = (NameSpaceSymbEntry)value.clone();
                this.needsClone();
                this.symb.put(s, nameSpaceSymbEntry);
                nameSpaceSymbEntry.lastrendered = s2;
                nameSpaceSymbEntry.rendered = true;
                return nameSpaceSymbEntry.n;
            }
            return null;
        }
        else {
            final NameSpaceSymbEntry nameSpaceSymbEntry2 = new NameSpaceSymbEntry(s2, attr, true, s);
            nameSpaceSymbEntry2.lastrendered = s2;
            this.needsClone();
            this.symb.put(s, nameSpaceSymbEntry2);
            if (value != null && value.lastrendered != null && value.lastrendered.equals(s2)) {
                nameSpaceSymbEntry2.rendered = true;
                return null;
            }
            return nameSpaceSymbEntry2.n;
        }
    }
    
    public int getLevel() {
        return this.level.size();
    }
    
    public void removeMapping(final String s) {
        if (this.symb.get(s) != null) {
            this.needsClone();
            this.symb.put(s, null);
        }
    }
    
    public void removeMappingIfNotRender(final String s) {
        final NameSpaceSymbEntry value = this.symb.get(s);
        if (value != null && !value.rendered) {
            this.needsClone();
            this.symb.put(s, null);
        }
    }
    
    public boolean removeMappingIfRender(final String s) {
        final NameSpaceSymbEntry value = this.symb.get(s);
        if (value != null && value.rendered) {
            this.needsClone();
            this.symb.put(s, null);
        }
        return false;
    }
    
    static {
        initialMap = new SymbMap();
        final NameSpaceSymbEntry nameSpaceSymbEntry = new NameSpaceSymbEntry("", null, true, "xmlns");
        nameSpaceSymbEntry.lastrendered = "";
        NameSpaceSymbTable.initialMap.put("xmlns", nameSpaceSymbEntry);
    }
}
