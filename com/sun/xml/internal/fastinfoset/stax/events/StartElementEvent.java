package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.Attribute;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.List;
import java.util.Map;
import javax.xml.stream.events.StartElement;

public class StartElementEvent extends EventBase implements StartElement
{
    private Map _attributes;
    private List _namespaces;
    private NamespaceContext _context;
    private QName _qname;
    
    public void reset() {
        if (this._attributes != null) {
            this._attributes.clear();
        }
        if (this._namespaces != null) {
            this._namespaces.clear();
        }
        if (this._context != null) {
            this._context = null;
        }
    }
    
    public StartElementEvent() {
        this._context = null;
        this.init();
    }
    
    public StartElementEvent(String prefix, String uri, final String localpart) {
        this._context = null;
        this.init();
        if (uri == null) {
            uri = "";
        }
        if (prefix == null) {
            prefix = "";
        }
        this._qname = new QName(uri, localpart, prefix);
        this.setEventType(1);
    }
    
    public StartElementEvent(final QName qname) {
        this._context = null;
        this.init();
        this._qname = qname;
    }
    
    public StartElementEvent(final StartElement startelement) {
        this(startelement.getName());
        this.addAttributes(startelement.getAttributes());
        this.addNamespaces(startelement.getNamespaces());
    }
    
    protected void init() {
        this.setEventType(1);
        this._attributes = new HashMap();
        this._namespaces = new ArrayList();
    }
    
    @Override
    public QName getName() {
        return this._qname;
    }
    
    @Override
    public Iterator getAttributes() {
        if (this._attributes != null) {
            final Collection coll = this._attributes.values();
            return new ReadIterator(coll.iterator());
        }
        return EmptyIterator.getInstance();
    }
    
    @Override
    public Iterator getNamespaces() {
        if (this._namespaces != null) {
            return new ReadIterator(this._namespaces.iterator());
        }
        return EmptyIterator.getInstance();
    }
    
    @Override
    public Attribute getAttributeByName(final QName qname) {
        if (qname == null) {
            return null;
        }
        return this._attributes.get(qname);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this._context;
    }
    
    public void setName(final QName qname) {
        this._qname = qname;
    }
    
    public String getNamespace() {
        return this._qname.getNamespaceURI();
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if (this.getNamespace() != null) {
            return this.getNamespace();
        }
        if (this._context != null) {
            return this._context.getNamespaceURI(prefix);
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append('<').append(this.nameAsString());
        if (this._attributes != null) {
            final Iterator it = this.getAttributes();
            Attribute attr = null;
            while (it.hasNext()) {
                attr = it.next();
                sb.append(' ').append(attr.toString());
            }
        }
        if (this._namespaces != null) {
            final Iterator it = this._namespaces.iterator();
            Namespace attr2 = null;
            while (it.hasNext()) {
                attr2 = it.next();
                sb.append(' ').append(attr2.toString());
            }
        }
        sb.append('>');
        return sb.toString();
    }
    
    public String nameAsString() {
        if ("".equals(this._qname.getNamespaceURI())) {
            return this._qname.getLocalPart();
        }
        if (this._qname.getPrefix() != null) {
            return "['" + this._qname.getNamespaceURI() + "']:" + this._qname.getPrefix() + ":" + this._qname.getLocalPart();
        }
        return "['" + this._qname.getNamespaceURI() + "']:" + this._qname.getLocalPart();
    }
    
    public void setNamespaceContext(final NamespaceContext context) {
        this._context = context;
    }
    
    public void addAttribute(final Attribute attr) {
        this._attributes.put(attr.getName(), attr);
    }
    
    public void addAttributes(final Iterator attrs) {
        if (attrs != null) {
            while (attrs.hasNext()) {
                final Attribute attr = attrs.next();
                this._attributes.put(attr.getName(), attr);
            }
        }
    }
    
    public void addNamespace(final Namespace namespace) {
        if (namespace != null) {
            this._namespaces.add(namespace);
        }
    }
    
    public void addNamespaces(final Iterator namespaces) {
        if (namespaces != null) {
            while (namespaces.hasNext()) {
                final Namespace namespace = namespaces.next();
                this._namespaces.add(namespace);
            }
        }
    }
}
