package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.UserDataHandler;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class DefaultNode implements Node
{
    @Override
    public String getNodeName() {
        return null;
    }
    
    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }
    
    @Override
    public short getNodeType() {
        return -1;
    }
    
    @Override
    public Node getParentNode() {
        return null;
    }
    
    @Override
    public NodeList getChildNodes() {
        return null;
    }
    
    @Override
    public Node getFirstChild() {
        return null;
    }
    
    @Override
    public Node getLastChild() {
        return null;
    }
    
    @Override
    public Node getPreviousSibling() {
        return null;
    }
    
    @Override
    public Node getNextSibling() {
        return null;
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }
    
    @Override
    public Document getOwnerDocument() {
        return null;
    }
    
    @Override
    public boolean hasChildNodes() {
        return false;
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        return null;
    }
    
    @Override
    public void normalize() {
    }
    
    @Override
    public boolean isSupported(final String feature, final String version) {
        return false;
    }
    
    @Override
    public String getNamespaceURI() {
        return null;
    }
    
    @Override
    public String getPrefix() {
        return null;
    }
    
    @Override
    public String getLocalName() {
        return null;
    }
    
    @Override
    public String getBaseURI() {
        return null;
    }
    
    @Override
    public boolean hasAttributes() {
        return false;
    }
    
    @Override
    public void setNodeValue(final String nodeValue) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Node removeChild(final Node oldChild) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Node appendChild(final Node newChild) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void setPrefix(final String prefix) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public short compareDocumentPosition(final Node other) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String getTextContent() throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void setTextContent(final String textContent) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public boolean isSameNode(final Node other) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String lookupPrefix(final String namespaceURI) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public boolean isDefaultNamespace(final String namespaceURI) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String lookupNamespaceURI(final String prefix) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public boolean isEqualNode(final Node arg) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Object getFeature(final String feature, final String version) {
        return null;
    }
    
    @Override
    public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Object getUserData(final String key) {
        return null;
    }
}
