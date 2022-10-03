package com.sun.xml.internal.bind.v2.runtime;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Map;

public final class AssociationMap<XmlNode>
{
    private final Map<XmlNode, Entry<XmlNode>> byElement;
    private final Map<Object, Entry<XmlNode>> byPeer;
    private final Set<XmlNode> usedNodes;
    
    public AssociationMap() {
        this.byElement = new IdentityHashMap<XmlNode, Entry<XmlNode>>();
        this.byPeer = new IdentityHashMap<Object, Entry<XmlNode>>();
        this.usedNodes = new HashSet<XmlNode>();
    }
    
    public void addInner(final XmlNode element, final Object inner) {
        Entry<XmlNode> e = this.byElement.get(element);
        if (e != null) {
            if (((Entry<Object>)e).inner != null) {
                this.byPeer.remove(((Entry<Object>)e).inner);
            }
            ((Entry<Object>)e).inner = inner;
        }
        else {
            e = new Entry<XmlNode>();
            ((Entry<Object>)e).element = element;
            ((Entry<Object>)e).inner = inner;
        }
        this.byElement.put(element, e);
        final Entry<XmlNode> old = this.byPeer.put(inner, e);
        if (old != null) {
            if (((Entry<Object>)old).outer != null) {
                this.byPeer.remove(((Entry<Object>)old).outer);
            }
            if (((Entry<Object>)old).element != null) {
                this.byElement.remove(((Entry<Object>)old).element);
            }
        }
    }
    
    public void addOuter(final XmlNode element, final Object outer) {
        Entry<XmlNode> e = this.byElement.get(element);
        if (e != null) {
            if (((Entry<Object>)e).outer != null) {
                this.byPeer.remove(((Entry<Object>)e).outer);
            }
            ((Entry<Object>)e).outer = outer;
        }
        else {
            e = new Entry<XmlNode>();
            ((Entry<Object>)e).element = element;
            ((Entry<Object>)e).outer = outer;
        }
        this.byElement.put(element, e);
        final Entry<XmlNode> old = this.byPeer.put(outer, e);
        if (old != null) {
            ((Entry<Object>)old).outer = null;
            if (((Entry<Object>)old).inner == null) {
                this.byElement.remove(((Entry<Object>)old).element);
            }
        }
    }
    
    public void addUsed(final XmlNode n) {
        this.usedNodes.add(n);
    }
    
    public Entry<XmlNode> byElement(final Object e) {
        return this.byElement.get(e);
    }
    
    public Entry<XmlNode> byPeer(final Object o) {
        return this.byPeer.get(o);
    }
    
    public Object getInnerPeer(final XmlNode element) {
        final Entry e = this.byElement(element);
        if (e == null) {
            return null;
        }
        return e.inner;
    }
    
    public Object getOuterPeer(final XmlNode element) {
        final Entry e = this.byElement(element);
        if (e == null) {
            return null;
        }
        return e.outer;
    }
    
    static final class Entry<XmlNode>
    {
        private XmlNode element;
        private Object inner;
        private Object outer;
        
        public XmlNode element() {
            return this.element;
        }
        
        public Object inner() {
            return this.inner;
        }
        
        public Object outer() {
            return this.outer;
        }
    }
}
