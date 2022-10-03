package org.apache.xerces.impl.xs.opti;

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
        this.nodeType = 9;
    }
    
    public String getNodeName() {
        return "#document";
    }
    
    public DocumentType getDoctype() {
        return null;
    }
    
    public DOMImplementation getImplementation() {
        return null;
    }
    
    public Element getDocumentElement() {
        return null;
    }
    
    public NodeList getElementsByTagName(final String s) {
        return null;
    }
    
    public NodeList getElementsByTagNameNS(final String s, final String s2) {
        return null;
    }
    
    public Element getElementById(final String s) {
        return null;
    }
    
    public Node importNode(final Node node, final boolean b) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Element createElement(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public DocumentFragment createDocumentFragment() {
        return null;
    }
    
    public Text createTextNode(final String s) {
        return null;
    }
    
    public Comment createComment(final String s) {
        return null;
    }
    
    public CDATASection createCDATASection(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public ProcessingInstruction createProcessingInstruction(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Attr createAttribute(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public EntityReference createEntityReference(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Element createElementNS(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Attr createAttributeNS(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public String getInputEncoding() {
        return null;
    }
    
    public String getXmlEncoding() {
        return null;
    }
    
    public boolean getXmlStandalone() {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void setXmlStandalone(final boolean b) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public String getXmlVersion() {
        return null;
    }
    
    public void setXmlVersion(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public boolean getStrictErrorChecking() {
        return false;
    }
    
    public void setStrictErrorChecking(final boolean b) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public String getDocumentURI() {
        return this.fDocumentURI;
    }
    
    public void setDocumentURI(final String fDocumentURI) {
        this.fDocumentURI = fDocumentURI;
    }
    
    public Node adoptNode(final Node node) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void normalizeDocument() {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public DOMConfiguration getDomConfig() {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Node renameNode(final Node node, final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
}
