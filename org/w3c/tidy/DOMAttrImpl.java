package org.w3c.tidy;

import org.w3c.dom.TypeInfo;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Attr;

public class DOMAttrImpl extends DOMNodeImpl implements Attr, Cloneable
{
    protected AttVal avAdaptee;
    
    protected DOMAttrImpl(final AttVal avAdaptee) {
        super(null);
        this.avAdaptee = avAdaptee;
    }
    
    public String getNodeValue() throws DOMException {
        return this.getValue();
    }
    
    public void setNodeValue(final String value) throws DOMException {
        this.setValue(value);
    }
    
    public String getNodeName() {
        return this.getName();
    }
    
    public short getNodeType() {
        return 2;
    }
    
    public String getName() {
        return this.avAdaptee.attribute;
    }
    
    public boolean getSpecified() {
        return this.avAdaptee.value != null;
    }
    
    public String getValue() {
        return (this.avAdaptee.value == null) ? this.avAdaptee.attribute : this.avAdaptee.value;
    }
    
    public void setValue(final String value) {
        this.avAdaptee.value = value;
    }
    
    public Node getParentNode() {
        return null;
    }
    
    public NodeList getChildNodes() {
        return new DOMNodeListImpl(null);
    }
    
    public Node getFirstChild() {
        return null;
    }
    
    public Node getLastChild() {
        return null;
    }
    
    public Node getPreviousSibling() {
        return null;
    }
    
    public Node getNextSibling() {
        return null;
    }
    
    public NamedNodeMap getAttributes() {
        return null;
    }
    
    public Document getOwnerDocument() {
        return null;
    }
    
    public Node insertBefore(final Node node, final Node node2) throws DOMException {
        throw new DOMException((short)7, "Not supported");
    }
    
    public Node replaceChild(final Node node, final Node node2) throws DOMException {
        throw new DOMException((short)7, "Not supported");
    }
    
    public Node removeChild(final Node node) throws DOMException {
        throw new DOMException((short)7, "Not supported");
    }
    
    public Node appendChild(final Node node) throws DOMException {
        throw new DOMException((short)7, "Not supported");
    }
    
    public boolean hasChildNodes() {
        return false;
    }
    
    public Node cloneNode(final boolean b) {
        return (Node)this.clone();
    }
    
    public Element getOwnerElement() {
        return null;
    }
    
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }
    
    public boolean isId() {
        return "id".equals(this.avAdaptee.getAttribute());
    }
    
    protected Object clone() {
        DOMAttrImpl domAttrImpl;
        try {
            domAttrImpl = (DOMAttrImpl)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new RuntimeException("Clone not supported");
        }
        domAttrImpl.avAdaptee = (AttVal)this.avAdaptee.clone();
        return domAttrImpl;
    }
}
