package org.apache.xerces.impl.xs.opti;

import org.w3c.dom.UserDataHandler;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class DefaultNode implements Node
{
    public String getNodeName() {
        return null;
    }
    
    public String getNodeValue() throws DOMException {
        return null;
    }
    
    public short getNodeType() {
        return -1;
    }
    
    public Node getParentNode() {
        return null;
    }
    
    public NodeList getChildNodes() {
        return null;
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
    
    public boolean hasChildNodes() {
        return false;
    }
    
    public Node cloneNode(final boolean b) {
        return null;
    }
    
    public void normalize() {
    }
    
    public boolean isSupported(final String s, final String s2) {
        return false;
    }
    
    public String getNamespaceURI() {
        return null;
    }
    
    public String getPrefix() {
        return null;
    }
    
    public String getLocalName() {
        return null;
    }
    
    public String getBaseURI() {
        return null;
    }
    
    public boolean hasAttributes() {
        return false;
    }
    
    public void setNodeValue(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Node insertBefore(final Node node, final Node node2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Node replaceChild(final Node node, final Node node2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Node removeChild(final Node node) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Node appendChild(final Node node) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void setPrefix(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public short compareDocumentPosition(final Node node) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public String getTextContent() throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public void setTextContent(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public boolean isSameNode(final Node node) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public String lookupPrefix(final String s) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public boolean isDefaultNamespace(final String s) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public String lookupNamespaceURI(final String s) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public boolean isEqualNode(final Node node) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Object getFeature(final String s, final String s2) {
        return null;
    }
    
    public Object setUserData(final String s, final Object o, final UserDataHandler userDataHandler) {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Object getUserData(final String s) {
        return null;
    }
}
