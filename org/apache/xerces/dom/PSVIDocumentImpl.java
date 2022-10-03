package org.apache.xerces.dom;

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
    
    public PSVIDocumentImpl(final DocumentType documentType) {
        super(documentType);
    }
    
    public Node cloneNode(final boolean b) {
        final PSVIDocumentImpl psviDocumentImpl = new PSVIDocumentImpl();
        this.callUserDataHandlers(this, psviDocumentImpl, (short)1);
        this.cloneNode(psviDocumentImpl, b);
        psviDocumentImpl.mutationEvents = this.mutationEvents;
        return psviDocumentImpl;
    }
    
    public DOMImplementation getImplementation() {
        return PSVIDOMImplementationImpl.getDOMImplementation();
    }
    
    public Element createElementNS(final String s, final String s2) throws DOMException {
        return new PSVIElementNSImpl(this, s, s2);
    }
    
    public Element createElementNS(final String s, final String s2, final String s3) throws DOMException {
        return new PSVIElementNSImpl(this, s, s2, s3);
    }
    
    public Attr createAttributeNS(final String s, final String s2) throws DOMException {
        return new PSVIAttrNSImpl(this, s, s2);
    }
    
    public Attr createAttributeNS(final String s, final String s2, final String s3) throws DOMException {
        return new PSVIAttrNSImpl(this, s, s2, s3);
    }
    
    public DOMConfiguration getDomConfig() {
        super.getDomConfig();
        return this.fConfiguration;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        throw new NotSerializableException(this.getClass().getName());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        throw new NotSerializableException(this.getClass().getName());
    }
}
