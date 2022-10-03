package com.sun.org.apache.xerces.internal.dom;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;

public class PSVIDocumentImpl extends DocumentImpl
{
    static final long serialVersionUID = -8822220250676434522L;
    
    public PSVIDocumentImpl() {
    }
    
    public PSVIDocumentImpl(final DocumentType doctype) {
        super(doctype);
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        final PSVIDocumentImpl newdoc = new PSVIDocumentImpl();
        this.callUserDataHandlers(this, newdoc, (short)1);
        this.cloneNode(newdoc, deep);
        newdoc.mutationEvents = this.mutationEvents;
        return newdoc;
    }
    
    @Override
    public DOMImplementation getImplementation() {
        return PSVIDOMImplementationImpl.getDOMImplementation();
    }
    
    @Override
    public Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        return new PSVIElementNSImpl(this, namespaceURI, qualifiedName);
    }
    
    @Override
    public Element createElementNS(final String namespaceURI, final String qualifiedName, final String localpart) throws DOMException {
        return new PSVIElementNSImpl(this, namespaceURI, qualifiedName, localpart);
    }
    
    @Override
    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        return new PSVIAttrNSImpl(this, namespaceURI, qualifiedName);
    }
    
    @Override
    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName, final String localName) throws DOMException {
        return new PSVIAttrNSImpl(this, namespaceURI, qualifiedName, localName);
    }
    
    @Override
    public DOMConfiguration getDomConfig() {
        super.getDomConfig();
        return this.fConfiguration;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        throw new NotSerializableException(this.getClass().getName());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        throw new NotSerializableException(this.getClass().getName());
    }
}
