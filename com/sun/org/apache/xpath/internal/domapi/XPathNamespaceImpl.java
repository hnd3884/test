package com.sun.org.apache.xpath.internal.domapi;

import org.w3c.dom.UserDataHandler;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathNamespace;

class XPathNamespaceImpl implements XPathNamespace
{
    private final Node m_attributeNode;
    private String textContent;
    
    XPathNamespaceImpl(final Node node) {
        this.m_attributeNode = node;
    }
    
    @Override
    public Element getOwnerElement() {
        return ((Attr)this.m_attributeNode).getOwnerElement();
    }
    
    @Override
    public String getNodeName() {
        return "#namespace";
    }
    
    @Override
    public String getNodeValue() throws DOMException {
        return this.m_attributeNode.getNodeValue();
    }
    
    @Override
    public void setNodeValue(final String arg0) throws DOMException {
    }
    
    @Override
    public short getNodeType() {
        return 13;
    }
    
    @Override
    public Node getParentNode() {
        return this.m_attributeNode.getParentNode();
    }
    
    @Override
    public NodeList getChildNodes() {
        return this.m_attributeNode.getChildNodes();
    }
    
    @Override
    public Node getFirstChild() {
        return this.m_attributeNode.getFirstChild();
    }
    
    @Override
    public Node getLastChild() {
        return this.m_attributeNode.getLastChild();
    }
    
    @Override
    public Node getPreviousSibling() {
        return this.m_attributeNode.getPreviousSibling();
    }
    
    @Override
    public Node getNextSibling() {
        return this.m_attributeNode.getNextSibling();
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        return this.m_attributeNode.getAttributes();
    }
    
    @Override
    public Document getOwnerDocument() {
        return this.m_attributeNode.getOwnerDocument();
    }
    
    @Override
    public Node insertBefore(final Node arg0, final Node arg1) throws DOMException {
        return null;
    }
    
    @Override
    public Node replaceChild(final Node arg0, final Node arg1) throws DOMException {
        return null;
    }
    
    @Override
    public Node removeChild(final Node arg0) throws DOMException {
        return null;
    }
    
    @Override
    public Node appendChild(final Node arg0) throws DOMException {
        return null;
    }
    
    @Override
    public boolean hasChildNodes() {
        return false;
    }
    
    @Override
    public Node cloneNode(final boolean arg0) {
        throw new DOMException((short)9, null);
    }
    
    @Override
    public void normalize() {
        this.m_attributeNode.normalize();
    }
    
    @Override
    public boolean isSupported(final String arg0, final String arg1) {
        return this.m_attributeNode.isSupported(arg0, arg1);
    }
    
    @Override
    public String getNamespaceURI() {
        return this.m_attributeNode.getNodeValue();
    }
    
    @Override
    public String getPrefix() {
        return this.m_attributeNode.getPrefix();
    }
    
    @Override
    public void setPrefix(final String arg0) throws DOMException {
    }
    
    @Override
    public String getLocalName() {
        return this.m_attributeNode.getPrefix();
    }
    
    @Override
    public boolean hasAttributes() {
        return this.m_attributeNode.hasAttributes();
    }
    
    @Override
    public String getBaseURI() {
        return null;
    }
    
    @Override
    public short compareDocumentPosition(final Node other) throws DOMException {
        return 0;
    }
    
    @Override
    public String getTextContent() throws DOMException {
        return this.textContent;
    }
    
    @Override
    public void setTextContent(final String textContent) throws DOMException {
        this.textContent = textContent;
    }
    
    @Override
    public boolean isSameNode(final Node other) {
        return false;
    }
    
    @Override
    public String lookupPrefix(final String namespaceURI) {
        return "";
    }
    
    @Override
    public boolean isDefaultNamespace(final String namespaceURI) {
        return false;
    }
    
    @Override
    public String lookupNamespaceURI(final String prefix) {
        return null;
    }
    
    @Override
    public boolean isEqualNode(final Node arg) {
        return false;
    }
    
    @Override
    public Object getFeature(final String feature, final String version) {
        return null;
    }
    
    @Override
    public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
        return null;
    }
    
    @Override
    public Object getUserData(final String key) {
        return null;
    }
}
