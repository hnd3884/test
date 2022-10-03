package org.apache.xerces.dom;

import org.w3c.dom.UserDataHandler;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class ParentNode extends ChildNode
{
    static final long serialVersionUID = 2815829867152120872L;
    protected CoreDocumentImpl ownerDocument;
    protected ChildNode firstChild;
    protected transient NodeListCache fNodeListCache;
    
    protected ParentNode(final CoreDocumentImpl ownerDocument) {
        super(ownerDocument);
        this.firstChild = null;
        this.fNodeListCache = null;
        this.ownerDocument = ownerDocument;
    }
    
    public ParentNode() {
        this.firstChild = null;
        this.fNodeListCache = null;
    }
    
    public Node cloneNode(final boolean b) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        final ParentNode parentNode = (ParentNode)super.cloneNode(b);
        parentNode.ownerDocument = this.ownerDocument;
        parentNode.firstChild = null;
        parentNode.fNodeListCache = null;
        if (b) {
            for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling) {
                parentNode.appendChild(childNode.cloneNode(true));
            }
        }
        return parentNode;
    }
    
    public Document getOwnerDocument() {
        return this.ownerDocument;
    }
    
    CoreDocumentImpl ownerDocument() {
        return this.ownerDocument;
    }
    
    protected void setOwnerDocument(final CoreDocumentImpl ownerDocument) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        super.setOwnerDocument(ownerDocument);
        this.ownerDocument = ownerDocument;
        for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling) {
            childNode.setOwnerDocument(ownerDocument);
        }
    }
    
    public boolean hasChildNodes() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.firstChild != null;
    }
    
    public NodeList getChildNodes() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this;
    }
    
    public Node getFirstChild() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.firstChild;
    }
    
    public Node getLastChild() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.lastChild();
    }
    
    final ChildNode lastChild() {
        return (this.firstChild != null) ? this.firstChild.previousSibling : null;
    }
    
    final void lastChild(final ChildNode previousSibling) {
        if (this.firstChild != null) {
            this.firstChild.previousSibling = previousSibling;
        }
    }
    
    public Node insertBefore(final Node node, final Node node2) throws DOMException {
        return this.internalInsertBefore(node, node2, false);
    }
    
    Node internalInsertBefore(final Node node, Node nextSibling, final boolean b) throws DOMException {
        final boolean errorChecking = this.ownerDocument.errorChecking;
        if (node.getNodeType() == 11) {
            if (errorChecking) {
                for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                    if (!this.ownerDocument.isKidOK(this, node2)) {
                        throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
                    }
                }
            }
            while (node.hasChildNodes()) {
                this.insertBefore(node.getFirstChild(), nextSibling);
            }
            return node;
        }
        if (node == nextSibling) {
            nextSibling = nextSibling.getNextSibling();
            this.removeChild(node);
            this.insertBefore(node, nextSibling);
            return node;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (node.getOwnerDocument() != this.ownerDocument && node != this.ownerDocument) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
            if (!this.ownerDocument.isKidOK(this, node)) {
                throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
            }
            if (nextSibling != null && nextSibling.getParentNode() != this) {
                throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
            }
            boolean b2 = true;
            for (NodeImpl parentNode = this; b2 && parentNode != null; b2 = (node != parentNode), parentNode = parentNode.parentNode()) {}
            if (!b2) {
                throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
            }
        }
        this.ownerDocument.insertingNode(this, b);
        final ChildNode fChild = (ChildNode)node;
        final NodeImpl parentNode2 = fChild.parentNode();
        if (parentNode2 != null) {
            parentNode2.removeChild(fChild);
        }
        final ChildNode nextSibling2 = (ChildNode)nextSibling;
        fChild.ownerNode = this;
        fChild.isOwned(true);
        if (this.firstChild == null) {
            (this.firstChild = fChild).isFirstChild(true);
            fChild.previousSibling = fChild;
        }
        else if (nextSibling2 == null) {
            final ChildNode previousSibling = this.firstChild.previousSibling;
            previousSibling.nextSibling = fChild;
            fChild.previousSibling = previousSibling;
            this.firstChild.previousSibling = fChild;
        }
        else if (nextSibling == this.firstChild) {
            this.firstChild.isFirstChild(false);
            fChild.nextSibling = this.firstChild;
            fChild.previousSibling = this.firstChild.previousSibling;
            this.firstChild.previousSibling = fChild;
            (this.firstChild = fChild).isFirstChild(true);
        }
        else {
            final ChildNode previousSibling2 = nextSibling2.previousSibling;
            fChild.nextSibling = nextSibling2;
            previousSibling2.nextSibling = fChild;
            nextSibling2.previousSibling = fChild;
            fChild.previousSibling = previousSibling2;
        }
        this.changed();
        if (this.fNodeListCache != null) {
            if (this.fNodeListCache.fLength != -1) {
                final NodeListCache fNodeListCache = this.fNodeListCache;
                ++fNodeListCache.fLength;
            }
            if (this.fNodeListCache.fChildIndex != -1) {
                if (this.fNodeListCache.fChild == nextSibling2) {
                    this.fNodeListCache.fChild = fChild;
                }
                else {
                    this.fNodeListCache.fChildIndex = -1;
                }
            }
        }
        this.ownerDocument.insertedNode(this, fChild, b);
        this.checkNormalizationAfterInsert(fChild);
        return node;
    }
    
    public Node removeChild(final Node node) throws DOMException {
        return this.internalRemoveChild(node, false);
    }
    
    Node internalRemoveChild(final Node node, final boolean b) throws DOMException {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (node != null && node.getParentNode() != this) {
                throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
            }
        }
        final ChildNode childNode = (ChildNode)node;
        ownerDocument.removingNode(this, childNode, b);
        final ChildNode previousSibling = childNode.previousSibling();
        if (this.fNodeListCache != null) {
            if (this.fNodeListCache.fLength != -1) {
                final NodeListCache fNodeListCache = this.fNodeListCache;
                --fNodeListCache.fLength;
            }
            if (this.fNodeListCache.fChildIndex != -1) {
                if (this.fNodeListCache.fChild == childNode) {
                    final NodeListCache fNodeListCache2 = this.fNodeListCache;
                    --fNodeListCache2.fChildIndex;
                    this.fNodeListCache.fChild = previousSibling;
                }
                else {
                    this.fNodeListCache.fChildIndex = -1;
                }
            }
        }
        if (childNode == this.firstChild) {
            childNode.isFirstChild(false);
            this.firstChild = childNode.nextSibling;
            if (this.firstChild != null) {
                this.firstChild.isFirstChild(true);
                this.firstChild.previousSibling = childNode.previousSibling;
            }
        }
        else {
            final ChildNode previousSibling2 = childNode.previousSibling;
            final ChildNode nextSibling = childNode.nextSibling;
            if ((previousSibling2.nextSibling = nextSibling) == null) {
                this.firstChild.previousSibling = previousSibling2;
            }
            else {
                nextSibling.previousSibling = previousSibling2;
            }
        }
        childNode.ownerNode = ownerDocument;
        childNode.isOwned(false);
        childNode.nextSibling = null;
        childNode.previousSibling = null;
        this.changed();
        ownerDocument.removedNode(this, b);
        this.checkNormalizationAfterRemove(previousSibling);
        return childNode;
    }
    
    public Node replaceChild(final Node node, final Node node2) throws DOMException {
        this.ownerDocument.replacingNode(this);
        this.internalInsertBefore(node, node2, true);
        if (node != node2) {
            this.internalRemoveChild(node2, true);
        }
        this.ownerDocument.replacedNode(this);
        return node2;
    }
    
    public String getTextContent() throws DOMException {
        final Node firstChild = this.getFirstChild();
        if (firstChild == null) {
            return "";
        }
        if (firstChild.getNextSibling() == null) {
            return this.hasTextContent(firstChild) ? ((NodeImpl)firstChild).getTextContent() : "";
        }
        final StringBuffer sb = new StringBuffer();
        this.getTextContent(sb);
        return sb.toString();
    }
    
    void getTextContent(final StringBuffer sb) throws DOMException {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (this.hasTextContent(node)) {
                ((NodeImpl)node).getTextContent(sb);
            }
        }
    }
    
    final boolean hasTextContent(final Node node) {
        return node.getNodeType() != 8 && node.getNodeType() != 7 && (node.getNodeType() != 3 || !((TextImpl)node).isIgnorableWhitespace());
    }
    
    public void setTextContent(final String s) throws DOMException {
        Node firstChild;
        while ((firstChild = this.getFirstChild()) != null) {
            this.removeChild(firstChild);
        }
        if (s != null && s.length() != 0) {
            this.appendChild(this.ownerDocument().createTextNode(s));
        }
    }
    
    private int nodeListGetLength() {
        if (this.fNodeListCache == null) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            if (this.firstChild == null) {
                return 0;
            }
            if (this.firstChild == this.lastChild()) {
                return 1;
            }
            this.fNodeListCache = this.ownerDocument.getNodeListCache(this);
        }
        if (this.fNodeListCache.fLength == -1) {
            int fChildIndex;
            ChildNode childNode;
            if (this.fNodeListCache.fChildIndex != -1 && this.fNodeListCache.fChild != null) {
                fChildIndex = this.fNodeListCache.fChildIndex;
                childNode = this.fNodeListCache.fChild;
            }
            else {
                childNode = this.firstChild;
                fChildIndex = 0;
            }
            while (childNode != null) {
                ++fChildIndex;
                childNode = childNode.nextSibling;
            }
            this.fNodeListCache.fLength = fChildIndex;
        }
        return this.fNodeListCache.fLength;
    }
    
    public int getLength() {
        return this.nodeListGetLength();
    }
    
    private Node nodeListItem(final int n) {
        if (this.fNodeListCache == null) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            if (this.firstChild == this.lastChild()) {
                return (n == 0) ? this.firstChild : null;
            }
            this.fNodeListCache = this.ownerDocument.getNodeListCache(this);
        }
        int fChildIndex = this.fNodeListCache.fChildIndex;
        ChildNode fChild = this.fNodeListCache.fChild;
        boolean b = true;
        if (fChildIndex != -1 && fChild != null) {
            b = false;
            if (fChildIndex < n) {
                while (fChildIndex < n && fChild != null) {
                    ++fChildIndex;
                    fChild = fChild.nextSibling;
                }
            }
            else if (fChildIndex > n) {
                while (fChildIndex > n && fChild != null) {
                    --fChildIndex;
                    fChild = fChild.previousSibling();
                }
            }
        }
        else {
            if (n < 0) {
                return null;
            }
            for (fChild = this.firstChild, fChildIndex = 0; fChildIndex < n && fChild != null; fChild = fChild.nextSibling, ++fChildIndex) {}
        }
        if (!b && (fChild == this.firstChild || fChild == this.lastChild())) {
            this.fNodeListCache.fChildIndex = -1;
            this.fNodeListCache.fChild = null;
            this.ownerDocument.freeNodeListCache(this.fNodeListCache);
        }
        else {
            this.fNodeListCache.fChildIndex = fChildIndex;
            this.fNodeListCache.fChild = fChild;
        }
        return fChild;
    }
    
    public Node item(final int n) {
        return this.nodeListItem(n);
    }
    
    protected final NodeList getChildNodesUnoptimized() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return new NodeList() {
            public int getLength() {
                return ParentNode.this.nodeListGetLength();
            }
            
            public Node item(final int n) {
                return ParentNode.this.nodeListItem(n);
            }
        };
    }
    
    public void normalize() {
        if (this.isNormalized()) {
            return;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling) {
            childNode.normalize();
        }
        this.isNormalized(true);
    }
    
    public boolean isEqualNode(final Node node) {
        if (!super.isEqualNode(node)) {
            return false;
        }
        Node node2;
        Node node3;
        for (node2 = this.getFirstChild(), node3 = node.getFirstChild(); node2 != null && node3 != null; node2 = node2.getNextSibling(), node3 = node3.getNextSibling()) {
            if (!node2.isEqualNode(node3)) {
                return false;
            }
        }
        return node2 == node3;
    }
    
    public void setReadOnly(final boolean b, final boolean b2) {
        super.setReadOnly(b, b2);
        if (b2) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling) {
                if (childNode.getNodeType() != 5) {
                    childNode.setReadOnly(b, true);
                }
            }
        }
    }
    
    protected void synchronizeChildren() {
        this.needsSyncChildren(false);
    }
    
    void checkNormalizationAfterInsert(final ChildNode childNode) {
        if (childNode.getNodeType() == 3) {
            final ChildNode previousSibling = childNode.previousSibling();
            final ChildNode nextSibling = childNode.nextSibling;
            if ((previousSibling != null && previousSibling.getNodeType() == 3) || (nextSibling != null && nextSibling.getNodeType() == 3)) {
                this.isNormalized(false);
            }
        }
        else if (!childNode.isNormalized()) {
            this.isNormalized(false);
        }
    }
    
    void checkNormalizationAfterRemove(final ChildNode childNode) {
        if (childNode != null && childNode.getNodeType() == 3) {
            final ChildNode nextSibling = childNode.nextSibling;
            if (nextSibling != null && nextSibling.getNodeType() == 3) {
                this.isNormalized(false);
            }
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        objectOutputStream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        this.needsSyncChildren(false);
    }
    
    class UserDataRecord implements Serializable
    {
        private static final long serialVersionUID = 3258126977134310455L;
        Object fData;
        UserDataHandler fHandler;
        
        UserDataRecord(final Object fData, final UserDataHandler fHandler) {
            this.fData = fData;
            this.fHandler = fHandler;
        }
    }
}
