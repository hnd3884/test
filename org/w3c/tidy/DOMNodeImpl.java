package org.w3c.tidy;

import org.w3c.dom.UserDataHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class DOMNodeImpl implements Node
{
    protected org.w3c.tidy.Node adaptee;
    
    protected DOMNodeImpl(final org.w3c.tidy.Node adaptee) {
        this.adaptee = adaptee;
    }
    
    public String getNodeValue() {
        String string = "";
        if ((this.adaptee.type == 4 || this.adaptee.type == 8 || this.adaptee.type == 2 || this.adaptee.type == 3) && this.adaptee.textarray != null && this.adaptee.start < this.adaptee.end) {
            string = TidyUtils.getString(this.adaptee.textarray, this.adaptee.start, this.adaptee.end - this.adaptee.start);
        }
        return string;
    }
    
    public void setNodeValue(final String s) {
        if (this.adaptee.type == 4 || this.adaptee.type == 8 || this.adaptee.type == 2 || this.adaptee.type == 3) {
            final byte[] bytes = TidyUtils.getBytes(s);
            this.adaptee.textarray = bytes;
            this.adaptee.start = 0;
            this.adaptee.end = bytes.length;
        }
    }
    
    public String getNodeName() {
        return this.adaptee.element;
    }
    
    public short getNodeType() {
        short n = -1;
        switch (this.adaptee.type) {
            case 0: {
                n = 9;
                break;
            }
            case 1: {
                n = 10;
                break;
            }
            case 2: {
                n = 8;
                break;
            }
            case 3: {
                n = 7;
                break;
            }
            case 4: {
                n = 3;
                break;
            }
            case 8: {
                n = 4;
                break;
            }
            case 5:
            case 7: {
                n = 1;
                break;
            }
        }
        return n;
    }
    
    public Node getParentNode() {
        if (this.adaptee.parent != null) {
            return this.adaptee.parent.getAdapter();
        }
        return null;
    }
    
    public NodeList getChildNodes() {
        return new DOMNodeListImpl(this.adaptee);
    }
    
    public Node getFirstChild() {
        if (this.adaptee.content != null) {
            return this.adaptee.content.getAdapter();
        }
        return null;
    }
    
    public Node getLastChild() {
        if (this.adaptee.last != null) {
            return this.adaptee.last.getAdapter();
        }
        return null;
    }
    
    public Node getPreviousSibling() {
        if (this.adaptee.prev != null) {
            return this.adaptee.prev.getAdapter();
        }
        return null;
    }
    
    public Node getNextSibling() {
        if (this.adaptee.next != null) {
            return this.adaptee.next.getAdapter();
        }
        return null;
    }
    
    public NamedNodeMap getAttributes() {
        return new DOMAttrMapImpl(this.adaptee.attributes);
    }
    
    public Document getOwnerDocument() {
        org.w3c.tidy.Node node = this.adaptee;
        if (node != null && node.type == 0) {
            return null;
        }
        while (node != null && node.type != 0) {
            node = node.parent;
        }
        if (node != null) {
            return (Document)node.getAdapter();
        }
        return null;
    }
    
    public Node insertBefore(final Node node, final Node node2) {
        if (node == null) {
            return null;
        }
        if (!(node instanceof DOMNodeImpl)) {
            throw new DOMException((short)4, "newChild not instanceof DOMNodeImpl");
        }
        final DOMNodeImpl domNodeImpl = (DOMNodeImpl)node;
        if (this.adaptee.type == 0) {
            if (domNodeImpl.adaptee.type != 1 && domNodeImpl.adaptee.type != 3) {
                throw new DOMException((short)3, "newChild cannot be a child of this node");
            }
        }
        else if (this.adaptee.type == 5 && domNodeImpl.adaptee.type != 5 && domNodeImpl.adaptee.type != 7 && domNodeImpl.adaptee.type != 2 && domNodeImpl.adaptee.type != 4 && domNodeImpl.adaptee.type != 8) {
            throw new DOMException((short)3, "newChild cannot be a child of this node");
        }
        if (node2 == null) {
            this.adaptee.insertNodeAtEnd(domNodeImpl.adaptee);
            if (this.adaptee.type == 7) {
                this.adaptee.setType((short)5);
            }
        }
        else {
            org.w3c.tidy.Node node3;
            for (node3 = this.adaptee.content; node3 != null && node3.getAdapter() != node2; node3 = node3.next) {}
            if (node3 == null) {
                throw new DOMException((short)8, "refChild not found");
            }
            org.w3c.tidy.Node.insertNodeBeforeElement(node3, domNodeImpl.adaptee);
        }
        return node;
    }
    
    public Node replaceChild(final Node node, final Node node2) {
        if (node == null) {
            return null;
        }
        if (!(node instanceof DOMNodeImpl)) {
            throw new DOMException((short)4, "newChild not instanceof DOMNodeImpl");
        }
        final DOMNodeImpl domNodeImpl = (DOMNodeImpl)node;
        if (this.adaptee.type == 0) {
            if (domNodeImpl.adaptee.type != 1 && domNodeImpl.adaptee.type != 3) {
                throw new DOMException((short)3, "newChild cannot be a child of this node");
            }
        }
        else if (this.adaptee.type == 5 && domNodeImpl.adaptee.type != 5 && domNodeImpl.adaptee.type != 7 && domNodeImpl.adaptee.type != 2 && domNodeImpl.adaptee.type != 4 && domNodeImpl.adaptee.type != 8) {
            throw new DOMException((short)3, "newChild cannot be a child of this node");
        }
        if (node2 == null) {
            throw new DOMException((short)8, "oldChild not found");
        }
        org.w3c.tidy.Node node3;
        for (node3 = this.adaptee.content; node3 != null && node3.getAdapter() != node2; node3 = node3.next) {}
        if (node3 == null) {
            throw new DOMException((short)8, "oldChild not found");
        }
        domNodeImpl.adaptee.next = node3.next;
        domNodeImpl.adaptee.prev = node3.prev;
        domNodeImpl.adaptee.last = node3.last;
        domNodeImpl.adaptee.parent = node3.parent;
        domNodeImpl.adaptee.content = node3.content;
        if (node3.parent != null) {
            if (node3.parent.content == node3) {
                node3.parent.content = domNodeImpl.adaptee;
            }
            if (node3.parent.last == node3) {
                node3.parent.last = domNodeImpl.adaptee;
            }
        }
        if (node3.prev != null) {
            node3.prev.next = domNodeImpl.adaptee;
        }
        if (node3.next != null) {
            node3.next.prev = domNodeImpl.adaptee;
        }
        for (org.w3c.tidy.Node node4 = node3.content; node4 != null; node4 = node4.next) {
            if (node4.parent == node3) {
                node4.parent = domNodeImpl.adaptee;
            }
        }
        return node2;
    }
    
    public Node removeChild(final Node node) {
        if (node == null) {
            return null;
        }
        org.w3c.tidy.Node node2;
        for (node2 = this.adaptee.content; node2 != null && node2.getAdapter() != node; node2 = node2.next) {}
        if (node2 == null) {
            throw new DOMException((short)8, "refChild not found");
        }
        org.w3c.tidy.Node.discardElement(node2);
        if (this.adaptee.content == null && this.adaptee.type == 5) {
            this.adaptee.setType((short)7);
        }
        return node;
    }
    
    public Node appendChild(final Node node) {
        if (node == null) {
            return null;
        }
        if (!(node instanceof DOMNodeImpl)) {
            throw new DOMException((short)4, "newChild not instanceof DOMNodeImpl");
        }
        final DOMNodeImpl domNodeImpl = (DOMNodeImpl)node;
        if (this.adaptee.type == 0) {
            if (domNodeImpl.adaptee.type != 1 && domNodeImpl.adaptee.type != 3) {
                throw new DOMException((short)3, "newChild cannot be a child of this node");
            }
        }
        else if (this.adaptee.type == 5 && domNodeImpl.adaptee.type != 5 && domNodeImpl.adaptee.type != 7 && domNodeImpl.adaptee.type != 2 && domNodeImpl.adaptee.type != 4 && domNodeImpl.adaptee.type != 8) {
            throw new DOMException((short)3, "newChild cannot be a child of this node");
        }
        this.adaptee.insertNodeAtEnd(domNodeImpl.adaptee);
        if (this.adaptee.type == 7) {
            this.adaptee.setType((short)5);
        }
        return node;
    }
    
    public boolean hasChildNodes() {
        return this.adaptee.content != null;
    }
    
    public Node cloneNode(final boolean b) {
        final org.w3c.tidy.Node cloneNode = this.adaptee.cloneNode(b);
        cloneNode.parent = null;
        return cloneNode.getAdapter();
    }
    
    public void normalize() {
    }
    
    public boolean supports(final String s, final String s2) {
        return this.isSupported(s, s2);
    }
    
    public String getNamespaceURI() {
        return null;
    }
    
    public String getPrefix() {
        return null;
    }
    
    public void setPrefix(final String s) throws DOMException {
    }
    
    public String getLocalName() {
        return this.getNodeName();
    }
    
    public boolean isSupported(final String s, final String s2) {
        return false;
    }
    
    public boolean hasAttributes() {
        return this.adaptee.attributes != null;
    }
    
    public short compareDocumentPosition(final Node node) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public String getBaseURI() {
        return null;
    }
    
    public Object getFeature(final String s, final String s2) {
        return null;
    }
    
    public String getTextContent() throws DOMException {
        return null;
    }
    
    public Object getUserData(final String s) {
        return null;
    }
    
    public boolean isDefaultNamespace(final String s) {
        return false;
    }
    
    public boolean isEqualNode(final Node node) {
        return false;
    }
    
    public boolean isSameNode(final Node node) {
        return false;
    }
    
    public String lookupNamespaceURI(final String s) {
        return null;
    }
    
    public String lookupPrefix(final String s) {
        return null;
    }
    
    public void setTextContent(final String s) throws DOMException {
        throw new DOMException((short)7, "Node is read only");
    }
    
    public Object setUserData(final String s, final Object o, final UserDataHandler userDataHandler) {
        return null;
    }
}
