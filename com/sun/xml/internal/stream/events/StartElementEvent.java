package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.Attribute;
import java.util.Collection;
import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.List;
import java.util.Map;
import javax.xml.stream.events.StartElement;

public class StartElementEvent extends DummyEvent implements StartElement
{
    private Map fAttributes;
    private List fNamespaces;
    private NamespaceContext fNamespaceContext;
    private QName fQName;
    
    public StartElementEvent(final String prefix, final String uri, final String localpart) {
        this(new QName(uri, localpart, prefix));
    }
    
    public StartElementEvent(final QName qname) {
        this.fNamespaceContext = null;
        this.fQName = qname;
        this.init();
    }
    
    public StartElementEvent(final StartElement startelement) {
        this(startelement.getName());
        this.addAttributes(startelement.getAttributes());
        this.addNamespaceAttributes(startelement.getNamespaces());
    }
    
    protected void init() {
        this.setEventType(1);
        this.fAttributes = new HashMap();
        this.fNamespaces = new ArrayList();
    }
    
    @Override
    public QName getName() {
        return this.fQName;
    }
    
    public void setName(final QName qname) {
        this.fQName = qname;
    }
    
    @Override
    public Iterator getAttributes() {
        if (this.fAttributes != null) {
            final Collection coll = this.fAttributes.values();
            return new ReadOnlyIterator(coll.iterator());
        }
        return new ReadOnlyIterator();
    }
    
    @Override
    public Iterator getNamespaces() {
        if (this.fNamespaces != null) {
            return new ReadOnlyIterator(this.fNamespaces.iterator());
        }
        return new ReadOnlyIterator();
    }
    
    @Override
    public Attribute getAttributeByName(final QName qname) {
        if (qname == null) {
            return null;
        }
        return this.fAttributes.get(qname);
    }
    
    public String getNamespace() {
        return this.fQName.getNamespaceURI();
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if (this.getNamespace() != null && this.fQName.getPrefix().equals(prefix)) {
            return this.getNamespace();
        }
        if (this.fNamespaceContext != null) {
            return this.fNamespaceContext.getNamespaceURI(prefix);
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuffer startElement = new StringBuffer();
        startElement.append("<");
        startElement.append(this.nameAsString());
        if (this.fAttributes != null) {
            final Iterator it = this.getAttributes();
            Attribute attr = null;
            while (it.hasNext()) {
                attr = it.next();
                startElement.append(" ");
                startElement.append(attr.toString());
            }
        }
        if (this.fNamespaces != null) {
            final Iterator it = this.fNamespaces.iterator();
            Namespace attr2 = null;
            while (it.hasNext()) {
                attr2 = it.next();
                startElement.append(" ");
                startElement.append(attr2.toString());
            }
        }
        startElement.append(">");
        return startElement.toString();
    }
    
    public String nameAsString() {
        if ("".equals(this.fQName.getNamespaceURI())) {
            return this.fQName.getLocalPart();
        }
        if (this.fQName.getPrefix() != null) {
            return "['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getPrefix() + ":" + this.fQName.getLocalPart();
        }
        return "['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getLocalPart();
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.fNamespaceContext;
    }
    
    public void setNamespaceContext(final NamespaceContext nc) {
        this.fNamespaceContext = nc;
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        writer.write(this.toString());
    }
    
    void addAttribute(final Attribute attr) {
        if (attr.isNamespace()) {
            this.fNamespaces.add(attr);
        }
        else {
            this.fAttributes.put(attr.getName(), attr);
        }
    }
    
    void addAttributes(final Iterator attrs) {
        if (attrs == null) {
            return;
        }
        while (attrs.hasNext()) {
            final Attribute attr = attrs.next();
            this.fAttributes.put(attr.getName(), attr);
        }
    }
    
    void addNamespaceAttribute(final Namespace attr) {
        if (attr == null) {
            return;
        }
        this.fNamespaces.add(attr);
    }
    
    void addNamespaceAttributes(final Iterator attrs) {
        if (attrs == null) {
            return;
        }
        while (attrs.hasNext()) {
            final Namespace attr = attrs.next();
            this.fNamespaces.add(attr);
        }
    }
}
