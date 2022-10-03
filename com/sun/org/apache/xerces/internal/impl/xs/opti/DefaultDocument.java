package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Attr;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Document;

public class DefaultDocument extends NodeImpl implements Document
{
    private String fDocumentURI;
    
    public DefaultDocument() {
        this.fDocumentURI = null;
    }
    
    @Override
    public DocumentType getDoctype() {
        return null;
    }
    
    @Override
    public DOMImplementation getImplementation() {
        return null;
    }
    
    @Override
    public Element getDocumentElement() {
        return null;
    }
    
    @Override
    public NodeList getElementsByTagName(final String tagname) {
        return null;
    }
    
    @Override
    public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
        return null;
    }
    
    @Override
    public Element getElementById(final String elementId) {
        return null;
    }
    
    @Override
    public Node importNode(final Node importedNode, final boolean deep) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Element createElement(final String tagName) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public DocumentFragment createDocumentFragment() {
        return null;
    }
    
    @Override
    public Text createTextNode(final String data) {
        return null;
    }
    
    @Override
    public Comment createComment(final String data) {
        return null;
    }
    
    @Override
    public CDATASection createCDATASection(final String data) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Attr createAttribute(final String name) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public EntityReference createEntityReference(final String name) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String getInputEncoding() {
        return null;
    }
    
    @Override
    public String getXmlEncoding() {
        return null;
    }
    
    @Override
    public boolean getXmlStandalone() {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void setXmlStandalone(final boolean standalone) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String getXmlVersion() {
        return null;
    }
    
    @Override
    public void setXmlVersion(final String version) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public boolean getStrictErrorChecking() {
        return false;
    }
    
    @Override
    public void setStrictErrorChecking(final boolean strictErrorChecking) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String getDocumentURI() {
        return this.fDocumentURI;
    }
    
    @Override
    public void setDocumentURI(final String documentURI) {
        this.fDocumentURI = documentURI;
    }
    
    @Override
    public Node adoptNode(final Node source) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void normalizeDocument() {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public DOMConfiguration getDomConfig() {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Node renameNode(final Node n, final String namespaceURI, final String name) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
}
