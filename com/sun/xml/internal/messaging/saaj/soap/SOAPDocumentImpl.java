package com.sun.xml.internal.messaging.saaj.soap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.EntityReference;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.ProcessingInstruction;
import com.sun.xml.internal.messaging.saaj.soap.impl.CDATAImpl;
import org.w3c.dom.CDATASection;
import com.sun.xml.internal.messaging.saaj.soap.impl.CommentImpl;
import org.w3c.dom.Comment;
import com.sun.xml.internal.messaging.saaj.soap.impl.TextImpl;
import org.w3c.dom.Text;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DOMException;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementFactory;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import java.util.logging.Logger;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

public class SOAPDocumentImpl extends DocumentImpl implements SOAPDocument
{
    private static final String XMLNS;
    protected static final Logger log;
    SOAPPartImpl enclosingSOAPPart;
    
    public SOAPDocumentImpl(final SOAPPartImpl enclosingDocument) {
        this.enclosingSOAPPart = enclosingDocument;
    }
    
    @Override
    public SOAPPartImpl getSOAPPart() {
        if (this.enclosingSOAPPart == null) {
            SOAPDocumentImpl.log.severe("SAAJ0541.soap.fragment.not.bound.to.part");
            throw new RuntimeException("Could not complete operation. Fragment not bound to SOAP part.");
        }
        return this.enclosingSOAPPart;
    }
    
    @Override
    public SOAPDocumentImpl getDocument() {
        return this;
    }
    
    @Override
    public DocumentType getDoctype() {
        return null;
    }
    
    @Override
    public DOMImplementation getImplementation() {
        return super.getImplementation();
    }
    
    @Override
    public Element getDocumentElement() {
        this.getSOAPPart().doGetDocumentElement();
        return this.doGetDocumentElement();
    }
    
    protected Element doGetDocumentElement() {
        return super.getDocumentElement();
    }
    
    @Override
    public Element createElement(final String tagName) throws DOMException {
        return ElementFactory.createElement(this, NameImpl.getLocalNameFromTagName(tagName), NameImpl.getPrefixFromTagName(tagName), null);
    }
    
    @Override
    public DocumentFragment createDocumentFragment() {
        return new SOAPDocumentFragment(this);
    }
    
    @Override
    public Text createTextNode(final String data) {
        return new TextImpl(this, data);
    }
    
    @Override
    public Comment createComment(final String data) {
        return new CommentImpl(this, data);
    }
    
    @Override
    public CDATASection createCDATASection(final String data) throws DOMException {
        return new CDATAImpl(this, data);
    }
    
    @Override
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
        SOAPDocumentImpl.log.severe("SAAJ0542.soap.proc.instructions.not.allowed.in.docs");
        throw new UnsupportedOperationException("Processing Instructions are not allowed in SOAP documents");
    }
    
    @Override
    public Attr createAttribute(final String name) throws DOMException {
        final boolean isQualifiedName = name.indexOf(":") > 0;
        if (isQualifiedName) {
            String nsUri = null;
            final String prefix = name.substring(0, name.indexOf(":"));
            if (SOAPDocumentImpl.XMLNS.equals(prefix)) {
                nsUri = ElementImpl.XMLNS_URI;
                return this.createAttributeNS(nsUri, name);
            }
        }
        return super.createAttribute(name);
    }
    
    @Override
    public EntityReference createEntityReference(final String name) throws DOMException {
        SOAPDocumentImpl.log.severe("SAAJ0543.soap.entity.refs.not.allowed.in.docs");
        throw new UnsupportedOperationException("Entity References are not allowed in SOAP documents");
    }
    
    @Override
    public NodeList getElementsByTagName(final String tagname) {
        return super.getElementsByTagName(tagname);
    }
    
    @Override
    public Node importNode(final Node importedNode, final boolean deep) throws DOMException {
        return super.importNode(importedNode, deep);
    }
    
    @Override
    public Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        return ElementFactory.createElement(this, NameImpl.getLocalNameFromTagName(qualifiedName), NameImpl.getPrefixFromTagName(qualifiedName), namespaceURI);
    }
    
    @Override
    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        return super.createAttributeNS(namespaceURI, qualifiedName);
    }
    
    @Override
    public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
        return super.getElementsByTagNameNS(namespaceURI, localName);
    }
    
    @Override
    public Element getElementById(final String elementId) {
        return super.getElementById(elementId);
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        final SOAPPartImpl newSoapPart = this.getSOAPPart().doCloneNode();
        super.cloneNode(newSoapPart.getDocument(), deep);
        return newSoapPart;
    }
    
    public void cloneNode(final SOAPDocumentImpl newdoc, final boolean deep) {
        super.cloneNode(newdoc, deep);
    }
    
    static {
        XMLNS = "xmlns".intern();
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
    }
}
