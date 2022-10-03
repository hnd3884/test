package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.ArrayList;
import javax.xml.stream.events.Namespace;
import java.util.Iterator;
import javax.xml.namespace.QName;
import java.util.List;
import javax.xml.stream.events.EndElement;

public class EndElementEvent extends EventBase implements EndElement
{
    List _namespaces;
    QName _qname;
    
    public void reset() {
        if (this._namespaces != null) {
            this._namespaces.clear();
        }
    }
    
    public EndElementEvent() {
        this._namespaces = null;
        this.setEventType(2);
    }
    
    public EndElementEvent(final String prefix, final String namespaceURI, final String localpart) {
        this._namespaces = null;
        this._qname = this.getQName(namespaceURI, localpart, prefix);
        this.setEventType(2);
    }
    
    public EndElementEvent(final QName qname) {
        this._namespaces = null;
        this._qname = qname;
        this.setEventType(2);
    }
    
    @Override
    public QName getName() {
        return this._qname;
    }
    
    public void setName(final QName qname) {
        this._qname = qname;
    }
    
    @Override
    public Iterator getNamespaces() {
        if (this._namespaces != null) {
            return this._namespaces.iterator();
        }
        return EmptyIterator.getInstance();
    }
    
    public void addNamespace(final Namespace namespace) {
        if (this._namespaces == null) {
            this._namespaces = new ArrayList();
        }
        this._namespaces.add(namespace);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("</").append(this.nameAsString());
        final Iterator namespaces = this.getNamespaces();
        while (namespaces.hasNext()) {
            sb.append(" ").append(namespaces.next().toString());
        }
        sb.append(">");
        return sb.toString();
    }
    
    private String nameAsString() {
        if ("".equals(this._qname.getNamespaceURI())) {
            return this._qname.getLocalPart();
        }
        if (this._qname.getPrefix() != null) {
            return "['" + this._qname.getNamespaceURI() + "']:" + this._qname.getPrefix() + ":" + this._qname.getLocalPart();
        }
        return "['" + this._qname.getNamespaceURI() + "']:" + this._qname.getLocalPart();
    }
    
    private QName getQName(final String uri, final String localPart, final String prefix) {
        QName qn = null;
        if (prefix != null && uri != null) {
            qn = new QName(uri, localPart, prefix);
        }
        else if (prefix == null && uri != null) {
            qn = new QName(uri, localPart);
        }
        else if (prefix == null && uri == null) {
            qn = new QName(localPart);
        }
        return qn;
    }
}
