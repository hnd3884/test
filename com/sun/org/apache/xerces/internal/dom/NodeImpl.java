package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import org.w3c.dom.UserDataHandler;
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
    
    protected NodeImpl(final CoreDocumentImpl ownerDocument) {
        this.ownerNode = ownerDocument;
    }
    
    public NodeImpl() {
    }
    
    @Override
    public abstract short getNodeType();
    
    @Override
    public abstract String getNodeName();
    
    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }
    
    @Override
    public void setNodeValue(final String x) throws DOMException {
    }
    
    @Override
    public Node appendChild(final Node newChild) throws DOMException {
        return this.insertBefore(newChild, null);
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        NodeImpl newnode;
        try {
            newnode = (NodeImpl)this.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException("**Internal Error**" + e);
        }
        newnode.ownerNode = this.ownerDocument();
        newnode.isOwned(false);
        newnode.isReadOnly(false);
        this.ownerDocument().callUserDataHandlers(this, newnode, (short)1);
        return newnode;
    }
    
    @Override
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
    
    void setOwnerDocument(final CoreDocumentImpl doc) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (!this.isOwned()) {
            this.ownerNode = doc;
        }
    }
    
    protected int getNodeNumber() {
        final CoreDocumentImpl cd = (CoreDocumentImpl)this.getOwnerDocument();
        final int nodeNumber = cd.getNodeNumber(this);
        return nodeNumber;
    }
    
    @Override
    public Node getParentNode() {
        return null;
    }
    
    NodeImpl parentNode() {
        return null;
    }
    
    @Override
    public Node getNextSibling() {
        return null;
    }
    
    @Override
    public Node getPreviousSibling() {
        return null;
    }
    
    ChildNode previousSibling() {
        return null;
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }
    
    @Override
    public boolean hasAttributes() {
        return false;
    }
    
    @Override
    public boolean hasChildNodes() {
        return false;
    }
    
    @Override
    public NodeList getChildNodes() {
        return this;
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
    public Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
        throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
    }
    
    @Override
    public Node removeChild(final Node oldChild) throws DOMException {
        throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
    }
    
    @Override
    public Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
        throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
    }
    
    @Override
    public int getLength() {
        return 0;
    }
    
    @Override
    public Node item(final int index) {
        return null;
    }
    
    @Override
    public void normalize() {
    }
    
    @Override
    public boolean isSupported(final String feature, final String version) {
        return this.ownerDocument().getImplementation().hasFeature(feature, version);
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
    public void setPrefix(final String prefix) throws DOMException {
        throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
    }
    
    @Override
    public String getLocalName() {
        return null;
    }
    
    @Override
    public void addEventListener(final String type, final EventListener listener, final boolean useCapture) {
        this.ownerDocument().addEventListener(this, type, listener, useCapture);
    }
    
    @Override
    public void removeEventListener(final String type, final EventListener listener, final boolean useCapture) {
        this.ownerDocument().removeEventListener(this, type, listener, useCapture);
    }
    
    @Override
    public boolean dispatchEvent(final Event event) {
        return this.ownerDocument().dispatchEvent(this, event);
    }
    
    @Override
    public String getBaseURI() {
        return null;
    }
    
    @Deprecated
    public short compareTreePosition(final Node other) {
        if (this == other) {
            return 48;
        }
        final short thisType = this.getNodeType();
        final short otherType = other.getNodeType();
        if (thisType == 6 || thisType == 12 || otherType == 6 || otherType == 12) {
            return 0;
        }
        Node thisAncestor = this;
        Node otherAncestor = other;
        int thisDepth = 0;
        int otherDepth = 0;
        for (Node node = this; node != null; node = node.getParentNode()) {
            ++thisDepth;
            if (node == other) {
                return 5;
            }
            thisAncestor = node;
        }
        for (Node node = other; node != null; node = node.getParentNode()) {
            ++otherDepth;
            if (node == this) {
                return 10;
            }
            otherAncestor = node;
        }
        Node thisNode = this;
        Node otherNode = other;
        final int thisAncestorType = thisAncestor.getNodeType();
        final int otherAncestorType = otherAncestor.getNodeType();
        if (thisAncestorType == 2) {
            thisNode = ((AttrImpl)thisAncestor).getOwnerElement();
        }
        if (otherAncestorType == 2) {
            otherNode = ((AttrImpl)otherAncestor).getOwnerElement();
        }
        if (thisAncestorType == 2 && otherAncestorType == 2 && thisNode == otherNode) {
            return 16;
        }
        if (thisAncestorType == 2) {
            thisDepth = 0;
            for (Node node = thisNode; node != null; node = node.getParentNode()) {
                ++thisDepth;
                if (node == otherNode) {
                    return 1;
                }
                thisAncestor = node;
            }
        }
        if (otherAncestorType == 2) {
            otherDepth = 0;
            for (Node node = otherNode; node != null; node = node.getParentNode()) {
                ++otherDepth;
                if (node == thisNode) {
                    return 2;
                }
                otherAncestor = node;
            }
        }
        if (thisAncestor != otherAncestor) {
            return 0;
        }
        if (thisDepth > otherDepth) {
            for (int i = 0; i < thisDepth - otherDepth; ++i) {
                thisNode = thisNode.getParentNode();
            }
            if (thisNode == otherNode) {
                return 1;
            }
        }
        else {
            for (int i = 0; i < otherDepth - thisDepth; ++i) {
                otherNode = otherNode.getParentNode();
            }
            if (otherNode == thisNode) {
                return 2;
            }
        }
        Node thisNodeP = thisNode.getParentNode();
        for (Node otherNodeP = otherNode.getParentNode(); thisNodeP != otherNodeP; thisNodeP = thisNodeP.getParentNode(), otherNodeP = otherNodeP.getParentNode()) {
            thisNode = thisNodeP;
            otherNode = otherNodeP;
        }
        for (Node current = thisNodeP.getFirstChild(); current != null; current = current.getNextSibling()) {
            if (current == otherNode) {
                return 1;
            }
            if (current == thisNode) {
                return 2;
            }
        }
        return 0;
    }
    
    @Override
    public short compareDocumentPosition(final Node other) throws DOMException {
        if (this == other) {
            return 0;
        }
        try {
            final NodeImpl nodeImpl = (NodeImpl)other;
        }
        catch (final ClassCastException e) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException((short)9, msg);
        }
        Document thisOwnerDoc;
        if (this.getNodeType() == 9) {
            thisOwnerDoc = (Document)this;
        }
        else {
            thisOwnerDoc = this.getOwnerDocument();
        }
        Document otherOwnerDoc;
        if (other.getNodeType() == 9) {
            otherOwnerDoc = (Document)other;
        }
        else {
            otherOwnerDoc = other.getOwnerDocument();
        }
        if (thisOwnerDoc != otherOwnerDoc && thisOwnerDoc != null && otherOwnerDoc != null) {
            final int otherDocNum = ((CoreDocumentImpl)otherOwnerDoc).getNodeNumber();
            final int thisDocNum = ((CoreDocumentImpl)thisOwnerDoc).getNodeNumber();
            if (otherDocNum > thisDocNum) {
                return 37;
            }
            return 35;
        }
        else {
            Node thisAncestor = this;
            Node otherAncestor = other;
            int thisDepth = 0;
            int otherDepth = 0;
            for (Node node = this; node != null; node = node.getParentNode()) {
                ++thisDepth;
                if (node == other) {
                    return 10;
                }
                thisAncestor = node;
            }
            for (Node node = other; node != null; node = node.getParentNode()) {
                ++otherDepth;
                if (node == this) {
                    return 20;
                }
                otherAncestor = node;
            }
            final int thisAncestorType = thisAncestor.getNodeType();
            final int otherAncestorType = otherAncestor.getNodeType();
            Node thisNode = this;
            Node otherNode = other;
            Label_0534: {
                switch (thisAncestorType) {
                    case 6:
                    case 12: {
                        final DocumentType container = thisOwnerDoc.getDoctype();
                        if (container == otherAncestor) {
                            return 10;
                        }
                        switch (otherAncestorType) {
                            case 6:
                            case 12: {
                                if (thisAncestorType != otherAncestorType) {
                                    return (short)((thisAncestorType > otherAncestorType) ? 2 : 4);
                                }
                                if (thisAncestorType == 12) {
                                    if (((NamedNodeMapImpl)container.getNotations()).precedes(otherAncestor, thisAncestor)) {
                                        return 34;
                                    }
                                    return 36;
                                }
                                else {
                                    if (((NamedNodeMapImpl)container.getEntities()).precedes(otherAncestor, thisAncestor)) {
                                        return 34;
                                    }
                                    return 36;
                                }
                                break;
                            }
                            default: {
                                thisAncestor = (thisNode = thisOwnerDoc);
                                break Label_0534;
                            }
                        }
                        break;
                    }
                    case 10: {
                        if (otherNode == thisOwnerDoc) {
                            return 10;
                        }
                        if (thisOwnerDoc != null && thisOwnerDoc == otherOwnerDoc) {
                            return 4;
                        }
                        break;
                    }
                    case 2: {
                        thisNode = ((AttrImpl)thisAncestor).getOwnerElement();
                        if (otherAncestorType == 2) {
                            otherNode = ((AttrImpl)otherAncestor).getOwnerElement();
                            if (otherNode == thisNode) {
                                if (((NamedNodeMapImpl)thisNode.getAttributes()).precedes(other, this)) {
                                    return 34;
                                }
                                return 36;
                            }
                        }
                        thisDepth = 0;
                        for (Node node = thisNode; node != null; node = node.getParentNode()) {
                            ++thisDepth;
                            if (node == otherNode) {
                                return 10;
                            }
                            thisAncestor = node;
                        }
                        break;
                    }
                }
            }
            switch (otherAncestorType) {
                case 6:
                case 12: {
                    final DocumentType container = thisOwnerDoc.getDoctype();
                    if (container == this) {
                        return 20;
                    }
                    otherAncestor = (otherNode = thisOwnerDoc);
                    break;
                }
                case 10: {
                    if (thisNode == otherOwnerDoc) {
                        return 20;
                    }
                    if (otherOwnerDoc != null && thisOwnerDoc == otherOwnerDoc) {
                        return 2;
                    }
                    break;
                }
                case 2: {
                    otherDepth = 0;
                    Node node;
                    for (otherNode = (node = ((AttrImpl)otherAncestor).getOwnerElement()); node != null; node = node.getParentNode()) {
                        ++otherDepth;
                        if (node == thisNode) {
                            return 20;
                        }
                        otherAncestor = node;
                    }
                    break;
                }
            }
            if (thisAncestor == otherAncestor) {
                if (thisDepth > otherDepth) {
                    for (int i = 0; i < thisDepth - otherDepth; ++i) {
                        thisNode = thisNode.getParentNode();
                    }
                    if (thisNode == otherNode) {
                        return 2;
                    }
                }
                else {
                    for (int i = 0; i < otherDepth - thisDepth; ++i) {
                        otherNode = otherNode.getParentNode();
                    }
                    if (otherNode == thisNode) {
                        return 4;
                    }
                }
                Node thisNodeP = thisNode.getParentNode();
                for (Node otherNodeP = otherNode.getParentNode(); thisNodeP != otherNodeP; thisNodeP = thisNodeP.getParentNode(), otherNodeP = otherNodeP.getParentNode()) {
                    thisNode = thisNodeP;
                    otherNode = otherNodeP;
                }
                for (Node current = thisNodeP.getFirstChild(); current != null; current = current.getNextSibling()) {
                    if (current == otherNode) {
                        return 2;
                    }
                    if (current == thisNode) {
                        return 4;
                    }
                }
                return 0;
            }
            final int thisAncestorNum = ((NodeImpl)thisAncestor).getNodeNumber();
            final int otherAncestorNum = ((NodeImpl)otherAncestor).getNodeNumber();
            if (thisAncestorNum > otherAncestorNum) {
                return 37;
            }
            return 35;
        }
    }
    
    @Override
    public String getTextContent() throws DOMException {
        return this.getNodeValue();
    }
    
    void getTextContent(final StringBuffer buf) throws DOMException {
        final String content = this.getNodeValue();
        if (content != null) {
            buf.append(content);
        }
    }
    
    @Override
    public void setTextContent(final String textContent) throws DOMException {
        this.setNodeValue(textContent);
    }
    
    @Override
    public boolean isSameNode(final Node other) {
        return this == other;
    }
    
    @Override
    public boolean isDefaultNamespace(final String namespaceURI) {
        final short type = this.getNodeType();
        switch (type) {
            case 1: {
                final String namespace = this.getNamespaceURI();
                final String prefix = this.getPrefix();
                if (prefix != null && prefix.length() != 0) {
                    if (this.hasAttributes()) {
                        final ElementImpl elem = (ElementImpl)this;
                        final NodeImpl attr = (NodeImpl)elem.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
                        if (attr != null) {
                            final String value = attr.getNodeValue();
                            if (namespaceURI == null) {
                                return namespace == value;
                            }
                            return namespaceURI.equals(value);
                        }
                    }
                    final NodeImpl ancestor = (NodeImpl)this.getElementAncestor(this);
                    return ancestor != null && ancestor.isDefaultNamespace(namespaceURI);
                }
                if (namespaceURI == null) {
                    return namespace == namespaceURI;
                }
                return namespaceURI.equals(namespace);
            }
            case 9: {
                return ((NodeImpl)((Document)this).getDocumentElement()).isDefaultNamespace(namespaceURI);
            }
            case 6:
            case 10:
            case 11:
            case 12: {
                return false;
            }
            case 2: {
                return this.ownerNode.getNodeType() == 1 && this.ownerNode.isDefaultNamespace(namespaceURI);
            }
            default: {
                final NodeImpl ancestor2 = (NodeImpl)this.getElementAncestor(this);
                return ancestor2 != null && ancestor2.isDefaultNamespace(namespaceURI);
            }
        }
    }
    
    @Override
    public String lookupPrefix(final String namespaceURI) {
        if (namespaceURI == null) {
            return null;
        }
        final short type = this.getNodeType();
        switch (type) {
            case 1: {
                final String namespace = this.getNamespaceURI();
                return this.lookupNamespacePrefix(namespaceURI, (ElementImpl)this);
            }
            case 9: {
                return ((NodeImpl)((Document)this).getDocumentElement()).lookupPrefix(namespaceURI);
            }
            case 6:
            case 10:
            case 11:
            case 12: {
                return null;
            }
            case 2: {
                if (this.ownerNode.getNodeType() == 1) {
                    return this.ownerNode.lookupPrefix(namespaceURI);
                }
                return null;
            }
            default: {
                final NodeImpl ancestor = (NodeImpl)this.getElementAncestor(this);
                if (ancestor != null) {
                    return ancestor.lookupPrefix(namespaceURI);
                }
                return null;
            }
        }
    }
    
    @Override
    public String lookupNamespaceURI(final String specifiedPrefix) {
        final short type = this.getNodeType();
        switch (type) {
            case 1: {
                String namespace = this.getNamespaceURI();
                final String prefix = this.getPrefix();
                if (namespace != null) {
                    if (specifiedPrefix == null && prefix == specifiedPrefix) {
                        return namespace;
                    }
                    if (prefix != null && prefix.equals(specifiedPrefix)) {
                        return namespace;
                    }
                }
                if (this.hasAttributes()) {
                    final NamedNodeMap map = this.getAttributes();
                    for (int length = map.getLength(), i = 0; i < length; ++i) {
                        final Node attr = map.item(i);
                        final String attrPrefix = attr.getPrefix();
                        final String value = attr.getNodeValue();
                        namespace = attr.getNamespaceURI();
                        if (namespace != null && namespace.equals("http://www.w3.org/2000/xmlns/")) {
                            if (specifiedPrefix == null && attr.getNodeName().equals("xmlns")) {
                                return value;
                            }
                            if (attrPrefix != null && attrPrefix.equals("xmlns") && attr.getLocalName().equals(specifiedPrefix)) {
                                return value;
                            }
                        }
                    }
                }
                final NodeImpl ancestor = (NodeImpl)this.getElementAncestor(this);
                if (ancestor != null) {
                    return ancestor.lookupNamespaceURI(specifiedPrefix);
                }
                return null;
            }
            case 9: {
                return ((NodeImpl)((Document)this).getDocumentElement()).lookupNamespaceURI(specifiedPrefix);
            }
            case 6:
            case 10:
            case 11:
            case 12: {
                return null;
            }
            case 2: {
                if (this.ownerNode.getNodeType() == 1) {
                    return this.ownerNode.lookupNamespaceURI(specifiedPrefix);
                }
                return null;
            }
            default: {
                final NodeImpl ancestor2 = (NodeImpl)this.getElementAncestor(this);
                if (ancestor2 != null) {
                    return ancestor2.lookupNamespaceURI(specifiedPrefix);
                }
                return null;
            }
        }
    }
    
    Node getElementAncestor(final Node currentNode) {
        final Node parent = currentNode.getParentNode();
        if (parent == null) {
            return null;
        }
        final short type = parent.getNodeType();
        if (type == 1) {
            return parent;
        }
        return this.getElementAncestor(parent);
    }
    
    String lookupNamespacePrefix(final String namespaceURI, final ElementImpl el) {
        String namespace = this.getNamespaceURI();
        final String prefix = this.getPrefix();
        if (namespace != null && namespace.equals(namespaceURI) && prefix != null) {
            final String foundNamespace = el.lookupNamespaceURI(prefix);
            if (foundNamespace != null && foundNamespace.equals(namespaceURI)) {
                return prefix;
            }
        }
        if (this.hasAttributes()) {
            final NamedNodeMap map = this.getAttributes();
            for (int length = map.getLength(), i = 0; i < length; ++i) {
                final Node attr = map.item(i);
                final String attrPrefix = attr.getPrefix();
                final String value = attr.getNodeValue();
                namespace = attr.getNamespaceURI();
                if (namespace != null && namespace.equals("http://www.w3.org/2000/xmlns/") && (attr.getNodeName().equals("xmlns") || (attrPrefix != null && attrPrefix.equals("xmlns") && value.equals(namespaceURI)))) {
                    final String localname = attr.getLocalName();
                    final String foundNamespace2 = el.lookupNamespaceURI(localname);
                    if (foundNamespace2 != null && foundNamespace2.equals(namespaceURI)) {
                        return localname;
                    }
                }
            }
        }
        final NodeImpl ancestor = (NodeImpl)this.getElementAncestor(this);
        if (ancestor != null) {
            return ancestor.lookupNamespacePrefix(namespaceURI, el);
        }
        return null;
    }
    
    @Override
    public boolean isEqualNode(final Node arg) {
        if (arg == this) {
            return true;
        }
        if (arg.getNodeType() != this.getNodeType()) {
            return false;
        }
        if (this.getNodeName() == null) {
            if (arg.getNodeName() != null) {
                return false;
            }
        }
        else if (!this.getNodeName().equals(arg.getNodeName())) {
            return false;
        }
        if (this.getLocalName() == null) {
            if (arg.getLocalName() != null) {
                return false;
            }
        }
        else if (!this.getLocalName().equals(arg.getLocalName())) {
            return false;
        }
        if (this.getNamespaceURI() == null) {
            if (arg.getNamespaceURI() != null) {
                return false;
            }
        }
        else if (!this.getNamespaceURI().equals(arg.getNamespaceURI())) {
            return false;
        }
        if (this.getPrefix() == null) {
            if (arg.getPrefix() != null) {
                return false;
            }
        }
        else if (!this.getPrefix().equals(arg.getPrefix())) {
            return false;
        }
        if (this.getNodeValue() == null) {
            if (arg.getNodeValue() != null) {
                return false;
            }
        }
        else if (!this.getNodeValue().equals(arg.getNodeValue())) {
            return false;
        }
        return true;
    }
    
    @Override
    public Object getFeature(final String feature, final String version) {
        return this.isSupported(feature, version) ? this : null;
    }
    
    @Override
    public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
        return this.ownerDocument().setUserData(this, key, data, handler);
    }
    
    @Override
    public Object getUserData(final String key) {
        return this.ownerDocument().getUserData(this, key);
    }
    
    protected Map<String, ParentNode.UserDataRecord> getUserDataRecord() {
        return this.ownerDocument().getUserDataRecord(this);
    }
    
    public void setReadOnly(final boolean readOnly, final boolean deep) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isReadOnly(readOnly);
    }
    
    public boolean getReadOnly() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.isReadOnly();
    }
    
    public void setUserData(final Object data) {
        this.ownerDocument().setUserData(this, data);
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
    
    final void isReadOnly(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x1) : (this.flags & 0xFFFFFFFE));
    }
    
    final boolean needsSyncData() {
        return (this.flags & 0x2) != 0x0;
    }
    
    final void needsSyncData(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x2) : (this.flags & 0xFFFFFFFD));
    }
    
    final boolean needsSyncChildren() {
        return (this.flags & 0x4) != 0x0;
    }
    
    public final void needsSyncChildren(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x4) : (this.flags & 0xFFFFFFFB));
    }
    
    final boolean isOwned() {
        return (this.flags & 0x8) != 0x0;
    }
    
    final void isOwned(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x8) : (this.flags & 0xFFFFFFF7));
    }
    
    final boolean isFirstChild() {
        return (this.flags & 0x10) != 0x0;
    }
    
    final void isFirstChild(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x10) : (this.flags & 0xFFFFFFEF));
    }
    
    final boolean isSpecified() {
        return (this.flags & 0x20) != 0x0;
    }
    
    final void isSpecified(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x20) : (this.flags & 0xFFFFFFDF));
    }
    
    final boolean internalIsIgnorableWhitespace() {
        return (this.flags & 0x40) != 0x0;
    }
    
    final void isIgnorableWhitespace(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x40) : (this.flags & 0xFFFFFFBF));
    }
    
    final boolean hasStringValue() {
        return (this.flags & 0x80) != 0x0;
    }
    
    final void hasStringValue(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x80) : (this.flags & 0xFFFFFF7F));
    }
    
    final boolean isNormalized() {
        return (this.flags & 0x100) != 0x0;
    }
    
    final void isNormalized(final boolean value) {
        if (!value && this.isNormalized() && this.ownerNode != null) {
            this.ownerNode.isNormalized(false);
        }
        this.flags = (short)(value ? (this.flags | 0x100) : (this.flags & 0xFFFFFEFF));
    }
    
    final boolean isIdAttribute() {
        return (this.flags & 0x200) != 0x0;
    }
    
    final void isIdAttribute(final boolean value) {
        this.flags = (short)(value ? (this.flags | 0x200) : (this.flags & 0xFFFFFDFF));
    }
    
    @Override
    public String toString() {
        return "[" + this.getNodeName() + ": " + this.getNodeValue() + "]";
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        out.defaultWriteObject();
    }
}
