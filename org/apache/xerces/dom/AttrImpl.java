package org.apache.xerces.dom;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.Attr;

public class AttrImpl extends NodeImpl implements Attr, TypeInfo
{
    static final long serialVersionUID = 7277707688218972102L;
    static final String DTD_URI = "http://www.w3.org/TR/REC-xml";
    protected Object value;
    protected String name;
    transient Object type;
    
    protected AttrImpl(final CoreDocumentImpl coreDocumentImpl, final String name) {
        super(coreDocumentImpl);
        this.value = null;
        this.name = name;
        this.isSpecified(true);
        this.hasStringValue(true);
    }
    
    protected AttrImpl() {
        this.value = null;
    }
    
    void rename(final String name) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.name = name;
    }
    
    protected void makeChildNode() {
        if (this.hasStringValue()) {
            if (this.value != null) {
                final TextImpl textImpl = (TextImpl)this.ownerDocument().createTextNode((String)this.value);
                ((NodeImpl)(this.value = textImpl)).isFirstChild(true);
                textImpl.previousSibling = textImpl;
                textImpl.ownerNode = this;
                textImpl.isOwned(true);
            }
            this.hasStringValue(false);
        }
    }
    
    protected void setOwnerDocument(final CoreDocumentImpl coreDocumentImpl) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        super.setOwnerDocument(coreDocumentImpl);
        if (!this.hasStringValue()) {
            for (ChildNode nextSibling = (ChildNode)this.value; nextSibling != null; nextSibling = nextSibling.nextSibling) {
                nextSibling.setOwnerDocument(coreDocumentImpl);
            }
        }
    }
    
    public void setIdAttribute(final boolean b) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isIdAttribute(b);
    }
    
    public boolean isId() {
        return this.isIdAttribute();
    }
    
    public Node cloneNode(final boolean b) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        final AttrImpl attrImpl = (AttrImpl)super.cloneNode(b);
        if (!attrImpl.hasStringValue()) {
            attrImpl.value = null;
            for (Node nextSibling = (Node)this.value; nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
                attrImpl.appendChild(nextSibling.cloneNode(true));
            }
        }
        attrImpl.isSpecified(true);
        return attrImpl;
    }
    
    public short getNodeType() {
        return 2;
    }
    
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    public void setNodeValue(final String value) throws DOMException {
        this.setValue(value);
    }
    
    public String getTypeName() {
        return (String)this.type;
    }
    
    public String getTypeNamespace() {
        if (this.type != null) {
            return "http://www.w3.org/TR/REC-xml";
        }
        return null;
    }
    
    public TypeInfo getSchemaTypeInfo() {
        return this;
    }
    
    public String getNodeValue() {
        return this.getValue();
    }
    
    public String getName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    public void setValue(final String s) {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        final Element ownerElement = this.getOwnerElement();
        String s2 = "";
        TextImpl textImpl = null;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (this.value != null) {
            if (ownerDocument.getMutationEvents()) {
                if (this.hasStringValue()) {
                    s2 = (String)this.value;
                    textImpl = (TextImpl)ownerDocument.createTextNode((String)this.value);
                    ((NodeImpl)(this.value = textImpl)).isFirstChild(true);
                    textImpl.previousSibling = textImpl;
                    textImpl.ownerNode = this;
                    textImpl.isOwned(true);
                    this.hasStringValue(false);
                    this.internalRemoveChild(textImpl, true);
                }
                else {
                    s2 = this.getValue();
                    while (this.value != null) {
                        this.internalRemoveChild((Node)this.value, true);
                    }
                }
            }
            else {
                if (this.hasStringValue()) {
                    s2 = (String)this.value;
                }
                else {
                    s2 = this.getValue();
                    final ChildNode childNode = (ChildNode)this.value;
                    childNode.previousSibling = null;
                    childNode.isFirstChild(false);
                    childNode.ownerNode = ownerDocument;
                }
                this.value = null;
                this.needsSyncChildren(false);
            }
            if (this.isIdAttribute() && ownerElement != null) {
                ownerDocument.removeIdentifier(s2);
            }
        }
        this.isSpecified(true);
        if (ownerDocument.getMutationEvents()) {
            if (textImpl == null) {
                textImpl = (TextImpl)ownerDocument.createTextNode(s);
            }
            else {
                textImpl.data = s;
            }
            this.internalInsertBefore(textImpl, null, true);
            this.hasStringValue(false);
            ownerDocument.modifiedAttrValue(this, s2);
        }
        else {
            this.value = s;
            this.hasStringValue(true);
            this.changed();
        }
        if (this.isIdAttribute() && ownerElement != null) {
            ownerDocument.putIdentifier(s, ownerElement);
        }
    }
    
    public String getValue() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (this.value == null) {
            return "";
        }
        if (this.hasStringValue()) {
            return (String)this.value;
        }
        final ChildNode childNode = (ChildNode)this.value;
        String s;
        if (childNode.getNodeType() == 5) {
            s = ((EntityReferenceImpl)childNode).getEntityRefValue();
        }
        else {
            s = childNode.getNodeValue();
        }
        ChildNode childNode2 = childNode.nextSibling;
        if (childNode2 == null || s == null) {
            return (s == null) ? "" : s;
        }
        final StringBuffer sb = new StringBuffer(s);
        while (childNode2 != null) {
            if (childNode2.getNodeType() == 5) {
                final String entityRefValue = ((EntityReferenceImpl)childNode2).getEntityRefValue();
                if (entityRefValue == null) {
                    return "";
                }
                sb.append(entityRefValue);
            }
            else {
                sb.append(childNode2.getNodeValue());
            }
            childNode2 = childNode2.nextSibling;
        }
        return sb.toString();
    }
    
    public boolean getSpecified() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.isSpecified();
    }
    
    public Element getElement() {
        return (Element)(this.isOwned() ? this.ownerNode : null);
    }
    
    public Element getOwnerElement() {
        return (Element)(this.isOwned() ? this.ownerNode : null);
    }
    
    public void normalize() {
        if (this.isNormalized() || this.hasStringValue()) {
            return;
        }
        Node nextSibling;
        for (Object o = this.value; o != null; o = nextSibling) {
            nextSibling = ((Node)o).getNextSibling();
            if (((Node)o).getNodeType() == 3) {
                if (nextSibling != null && nextSibling.getNodeType() == 3) {
                    ((Text)o).appendData(nextSibling.getNodeValue());
                    this.removeChild(nextSibling);
                    nextSibling = (Node)o;
                }
                else if (((Node)o).getNodeValue() == null || ((Node)o).getNodeValue().length() == 0) {
                    this.removeChild((Node)o);
                }
            }
        }
        this.isNormalized(true);
    }
    
    public void setSpecified(final boolean b) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isSpecified(b);
    }
    
    public void setType(final Object type) {
        this.type = type;
    }
    
    public String toString() {
        return this.getName() + "=" + "\"" + this.getValue() + "\"";
    }
    
    public boolean hasChildNodes() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.value != null;
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
        this.makeChildNode();
        return (Node)this.value;
    }
    
    public Node getLastChild() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.lastChild();
    }
    
    final ChildNode lastChild() {
        this.makeChildNode();
        return (this.value != null) ? ((ChildNode)this.value).previousSibling : null;
    }
    
    final void lastChild(final ChildNode previousSibling) {
        if (this.value != null) {
            ((ChildNode)this.value).previousSibling = previousSibling;
        }
    }
    
    public Node insertBefore(final Node node, final Node node2) throws DOMException {
        return this.internalInsertBefore(node, node2, false);
    }
    
    Node internalInsertBefore(final Node node, Node nextSibling, final boolean b) throws DOMException {
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        final boolean errorChecking = ownerDocument.errorChecking;
        if (node.getNodeType() == 11) {
            if (errorChecking) {
                for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                    if (!ownerDocument.isKidOK(this, node2)) {
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
            if (node.getOwnerDocument() != ownerDocument) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
            if (!ownerDocument.isKidOK(this, node)) {
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
        this.makeChildNode();
        ownerDocument.insertingNode(this, b);
        final ChildNode childNode = (ChildNode)node;
        final NodeImpl parentNode2 = childNode.parentNode();
        if (parentNode2 != null) {
            parentNode2.removeChild(childNode);
        }
        final ChildNode nextSibling2 = (ChildNode)nextSibling;
        childNode.ownerNode = this;
        childNode.isOwned(true);
        final ChildNode nextSibling3 = (ChildNode)this.value;
        if (nextSibling3 == null) {
            ((NodeImpl)(this.value = childNode)).isFirstChild(true);
            childNode.previousSibling = childNode;
        }
        else if (nextSibling2 == null) {
            final ChildNode previousSibling = nextSibling3.previousSibling;
            previousSibling.nextSibling = childNode;
            childNode.previousSibling = previousSibling;
            nextSibling3.previousSibling = childNode;
        }
        else if (nextSibling == nextSibling3) {
            nextSibling3.isFirstChild(false);
            childNode.nextSibling = nextSibling3;
            childNode.previousSibling = nextSibling3.previousSibling;
            nextSibling3.previousSibling = childNode;
            ((NodeImpl)(this.value = childNode)).isFirstChild(true);
        }
        else {
            final ChildNode previousSibling2 = nextSibling2.previousSibling;
            childNode.nextSibling = nextSibling2;
            previousSibling2.nextSibling = childNode;
            nextSibling2.previousSibling = childNode;
            childNode.previousSibling = previousSibling2;
        }
        this.changed();
        ownerDocument.insertedNode(this, childNode, b);
        this.checkNormalizationAfterInsert(childNode);
        return node;
    }
    
    public Node removeChild(final Node node) throws DOMException {
        if (this.hasStringValue()) {
            throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
        }
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
        if (childNode == this.value) {
            childNode.isFirstChild(false);
            this.value = childNode.nextSibling;
            final ChildNode childNode2 = (ChildNode)this.value;
            if (childNode2 != null) {
                childNode2.isFirstChild(true);
                childNode2.previousSibling = childNode.previousSibling;
            }
        }
        else {
            final ChildNode previousSibling = childNode.previousSibling;
            final ChildNode nextSibling = childNode.nextSibling;
            if ((previousSibling.nextSibling = nextSibling) == null) {
                ((ChildNode)this.value).previousSibling = previousSibling;
            }
            else {
                nextSibling.previousSibling = previousSibling;
            }
        }
        final ChildNode previousSibling2 = childNode.previousSibling();
        childNode.ownerNode = ownerDocument;
        childNode.isOwned(false);
        childNode.nextSibling = null;
        childNode.previousSibling = null;
        this.changed();
        ownerDocument.removedNode(this, b);
        this.checkNormalizationAfterRemove(previousSibling2);
        return childNode;
    }
    
    public Node replaceChild(final Node node, final Node node2) throws DOMException {
        this.makeChildNode();
        final CoreDocumentImpl ownerDocument = this.ownerDocument();
        ownerDocument.replacingNode(this);
        this.internalInsertBefore(node, node2, true);
        if (node != node2) {
            this.internalRemoveChild(node2, true);
        }
        ownerDocument.replacedNode(this);
        return node2;
    }
    
    public int getLength() {
        if (this.hasStringValue()) {
            return 1;
        }
        ChildNode nextSibling = (ChildNode)this.value;
        int n = 0;
        while (nextSibling != null) {
            ++n;
            nextSibling = nextSibling.nextSibling;
        }
        return n;
    }
    
    public Node item(final int n) {
        if (this.hasStringValue()) {
            if (n != 0 || this.value == null) {
                return null;
            }
            this.makeChildNode();
            return (Node)this.value;
        }
        else {
            if (n < 0) {
                return null;
            }
            ChildNode nextSibling = (ChildNode)this.value;
            for (int n2 = 0; n2 < n && nextSibling != null; nextSibling = nextSibling.nextSibling, ++n2) {}
            return nextSibling;
        }
    }
    
    public boolean isEqualNode(final Node node) {
        return super.isEqualNode(node);
    }
    
    public boolean isDerivedFrom(final String s, final String s2, final int n) {
        return false;
    }
    
    public void setReadOnly(final boolean b, final boolean b2) {
        super.setReadOnly(b, b2);
        if (b2) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            if (this.hasStringValue()) {
                return;
            }
            for (ChildNode nextSibling = (ChildNode)this.value; nextSibling != null; nextSibling = nextSibling.nextSibling) {
                if (nextSibling.getNodeType() != 5) {
                    nextSibling.setReadOnly(b, true);
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
}
