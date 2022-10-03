package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class DefaultElement extends NodeImpl implements Element
{
    public DefaultElement() {
    }
    
    public DefaultElement(final String prefix, final String localpart, final String rawname, final String uri, final short nodeType) {
        super(prefix, localpart, rawname, uri, nodeType);
    }
    
    @Override
    public String getTagName() {
        return null;
    }
    
    @Override
    public String getAttribute(final String name) {
        return null;
    }
    
    @Override
    public Attr getAttributeNode(final String name) {
        return null;
    }
    
    @Override
    public NodeList getElementsByTagName(final String name) {
        return null;
    }
    
    @Override
    public String getAttributeNS(final String namespaceURI, final String localName) {
        return null;
    }
    
    @Override
    public Attr getAttributeNodeNS(final String namespaceURI, final String localName) {
        return null;
    }
    
    @Override
    public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
        return null;
    }
    
    @Override
    public boolean hasAttribute(final String name) {
        return false;
    }
    
    @Override
    public boolean hasAttributeNS(final String namespaceURI, final String localName) {
        return false;
    }
    
    @Override
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }
    
    @Override
    public void setAttribute(final String name, final String value) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void removeAttribute(final String name) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Attr removeAttributeNode(final Attr oldAttr) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Attr setAttributeNode(final Attr newAttr) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void removeAttributeNS(final String namespaceURI, final String localName) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Attr setAttributeNodeNS(final Attr newAttr) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void setIdAttributeNode(final Attr at, final boolean makeId) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void setIdAttribute(final String name, final boolean makeId) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void setIdAttributeNS(final String namespaceURI, final String localName, final boolean makeId) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
}
