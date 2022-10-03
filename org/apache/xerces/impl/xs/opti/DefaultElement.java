package org.apache.xerces.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class DefaultElement extends NodeImpl implements Element
{
    public DefaultElement() {
    }
    
    public DefaultElement(final String s, final String s2, final String s3, final String s4, final short n) {
        super(s, s2, s3, s4, n);
    }
    
    public String getTagName() {
        return null;
    }
    
    public String getAttribute(final String s) {
        return null;
    }
    
    public Attr getAttributeNode(final String s) {
        return null;
    }
    
    public NodeList getElementsByTagName(final String s) {
        return null;
    }
    
    public String getAttributeNS(final String s, final String s2) {
        return null;
    }
    
    public Attr getAttributeNodeNS(final String s, final String s2) {
        return null;
    }
    
    public NodeList getElementsByTagNameNS(final String s, final String s2) {
        return null;
    }
    
    public boolean hasAttribute(final String s) {
        return false;
    }
    
    public boolean hasAttributeNS(final String s, final String s2) {
        return false;
    }
    
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }
    
    public void setAttribute(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void removeAttribute(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Attr removeAttributeNode(final Attr attr) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Attr setAttributeNode(final Attr attr) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void setAttributeNS(final String s, final String s2, final String s3) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void removeAttributeNS(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Attr setAttributeNodeNS(final Attr attr) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void setIdAttributeNode(final Attr attr, final boolean b) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void setIdAttribute(final String s, final boolean b) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void setIdAttributeNS(final String s, final String s2, final boolean b) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
}
