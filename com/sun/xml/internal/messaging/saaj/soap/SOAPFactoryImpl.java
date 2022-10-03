package com.sun.xml.internal.messaging.saaj.soap;

import javax.xml.soap.SOAPFault;
import javax.xml.soap.Detail;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementFactory;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.soap.SOAPException;
import java.util.logging.Level;
import javax.xml.soap.SOAPElement;
import java.util.logging.Logger;
import javax.xml.soap.SOAPFactory;

public abstract class SOAPFactoryImpl extends SOAPFactory
{
    protected static final Logger log;
    
    protected abstract SOAPDocumentImpl createDocument();
    
    @Override
    public SOAPElement createElement(final String tagName) throws SOAPException {
        if (tagName == null) {
            SOAPFactoryImpl.log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "tagName", "SOAPFactory.createElement" });
            throw new SOAPException("Null tagName argument passed to createElement");
        }
        return ElementFactory.createElement(this.createDocument(), NameImpl.createFromTagName(tagName));
    }
    
    @Override
    public SOAPElement createElement(final Name name) throws SOAPException {
        if (name == null) {
            SOAPFactoryImpl.log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "name", "SOAPFactory.createElement" });
            throw new SOAPException("Null name argument passed to createElement");
        }
        return ElementFactory.createElement(this.createDocument(), name);
    }
    
    @Override
    public SOAPElement createElement(final QName qname) throws SOAPException {
        if (qname == null) {
            SOAPFactoryImpl.log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "qname", "SOAPFactory.createElement" });
            throw new SOAPException("Null qname argument passed to createElement");
        }
        return ElementFactory.createElement(this.createDocument(), qname);
    }
    
    @Override
    public SOAPElement createElement(final String localName, final String prefix, final String uri) throws SOAPException {
        if (localName == null) {
            SOAPFactoryImpl.log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "localName", "SOAPFactory.createElement" });
            throw new SOAPException("Null localName argument passed to createElement");
        }
        return ElementFactory.createElement(this.createDocument(), localName, prefix, uri);
    }
    
    @Override
    public Name createName(final String localName, final String prefix, final String uri) throws SOAPException {
        if (localName == null) {
            SOAPFactoryImpl.log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "localName", "SOAPFactory.createName" });
            throw new SOAPException("Null localName argument passed to createName");
        }
        return NameImpl.create(localName, prefix, uri);
    }
    
    @Override
    public Name createName(final String localName) throws SOAPException {
        if (localName == null) {
            SOAPFactoryImpl.log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "localName", "SOAPFactory.createName" });
            throw new SOAPException("Null localName argument passed to createName");
        }
        return NameImpl.createFromUnqualifiedName(localName);
    }
    
    @Override
    public SOAPElement createElement(final Element domElement) throws SOAPException {
        if (domElement == null) {
            return null;
        }
        return this.convertToSoapElement(domElement);
    }
    
    private SOAPElement convertToSoapElement(final Element element) throws SOAPException {
        if (element instanceof SOAPElement) {
            return (SOAPElement)element;
        }
        final SOAPElement copy = this.createElement(element.getLocalName(), element.getPrefix(), element.getNamespaceURI());
        final Document ownerDoc = copy.getOwnerDocument();
        final NamedNodeMap attrMap = element.getAttributes();
        for (int i = 0; i < attrMap.getLength(); ++i) {
            final Attr nextAttr = (Attr)attrMap.item(i);
            final Attr importedAttr = (Attr)ownerDoc.importNode(nextAttr, true);
            copy.setAttributeNodeNS(importedAttr);
        }
        final NodeList nl = element.getChildNodes();
        for (int j = 0; j < nl.getLength(); ++j) {
            final Node next = nl.item(j);
            final Node imported = ownerDoc.importNode(next, true);
            copy.appendChild(imported);
        }
        return copy;
    }
    
    @Override
    public Detail createDetail() throws SOAPException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SOAPFault createFault(final String reasonText, final QName faultCode) throws SOAPException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SOAPFault createFault() throws SOAPException {
        throw new UnsupportedOperationException();
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
    }
}
