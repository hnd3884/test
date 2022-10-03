package org.w3c.tidy;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.NodeList;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Attr;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Document;

public class DOMDocumentImpl extends DOMNodeImpl implements Document
{
    private TagTable tt;
    
    protected DOMDocumentImpl(final org.w3c.tidy.Node node) {
        super(node);
        this.tt = new TagTable();
    }
    
    public String getNodeName() {
        return "#document";
    }
    
    public short getNodeType() {
        return 9;
    }
    
    public DocumentType getDoctype() {
        org.w3c.tidy.Node node;
        for (node = this.adaptee.content; node != null && node.type != 1; node = node.next) {}
        if (node != null) {
            return (DocumentType)node.getAdapter();
        }
        return null;
    }
    
    public DOMImplementation getImplementation() {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public Element getDocumentElement() {
        org.w3c.tidy.Node node;
        for (node = this.adaptee.content; node != null && node.type != 5 && node.type != 7; node = node.next) {}
        if (node != null) {
            return (Element)node.getAdapter();
        }
        return null;
    }
    
    public Element createElement(final String s) throws DOMException {
        final org.w3c.tidy.Node node = new org.w3c.tidy.Node((short)7, null, 0, 0, s, this.tt);
        if (node != null) {
            if (node.tag == null) {
                node.tag = TagTable.XML_TAGS;
            }
            return (Element)node.getAdapter();
        }
        return null;
    }
    
    public DocumentFragment createDocumentFragment() {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public Text createTextNode(final String s) {
        final byte[] bytes = TidyUtils.getBytes(s);
        final org.w3c.tidy.Node node = new org.w3c.tidy.Node((short)4, bytes, 0, bytes.length);
        if (node != null) {
            return (Text)node.getAdapter();
        }
        return null;
    }
    
    public Comment createComment(final String s) {
        final byte[] bytes = TidyUtils.getBytes(s);
        final org.w3c.tidy.Node node = new org.w3c.tidy.Node((short)2, bytes, 0, bytes.length);
        if (node != null) {
            return (Comment)node.getAdapter();
        }
        return null;
    }
    
    public CDATASection createCDATASection(final String s) throws DOMException {
        throw new DOMException((short)9, "HTML document");
    }
    
    public ProcessingInstruction createProcessingInstruction(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "HTML document");
    }
    
    public Attr createAttribute(final String s) throws DOMException {
        final AttVal attVal = new AttVal(null, null, 34, s, null);
        if (attVal != null) {
            attVal.dict = AttributeTable.getDefaultAttributeTable().findAttribute(attVal);
            return attVal.getAdapter();
        }
        return null;
    }
    
    public EntityReference createEntityReference(final String s) throws DOMException {
        throw new DOMException((short)9, "createEntityReference not supported");
    }
    
    public NodeList getElementsByTagName(final String s) {
        return new DOMNodeListByTagNameImpl(this.adaptee, s);
    }
    
    public Node importNode(final Node node, final boolean b) throws DOMException {
        throw new DOMException((short)9, "importNode not supported");
    }
    
    public Attr createAttributeNS(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "createAttributeNS not supported");
    }
    
    public Element createElementNS(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "createElementNS not supported");
    }
    
    public NodeList getElementsByTagNameNS(final String s, final String s2) {
        throw new DOMException((short)9, "getElementsByTagNameNS not supported");
    }
    
    public Element getElementById(final String s) {
        return null;
    }
    
    public Node adoptNode(final Node node) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public String getDocumentURI() {
        return null;
    }
    
    public DOMConfiguration getDomConfig() {
        return null;
    }
    
    public String getInputEncoding() {
        return null;
    }
    
    public boolean getStrictErrorChecking() {
        return true;
    }
    
    public String getXmlEncoding() {
        return null;
    }
    
    public boolean getXmlStandalone() {
        return false;
    }
    
    public String getXmlVersion() {
        return "1.0";
    }
    
    public void normalizeDocument() {
    }
    
    public Node renameNode(final Node node, final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public void setDocumentURI(final String s) {
    }
    
    public void setStrictErrorChecking(final boolean b) {
    }
    
    public void setXmlStandalone(final boolean b) throws DOMException {
    }
    
    public void setXmlVersion(final String s) throws DOMException {
    }
}
