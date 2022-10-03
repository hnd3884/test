package org.apache.xerces.dom;

import java.util.List;
import org.w3c.dom.Element;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class AttributeMap extends NamedNodeMapImpl
{
    static final long serialVersionUID = 8872606282138665383L;
    
    protected AttributeMap(final ElementImpl elementImpl, final NamedNodeMapImpl namedNodeMapImpl) {
        super(elementImpl);
        if (namedNodeMapImpl != null) {
            this.cloneContent(namedNodeMapImpl);
            if (this.nodes != null) {
                this.hasDefaults(true);
            }
        }
    }
    
    public Node setNamedItem(final Node node) throws DOMException {
        final boolean errorChecking = this.ownerNode.ownerDocument().errorChecking;
        if (errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (node.getOwnerDocument() != this.ownerNode.ownerDocument()) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
            if (node.getNodeType() != 2) {
                throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
            }
        }
        final AttrImpl attrImpl = (AttrImpl)node;
        if (!attrImpl.isOwned()) {
            attrImpl.ownerNode = this.ownerNode;
            attrImpl.isOwned(true);
            final int namePoint = this.findNamePoint(attrImpl.getNodeName(), 0);
            AttrImpl attrImpl2 = null;
            if (namePoint >= 0) {
                attrImpl2 = this.nodes.get(namePoint);
                this.nodes.set(namePoint, node);
                attrImpl2.ownerNode = this.ownerNode.ownerDocument();
                attrImpl2.isOwned(false);
                attrImpl2.isSpecified(true);
            }
            else {
                final int n = -1 - namePoint;
                if (null == this.nodes) {
                    this.nodes = new ArrayList(5);
                }
                this.nodes.add(n, node);
            }
            this.ownerNode.ownerDocument().setAttrNode(attrImpl, attrImpl2);
            if (!attrImpl.isNormalized()) {
                this.ownerNode.isNormalized(false);
            }
            return attrImpl2;
        }
        if (errorChecking && attrImpl.getOwnerElement() != this.ownerNode) {
            throw new DOMException((short)10, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", null));
        }
        return node;
    }
    
    public Node setNamedItemNS(final Node node) throws DOMException {
        final boolean errorChecking = this.ownerNode.ownerDocument().errorChecking;
        if (errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (node.getOwnerDocument() != this.ownerNode.ownerDocument()) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
            if (node.getNodeType() != 2) {
                throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
            }
        }
        final AttrImpl attrImpl = (AttrImpl)node;
        if (!attrImpl.isOwned()) {
            attrImpl.ownerNode = this.ownerNode;
            attrImpl.isOwned(true);
            final int namePoint = this.findNamePoint(attrImpl.getNamespaceURI(), attrImpl.getLocalName());
            AttrImpl attrImpl2 = null;
            if (namePoint >= 0) {
                attrImpl2 = this.nodes.get(namePoint);
                this.nodes.set(namePoint, node);
                attrImpl2.ownerNode = this.ownerNode.ownerDocument();
                attrImpl2.isOwned(false);
                attrImpl2.isSpecified(true);
            }
            else {
                final int namePoint2 = this.findNamePoint(node.getNodeName(), 0);
                if (namePoint2 >= 0) {
                    attrImpl2 = (AttrImpl)this.nodes.get(namePoint2);
                    this.nodes.add(namePoint2, node);
                }
                else {
                    final int n = -1 - namePoint2;
                    if (null == this.nodes) {
                        this.nodes = new ArrayList(5);
                    }
                    this.nodes.add(n, node);
                }
            }
            this.ownerNode.ownerDocument().setAttrNode(attrImpl, attrImpl2);
            if (!attrImpl.isNormalized()) {
                this.ownerNode.isNormalized(false);
            }
            return attrImpl2;
        }
        if (errorChecking && attrImpl.getOwnerElement() != this.ownerNode) {
            throw new DOMException((short)10, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", null));
        }
        return node;
    }
    
    public Node removeNamedItem(final String s) throws DOMException {
        return this.internalRemoveNamedItem(s, true);
    }
    
    Node safeRemoveNamedItem(final String s) {
        return this.internalRemoveNamedItem(s, false);
    }
    
    protected Node removeItem(final Node node, final boolean b) throws DOMException {
        int n = -1;
        if (this.nodes != null) {
            for (int size = this.nodes.size(), i = 0; i < size; ++i) {
                if (this.nodes.get(i) == node) {
                    n = i;
                    break;
                }
            }
        }
        if (n < 0) {
            throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
        }
        return this.remove((AttrImpl)node, n, b);
    }
    
    protected final Node internalRemoveNamedItem(final String s, final boolean b) {
        if (this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        final int namePoint = this.findNamePoint(s, 0);
        if (namePoint >= 0) {
            return this.remove((AttrImpl)this.nodes.get(namePoint), namePoint, true);
        }
        if (b) {
            throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
        }
        return null;
    }
    
    private final Node remove(final AttrImpl attrImpl, final int n, final boolean b) {
        final CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
        final String nodeName = attrImpl.getNodeName();
        if (attrImpl.isIdAttribute()) {
            ownerDocument.removeIdentifier(attrImpl.getValue());
        }
        if (this.hasDefaults() && b) {
            final NamedNodeMapImpl defaultAttributes = ((ElementImpl)this.ownerNode).getDefaultAttributes();
            final Node namedItem;
            if (defaultAttributes != null && (namedItem = defaultAttributes.getNamedItem(nodeName)) != null && this.findNamePoint(nodeName, n + 1) < 0) {
                final NodeImpl nodeImpl = (NodeImpl)namedItem.cloneNode(true);
                if (namedItem.getLocalName() != null) {
                    ((AttrNSImpl)nodeImpl).namespaceURI = attrImpl.getNamespaceURI();
                }
                nodeImpl.ownerNode = this.ownerNode;
                nodeImpl.isOwned(true);
                nodeImpl.isSpecified(false);
                this.nodes.set(n, nodeImpl);
                if (attrImpl.isIdAttribute()) {
                    ownerDocument.putIdentifier(nodeImpl.getNodeValue(), (Element)this.ownerNode);
                }
            }
            else {
                this.nodes.remove(n);
            }
        }
        else {
            this.nodes.remove(n);
        }
        attrImpl.ownerNode = ownerDocument;
        attrImpl.isOwned(false);
        attrImpl.isSpecified(true);
        attrImpl.isIdAttribute(false);
        ownerDocument.removedAttrNode(attrImpl, this.ownerNode, nodeName);
        return attrImpl;
    }
    
    public Node removeNamedItemNS(final String s, final String s2) throws DOMException {
        return this.internalRemoveNamedItemNS(s, s2, true);
    }
    
    Node safeRemoveNamedItemNS(final String s, final String s2) {
        return this.internalRemoveNamedItemNS(s, s2, false);
    }
    
    protected final Node internalRemoveNamedItemNS(final String namespaceURI, final String s, final boolean b) {
        final CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
        if (ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        final int namePoint = this.findNamePoint(namespaceURI, s);
        if (namePoint >= 0) {
            final AttrImpl attrImpl = this.nodes.get(namePoint);
            if (attrImpl.isIdAttribute()) {
                ownerDocument.removeIdentifier(attrImpl.getValue());
            }
            final String nodeName = attrImpl.getNodeName();
            if (this.hasDefaults()) {
                final NamedNodeMapImpl defaultAttributes = ((ElementImpl)this.ownerNode).getDefaultAttributes();
                final Node namedItem;
                if (defaultAttributes != null && (namedItem = defaultAttributes.getNamedItem(nodeName)) != null) {
                    final int namePoint2 = this.findNamePoint(nodeName, 0);
                    if (namePoint2 >= 0 && this.findNamePoint(nodeName, namePoint2 + 1) < 0) {
                        final NodeImpl nodeImpl = (NodeImpl)namedItem.cloneNode(true);
                        nodeImpl.ownerNode = this.ownerNode;
                        if (namedItem.getLocalName() != null) {
                            ((AttrNSImpl)nodeImpl).namespaceURI = namespaceURI;
                        }
                        nodeImpl.isOwned(true);
                        nodeImpl.isSpecified(false);
                        this.nodes.set(namePoint, nodeImpl);
                        if (nodeImpl.isIdAttribute()) {
                            ownerDocument.putIdentifier(nodeImpl.getNodeValue(), (Element)this.ownerNode);
                        }
                    }
                    else {
                        this.nodes.remove(namePoint);
                    }
                }
                else {
                    this.nodes.remove(namePoint);
                }
            }
            else {
                this.nodes.remove(namePoint);
            }
            attrImpl.ownerNode = ownerDocument;
            attrImpl.isOwned(false);
            attrImpl.isSpecified(true);
            attrImpl.isIdAttribute(false);
            ownerDocument.removedAttrNode(attrImpl, this.ownerNode, s);
            return attrImpl;
        }
        if (b) {
            throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
        }
        return null;
    }
    
    public NamedNodeMapImpl cloneMap(final NodeImpl nodeImpl) {
        final AttributeMap attributeMap = new AttributeMap((ElementImpl)nodeImpl, null);
        attributeMap.hasDefaults(this.hasDefaults());
        attributeMap.cloneContent(this);
        return attributeMap;
    }
    
    protected void cloneContent(final NamedNodeMapImpl namedNodeMapImpl) {
        final List nodes = namedNodeMapImpl.nodes;
        if (nodes != null) {
            final int size = nodes.size();
            if (size != 0) {
                if (this.nodes == null) {
                    this.nodes = new ArrayList(size);
                }
                else {
                    this.nodes.clear();
                }
                for (int i = 0; i < size; ++i) {
                    final NodeImpl nodeImpl = nodes.get(i);
                    final NodeImpl nodeImpl2 = (NodeImpl)nodeImpl.cloneNode(true);
                    nodeImpl2.isSpecified(nodeImpl.isSpecified());
                    this.nodes.add(nodeImpl2);
                    nodeImpl2.ownerNode = this.ownerNode;
                    nodeImpl2.isOwned(true);
                }
            }
        }
    }
    
    void moveSpecifiedAttributes(final AttributeMap attributeMap) {
        for (int i = ((attributeMap.nodes != null) ? attributeMap.nodes.size() : 0) - 1; i >= 0; --i) {
            final AttrImpl attrImpl = attributeMap.nodes.get(i);
            if (attrImpl.isSpecified()) {
                attributeMap.remove(attrImpl, i, false);
                if (attrImpl.getLocalName() != null) {
                    this.setNamedItem(attrImpl);
                }
                else {
                    this.setNamedItemNS(attrImpl);
                }
            }
        }
    }
    
    protected void reconcileDefaults(final NamedNodeMapImpl namedNodeMapImpl) {
        for (int i = ((this.nodes != null) ? this.nodes.size() : 0) - 1; i >= 0; --i) {
            final AttrImpl attrImpl = this.nodes.get(i);
            if (!attrImpl.isSpecified()) {
                this.remove(attrImpl, i, false);
            }
        }
        if (namedNodeMapImpl == null) {
            return;
        }
        if (this.nodes == null || this.nodes.size() == 0) {
            this.cloneContent(namedNodeMapImpl);
        }
        else {
            for (int size = namedNodeMapImpl.nodes.size(), j = 0; j < size; ++j) {
                final AttrImpl attrImpl2 = namedNodeMapImpl.nodes.get(j);
                final int namePoint = this.findNamePoint(attrImpl2.getNodeName(), 0);
                if (namePoint < 0) {
                    final int n = -1 - namePoint;
                    final NodeImpl nodeImpl = (NodeImpl)attrImpl2.cloneNode(true);
                    nodeImpl.ownerNode = this.ownerNode;
                    nodeImpl.isOwned(true);
                    nodeImpl.isSpecified(false);
                    this.nodes.add(n, nodeImpl);
                }
            }
        }
    }
    
    protected final int addItem(final Node node) {
        final AttrImpl attrImpl = (AttrImpl)node;
        attrImpl.ownerNode = this.ownerNode;
        attrImpl.isOwned(true);
        int n = this.findNamePoint(attrImpl.getNamespaceURI(), attrImpl.getLocalName());
        if (n >= 0) {
            this.nodes.set(n, node);
        }
        else {
            n = this.findNamePoint(attrImpl.getNodeName(), 0);
            if (n >= 0) {
                this.nodes.add(n, node);
            }
            else {
                n = -1 - n;
                if (null == this.nodes) {
                    this.nodes = new ArrayList(5);
                }
                this.nodes.add(n, node);
            }
        }
        this.ownerNode.ownerDocument().setAttrNode(attrImpl, null);
        return n;
    }
}
