package org.apache.xerces.dom;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentType;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import java.io.Serializable;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public abstract class NodeImpl implements Node, NodeList, EventTarget, Cloneable, Serializable
{
    public static final short TREE_POSITION_PRECEDING = 1;
    public static final short TREE_POSITION_FOLLOWING = 2;
    public static final short TREE_POSITION_ANCESTOR = 4;
    public static final short TREE_POSITION_DESCENDANT = 8;
    public static final short TREE_POSITION_EQUIVALENT = 16;
    public static final short TREE_POSITION_SAME_NODE = 32;
    public static final short TREE_POSITION_DISCONNECTED = 0;
    public static final short DOCUMENT_POSITION_DISCONNECTED = 1;
    public static final short DOCUMENT_POSITION_PRECEDING = 2;
    public static final short DOCUMENT_POSITION_FOLLOWING = 4;
    public static final short DOCUMENT_POSITION_CONTAINS = 8;
    public static final short DOCUMENT_POSITION_IS_CONTAINED = 16;
    public static final short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32;
    static final long serialVersionUID = -6316591992167219696L;
    public static final short ELEMENT_DEFINITION_NODE = 21;
    protected NodeImpl ownerNode;
    protected short flags;
    protected static final short READONLY = 1;
    protected static final short SYNCDATA = 2;
    protected static final short SYNCCHILDREN = 4;
    protected static final short OWNED = 8;
    protected static final short FIRSTCHILD = 16;
    protected static final short SPECIFIED = 32;
    protected static final short IGNORABLEWS = 64;
    protected static final short HASSTRING = 128;
    protected static final short NORMALIZED = 256;
    protected static final short ID = 512;
    
    protected NodeImpl(final CoreDocumentImpl ownerNode) {
        this.ownerNode = ownerNode;
    }
    
    public NodeImpl() {
    }
    
    public abstract short getNodeType();
    
    public abstract String getNodeName();
    
    public String getNodeValue() throws DOMException {
        return null;
    }
    
    public void setNodeValue(final String s) throws DOMException {
    }
    
    public Node appendChild(final Node node) throws DOMException {
        return this.insertBefore(node, null);
    }
    
    public Node cloneNode(final boolean b) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        NodeImpl nodeImpl;
        try {
            nodeImpl = (NodeImpl)this.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new RuntimeException("**Internal Error**" + ex);
        }
        nodeImpl.ownerNode = this.ownerDocument();
        nodeImpl.isOwned(false);
        nodeImpl.isReadOnly(false);
        this.ownerDocument().callUserDataHandlers(this, nodeImpl, (short)1);
        return nodeImpl;
    }
    
    public Document getOwnerDocument() {
        if (this.isOwned()) {
            return this.ownerNode.ownerDocument();
        }
        return (Document)this.ownerNode;
    }
    
    CoreDocumentImpl ownerDocument() {
        if (this.isOwned()) {
            return this.ownerNode.ownerDocument();
        }
        return (CoreDocumentImpl)this.ownerNode;
    }
    
    protected void setOwnerDocument(final CoreDocumentImpl ownerNode) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (!this.isOwned()) {
            this.ownerNode = ownerNode;
        }
    }
    
    protected int getNodeNumber() {
        return ((CoreDocumentImpl)this.getOwnerDocument()).getNodeNumber(this);
    }
    
    public Node getParentNode() {
        return null;
    }
    
    NodeImpl parentNode() {
        return null;
    }
    
    public Node getNextSibling() {
        return null;
    }
    
    public Node getPreviousSibling() {
        return null;
    }
    
    ChildNode previousSibling() {
        return null;
    }
    
    public NamedNodeMap getAttributes() {
        return null;
    }
    
    public boolean hasAttributes() {
        return false;
    }
    
    public boolean hasChildNodes() {
        return false;
    }
    
    public NodeList getChildNodes() {
        return this;
    }
    
    public Node getFirstChild() {
        return null;
    }
    
    public Node getLastChild() {
        return null;
    }
    
    public Node insertBefore(final Node node, final Node node2) throws DOMException {
        throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
    }
    
    public Node removeChild(final Node node) throws DOMException {
        throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
    }
    
    public Node replaceChild(final Node node, final Node node2) throws DOMException {
        throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
    }
    
    public int getLength() {
        return 0;
    }
    
    public Node item(final int n) {
        return null;
    }
    
    public void normalize() {
    }
    
    public boolean isSupported(final String s, final String s2) {
        return this.ownerDocument().getImplementation().hasFeature(s, s2);
    }
    
    public String getNamespaceURI() {
        return null;
    }
    
    public String getPrefix() {
        return null;
    }
    
    public void setPrefix(final String s) throws DOMException {
        throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
    }
    
    public String getLocalName() {
        return null;
    }
    
    public void addEventListener(final String s, final EventListener eventListener, final boolean b) {
        this.ownerDocument().addEventListener(this, s, eventListener, b);
    }
    
    public void removeEventListener(final String s, final EventListener eventListener, final boolean b) {
        this.ownerDocument().removeEventListener(this, s, eventListener, b);
    }
    
    public boolean dispatchEvent(final Event event) {
        return this.ownerDocument().dispatchEvent(this, event);
    }
    
    public String getBaseURI() {
        return null;
    }
    
    public short compareTreePosition(final Node node) {
        if (this == node) {
            return 48;
        }
        final short nodeType = this.getNodeType();
        final short nodeType2 = node.getNodeType();
        if (nodeType == 6 || nodeType == 12 || nodeType2 == 6 || nodeType2 == 12) {
            return 0;
        }
        Node node2 = this;
        Node node3 = node;
        int n = 0;
        int n2 = 0;
        for (Node parentNode = this; parentNode != null; parentNode = parentNode.getParentNode()) {
            ++n;
            if (parentNode == node) {
                return 5;
            }
            node2 = parentNode;
        }
        for (Node parentNode2 = node; parentNode2 != null; parentNode2 = parentNode2.getParentNode()) {
            ++n2;
            if (parentNode2 == this) {
                return 10;
            }
            node3 = parentNode2;
        }
        Object o = this;
        Node node4 = node;
        final short nodeType3 = node2.getNodeType();
        final short nodeType4 = node3.getNodeType();
        if (nodeType3 == 2) {
            o = ((AttrImpl)node2).getOwnerElement();
        }
        if (nodeType4 == 2) {
            node4 = ((AttrImpl)node3).getOwnerElement();
        }
        if (nodeType3 == 2 && nodeType4 == 2 && o == node4) {
            return 16;
        }
        if (nodeType3 == 2) {
            n = 0;
            for (Node parentNode3 = (Node)o; parentNode3 != null; parentNode3 = parentNode3.getParentNode()) {
                ++n;
                if (parentNode3 == node4) {
                    return 1;
                }
                node2 = parentNode3;
            }
        }
        if (nodeType4 == 2) {
            n2 = 0;
            for (Node parentNode4 = node4; parentNode4 != null; parentNode4 = parentNode4.getParentNode()) {
                ++n2;
                if (parentNode4 == o) {
                    return 2;
                }
                node3 = parentNode4;
            }
        }
        if (node2 != node3) {
            return 0;
        }
        if (n > n2) {
            for (int i = 0; i < n - n2; ++i) {
                o = ((Node)o).getParentNode();
            }
            if (o == node4) {
                return 1;
            }
        }
        else {
            for (int j = 0; j < n2 - n; ++j) {
                node4 = node4.getParentNode();
            }
            if (node4 == o) {
                return 2;
            }
        }
        Node node5 = ((Node)o).getParentNode();
        for (Node node6 = node4.getParentNode(); node5 != node6; node5 = node5.getParentNode(), node6 = node6.getParentNode()) {
            o = node5;
            node4 = node6;
        }
        for (Node node7 = node5.getFirstChild(); node7 != null; node7 = node7.getNextSibling()) {
            if (node7 == node4) {
                return 1;
            }
            if (node7 == o) {
                return 2;
            }
        }
        return 0;
    }
    
    public short compareDocumentPosition(final Node node) throws DOMException {
        if (this == node) {
            return 0;
        }
        if (node != null && !(node instanceof NodeImpl)) {
            throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
        }
        Document ownerDocument;
        if (this.getNodeType() == 9) {
            ownerDocument = (Document)this;
        }
        else {
            ownerDocument = this.getOwnerDocument();
        }
        Document ownerDocument2;
        if (node.getNodeType() == 9) {
            ownerDocument2 = (Document)node;
        }
        else {
            ownerDocument2 = node.getOwnerDocument();
        }
        if (ownerDocument != ownerDocument2 && ownerDocument != null && ownerDocument2 != null) {
            if (((CoreDocumentImpl)ownerDocument2).getNodeNumber() > ((CoreDocumentImpl)ownerDocument).getNodeNumber()) {
                return 37;
            }
            return 35;
        }
        else {
            Node node2 = this;
            Node node3 = node;
            int n = 0;
            int n2 = 0;
            for (Node parentNode = this; parentNode != null; parentNode = parentNode.getParentNode()) {
                ++n;
                if (parentNode == node) {
                    return 10;
                }
                node2 = parentNode;
            }
            for (Node parentNode2 = node; parentNode2 != null; parentNode2 = parentNode2.getParentNode()) {
                ++n2;
                if (parentNode2 == this) {
                    return 20;
                }
                node3 = parentNode2;
            }
            final short nodeType = node2.getNodeType();
            final short nodeType2 = node3.getNodeType();
            Object o = this;
            Node node4 = node;
            Label_0538: {
                switch (nodeType) {
                    case 6:
                    case 12: {
                        final DocumentType doctype = ownerDocument.getDoctype();
                        if (doctype == node3) {
                            return 10;
                        }
                        switch (nodeType2) {
                            case 6:
                            case 12: {
                                if (nodeType != nodeType2) {
                                    return (short)((nodeType > nodeType2) ? 2 : 4);
                                }
                                if (nodeType == 12) {
                                    if (((NamedNodeMapImpl)doctype.getNotations()).precedes(node3, node2)) {
                                        return 34;
                                    }
                                    return 36;
                                }
                                else {
                                    if (((NamedNodeMapImpl)doctype.getEntities()).precedes(node3, node2)) {
                                        return 34;
                                    }
                                    return 36;
                                }
                                break;
                            }
                            default: {
                                node2 = (Node)(o = ownerDocument);
                                break Label_0538;
                            }
                        }
                        break;
                    }
                    case 10: {
                        if (node4 == ownerDocument) {
                            return 10;
                        }
                        if (ownerDocument != null && ownerDocument == ownerDocument2) {
                            return 4;
                        }
                        break;
                    }
                    case 2: {
                        o = ((AttrImpl)node2).getOwnerElement();
                        if (nodeType2 == 2) {
                            node4 = ((AttrImpl)node3).getOwnerElement();
                            if (node4 == o) {
                                if (((NamedNodeMapImpl)((Node)o).getAttributes()).precedes(node, this)) {
                                    return 34;
                                }
                                return 36;
                            }
                        }
                        n = 0;
                        for (Node parentNode3 = (Node)o; parentNode3 != null; parentNode3 = parentNode3.getParentNode()) {
                            ++n;
                            if (parentNode3 == node4) {
                                return 10;
                            }
                            node2 = parentNode3;
                        }
                        break;
                    }
                }
            }
            switch (nodeType2) {
                case 6:
                case 12: {
                    if (ownerDocument.getDoctype() == this) {
                        return 20;
                    }
                    node3 = (node4 = ownerDocument);
                    break;
                }
                case 10: {
                    if (o == ownerDocument2) {
                        return 20;
                    }
                    if (ownerDocument2 != null && ownerDocument == ownerDocument2) {
                        return 2;
                    }
                    break;
                }
                case 2: {
                    n2 = 0;
                    Node node5;
                    for (node4 = (node5 = ((AttrImpl)node3).getOwnerElement()); node5 != null; node5 = node5.getParentNode()) {
                        ++n2;
                        if (node5 == o) {
                            return 20;
                        }
                        node3 = node5;
                    }
                    break;
                }
            }
            if (node2 == node3) {
                if (n > n2) {
                    for (int i = 0; i < n - n2; ++i) {
                        o = ((Node)o).getParentNode();
                    }
                    if (o == node4) {
                        return 2;
                    }
                }
                else {
                    for (int j = 0; j < n2 - n; ++j) {
                        node4 = node4.getParentNode();
                    }
                    if (node4 == o) {
                        return 4;
                    }
                }
                Node node6 = ((Node)o).getParentNode();
                for (Node node7 = node4.getParentNode(); node6 != node7; node6 = node6.getParentNode(), node7 = node7.getParentNode()) {
                    o = node6;
                    node4 = node7;
                }
                for (Node node8 = node6.getFirstChild(); node8 != null; node8 = node8.getNextSibling()) {
                    if (node8 == node4) {
                        return 2;
                    }
                    if (node8 == o) {
                        return 4;
                    }
                }
                return 0;
            }
            if (((NodeImpl)node2).getNodeNumber() > ((NodeImpl)node3).getNodeNumber()) {
                return 37;
            }
            return 35;
        }
    }
    
    public String getTextContent() throws DOMException {
        return this.getNodeValue();
    }
    
    void getTextContent(final StringBuffer sb) throws DOMException {
        final String nodeValue = this.getNodeValue();
        if (nodeValue != null) {
            sb.append(nodeValue);
        }
    }
    
    public void setTextContent(final String nodeValue) throws DOMException {
        this.setNodeValue(nodeValue);
    }
    
    public boolean isSameNode(final Node node) {
        return this == node;
    }
    
    public boolean isDefaultNamespace(final String s) {
        switch (this.getNodeType()) {
            case 1: {
                final String namespaceURI = this.getNamespaceURI();
                final String prefix = this.getPrefix();
                if (prefix != null && prefix.length() != 0) {
                    if (this.hasAttributes()) {
                        final NodeImpl nodeImpl = (NodeImpl)((ElementImpl)this).getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
                        if (nodeImpl != null) {
                            final String nodeValue = nodeImpl.getNodeValue();
                            if (s == null) {
                                return namespaceURI == nodeValue;
                            }
                            return s.equals(nodeValue);
                        }
                    }
                    final NodeImpl nodeImpl2 = (NodeImpl)this.getElementAncestor(this);
                    return nodeImpl2 != null && nodeImpl2.isDefaultNamespace(s);
                }
                if (s == null) {
                    return namespaceURI == s;
                }
                return s.equals(namespaceURI);
            }
            case 9: {
                final Element documentElement = ((Document)this).getDocumentElement();
                return documentElement != null && documentElement.isDefaultNamespace(s);
            }
            case 6:
            case 10:
            case 11:
            case 12: {
                return false;
            }
            case 2: {
                return this.ownerNode.getNodeType() == 1 && this.ownerNode.isDefaultNamespace(s);
            }
            default: {
                final NodeImpl nodeImpl3 = (NodeImpl)this.getElementAncestor(this);
                return nodeImpl3 != null && nodeImpl3.isDefaultNamespace(s);
            }
        }
    }
    
    public String lookupPrefix(final String s) {
        if (s == null) {
            return null;
        }
        switch (this.getNodeType()) {
            case 1: {
                this.getNamespaceURI();
                return this.lookupNamespacePrefix(s, (ElementImpl)this);
            }
            case 9: {
                final Element documentElement = ((Document)this).getDocumentElement();
                if (documentElement != null) {
                    return documentElement.lookupPrefix(s);
                }
                return null;
            }
            case 6:
            case 10:
            case 11:
            case 12: {
                return null;
            }
            case 2: {
                if (this.ownerNode.getNodeType() == 1) {
                    return this.ownerNode.lookupPrefix(s);
                }
                return null;
            }
            default: {
                final NodeImpl nodeImpl = (NodeImpl)this.getElementAncestor(this);
                if (nodeImpl != null) {
                    return nodeImpl.lookupPrefix(s);
                }
                return null;
            }
        }
    }
    
    public String lookupNamespaceURI(final String s) {
        switch (this.getNodeType()) {
            case 1: {
                final String namespaceURI = this.getNamespaceURI();
                final String prefix = this.getPrefix();
                if (namespaceURI != null) {
                    if (s == null && prefix == s) {
                        return namespaceURI;
                    }
                    if (prefix != null && prefix.equals(s)) {
                        return namespaceURI;
                    }
                }
                if (this.hasAttributes()) {
                    final NamedNodeMap attributes = this.getAttributes();
                    for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                        final Node item = attributes.item(i);
                        final String namespaceURI2 = item.getNamespaceURI();
                        if (namespaceURI2 != null && namespaceURI2.equals("http://www.w3.org/2000/xmlns/")) {
                            final String prefix2 = item.getPrefix();
                            final String nodeValue = item.getNodeValue();
                            if (s == null && item.getNodeName().equals("xmlns")) {
                                return (nodeValue.length() > 0) ? nodeValue : null;
                            }
                            if (prefix2 != null && prefix2.equals("xmlns") && item.getLocalName().equals(s)) {
                                return (nodeValue.length() > 0) ? nodeValue : null;
                            }
                        }
                    }
                }
                final NodeImpl nodeImpl = (NodeImpl)this.getElementAncestor(this);
                if (nodeImpl != null) {
                    return nodeImpl.lookupNamespaceURI(s);
                }
                return null;
            }
            case 9: {
                final Element documentElement = ((Document)this).getDocumentElement();
                if (documentElement != null) {
                    return documentElement.lookupNamespaceURI(s);
                }
                return null;
            }
            case 6:
            case 10:
            case 11:
            case 12: {
                return null;
            }
            case 2: {
                if (this.ownerNode.getNodeType() == 1) {
                    return this.ownerNode.lookupNamespaceURI(s);
                }
                return null;
            }
            default: {
                final NodeImpl nodeImpl2 = (NodeImpl)this.getElementAncestor(this);
                if (nodeImpl2 != null) {
                    return nodeImpl2.lookupNamespaceURI(s);
                }
                return null;
            }
        }
    }
    
    Node getElementAncestor(final Node node) {
        for (Node node2 = node.getParentNode(); node2 != null; node2 = node2.getParentNode()) {
            if (node2.getNodeType() == 1) {
                return node2;
            }
        }
        return null;
    }
    
    String lookupNamespacePrefix(final String s, final ElementImpl elementImpl) {
        final String namespaceURI = this.getNamespaceURI();
        final String prefix = this.getPrefix();
        if (namespaceURI != null && namespaceURI.equals(s) && prefix != null) {
            final String lookupNamespaceURI = elementImpl.lookupNamespaceURI(prefix);
            if (lookupNamespaceURI != null && lookupNamespaceURI.equals(s)) {
                return prefix;
            }
        }
        if (this.hasAttributes()) {
            final NamedNodeMap attributes = this.getAttributes();
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                final Node item = attributes.item(i);
                final String namespaceURI2 = item.getNamespaceURI();
                if (namespaceURI2 != null && namespaceURI2.equals("http://www.w3.org/2000/xmlns/")) {
                    final String prefix2 = item.getPrefix();
                    final String nodeValue = item.getNodeValue();
                    if (item.getNodeName().equals("xmlns") || (prefix2 != null && prefix2.equals("xmlns") && nodeValue.equals(s))) {
                        final String localName = item.getLocalName();
                        final String lookupNamespaceURI2 = elementImpl.lookupNamespaceURI(localName);
                        if (lookupNamespaceURI2 != null && lookupNamespaceURI2.equals(s)) {
                            return localName;
                        }
                    }
                }
            }
        }
        final NodeImpl nodeImpl = (NodeImpl)this.getElementAncestor(this);
        if (nodeImpl != null) {
            return nodeImpl.lookupNamespacePrefix(s, elementImpl);
        }
        return null;
    }
    
    public boolean isEqualNode(final Node node) {
        if (node == this) {
            return true;
        }
        if (node.getNodeType() != this.getNodeType()) {
            return false;
        }
        if (this.getNodeName() == null) {
            if (node.getNodeName() != null) {
                return false;
            }
        }
        else if (!this.getNodeName().equals(node.getNodeName())) {
            return false;
        }
        if (this.getLocalName() == null) {
            if (node.getLocalName() != null) {
                return false;
            }
        }
        else if (!this.getLocalName().equals(node.getLocalName())) {
            return false;
        }
        if (this.getNamespaceURI() == null) {
            if (node.getNamespaceURI() != null) {
                return false;
            }
        }
        else if (!this.getNamespaceURI().equals(node.getNamespaceURI())) {
            return false;
        }
        if (this.getPrefix() == null) {
            if (node.getPrefix() != null) {
                return false;
            }
        }
        else if (!this.getPrefix().equals(node.getPrefix())) {
            return false;
        }
        if (this.getNodeValue() == null) {
            if (node.getNodeValue() != null) {
                return false;
            }
        }
        else if (!this.getNodeValue().equals(node.getNodeValue())) {
            return false;
        }
        return true;
    }
    
    public Object getFeature(final String s, final String s2) {
        return this.isSupported(s, s2) ? this : null;
    }
    
    public Object setUserData(final String s, final Object o, final UserDataHandler userDataHandler) {
        return this.ownerDocument().setUserData(this, s, o, userDataHandler);
    }
    
    public Object getUserData(final String s) {
        return this.ownerDocument().getUserData(this, s);
    }
    
    protected Hashtable getUserDataRecord() {
        return this.ownerDocument().getUserDataRecord(this);
    }
    
    public void setReadOnly(final boolean b, final boolean b2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isReadOnly(b);
    }
    
    public boolean getReadOnly() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.isReadOnly();
    }
    
    public void setUserData(final Object o) {
        this.ownerDocument().setUserData(this, o);
    }
    
    public Object getUserData() {
        return this.ownerDocument().getUserData(this);
    }
    
    protected void changed() {
        this.ownerDocument().changed();
    }
    
    protected int changes() {
        return this.ownerDocument().changes();
    }
    
    protected void synchronizeData() {
        this.needsSyncData(false);
    }
    
    protected Node getContainer() {
        return null;
    }
    
    final boolean isReadOnly() {
        return (this.flags & 0x1) != 0x0;
    }
    
    final void isReadOnly(final boolean b) {
        this.flags = (short)(b ? (this.flags | 0x1) : (this.flags & 0xFFFFFFFE));
    }
    
    final boolean needsSyncData() {
        return (this.flags & 0x2) != 0x0;
    }
    
    final void needsSyncData(final boolean b) {
        this.flags = (short)(b ? (this.flags | 0x2) : (this.flags & 0xFFFFFFFD));
    }
    
    final boolean needsSyncChildren() {
        return (this.flags & 0x4) != 0x0;
    }
    
    public final void needsSyncChildren(final boolean b) {
        this.flags = (short)(b ? (this.flags | 0x4) : (this.flags & 0xFFFFFFFB));
    }
    
    final boolean isOwned() {
        return (this.flags & 0x8) != 0x0;
    }
    
    final void isOwned(final boolean b) {
        this.flags = (short)(b ? (this.flags | 0x8) : (this.flags & 0xFFFFFFF7));
    }
    
    final boolean isFirstChild() {
        return (this.flags & 0x10) != 0x0;
    }
    
    final void isFirstChild(final boolean b) {
        this.flags = (short)(b ? (this.flags | 0x10) : (this.flags & 0xFFFFFFEF));
    }
    
    final boolean isSpecified() {
        return (this.flags & 0x20) != 0x0;
    }
    
    final void isSpecified(final boolean b) {
        this.flags = (short)(b ? (this.flags | 0x20) : (this.flags & 0xFFFFFFDF));
    }
    
    final boolean internalIsIgnorableWhitespace() {
        return (this.flags & 0x40) != 0x0;
    }
    
    final void isIgnorableWhitespace(final boolean b) {
        this.flags = (short)(b ? (this.flags | 0x40) : (this.flags & 0xFFFFFFBF));
    }
    
    final boolean hasStringValue() {
        return (this.flags & 0x80) != 0x0;
    }
    
    final void hasStringValue(final boolean b) {
        this.flags = (short)(b ? (this.flags | 0x80) : (this.flags & 0xFFFFFF7F));
    }
    
    final boolean isNormalized() {
        return (this.flags & 0x100) != 0x0;
    }
    
    final void isNormalized(final boolean b) {
        if (!b && this.isNormalized() && this.ownerNode != null) {
            this.ownerNode.isNormalized(false);
        }
        this.flags = (short)(b ? (this.flags | 0x100) : (this.flags & 0xFFFFFEFF));
    }
    
    final boolean isIdAttribute() {
        return (this.flags & 0x200) != 0x0;
    }
    
    final void isIdAttribute(final boolean b) {
        this.flags = (short)(b ? (this.flags | 0x200) : (this.flags & 0xFFFFFDFF));
    }
    
    public String toString() {
        return "[" + this.getNodeName() + ": " + this.getNodeValue() + "]";
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        objectOutputStream.defaultWriteObject();
    }
}
