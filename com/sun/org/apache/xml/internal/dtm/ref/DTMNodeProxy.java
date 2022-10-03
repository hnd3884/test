package com.sun.org.apache.xml.internal.dtm.ref;

import org.w3c.dom.TypeInfo;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.UserDataHandler;
import com.sun.org.apache.xpath.internal.NodeSet;
import java.util.Vector;
import org.w3c.dom.EntityReference;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import com.sun.org.apache.xml.internal.dtm.DTMDOMException;
import java.util.Objects;
import org.w3c.dom.DOMImplementation;
import com.sun.org.apache.xml.internal.dtm.DTM;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Comment;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DTMNodeProxy implements Node, Document, Text, Element, Attr, ProcessingInstruction, Comment, DocumentFragment
{
    public DTM dtm;
    int node;
    private static final String EMPTYSTRING = "";
    static final DOMImplementation implementation;
    protected String fDocumentURI;
    protected String actualEncoding;
    private String xmlEncoding;
    private boolean xmlStandalone;
    private String xmlVersion;
    
    public DTMNodeProxy(final DTM dtm, final int node) {
        this.dtm = dtm;
        this.node = node;
    }
    
    public final DTM getDTM() {
        return this.dtm;
    }
    
    public final int getDTMNodeNumber() {
        return this.node;
    }
    
    public final boolean equals(final Node node) {
        try {
            final DTMNodeProxy dtmp = (DTMNodeProxy)node;
            return dtmp.node == this.node && dtmp.dtm == this.dtm;
        }
        catch (final ClassCastException cce) {
            return false;
        }
    }
    
    @Override
    public final boolean equals(final Object node) {
        return node instanceof Node && this.equals((Node)node);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.dtm);
        hash = 29 * hash + this.node;
        return hash;
    }
    
    public final boolean sameNodeAs(final Node other) {
        if (!(other instanceof DTMNodeProxy)) {
            return false;
        }
        final DTMNodeProxy that = (DTMNodeProxy)other;
        return this.dtm == that.dtm && this.node == that.node;
    }
    
    @Override
    public final String getNodeName() {
        return this.dtm.getNodeName(this.node);
    }
    
    @Override
    public final String getTarget() {
        return this.dtm.getNodeName(this.node);
    }
    
    @Override
    public final String getLocalName() {
        return this.dtm.getLocalName(this.node);
    }
    
    @Override
    public final String getPrefix() {
        return this.dtm.getPrefix(this.node);
    }
    
    @Override
    public final void setPrefix(final String prefix) throws DOMException {
        throw new DTMDOMException((short)7);
    }
    
    @Override
    public final String getNamespaceURI() {
        return this.dtm.getNamespaceURI(this.node);
    }
    
    public final boolean supports(final String feature, final String version) {
        return DTMNodeProxy.implementation.hasFeature(feature, version);
    }
    
    @Override
    public final boolean isSupported(final String feature, final String version) {
        return DTMNodeProxy.implementation.hasFeature(feature, version);
    }
    
    @Override
    public final String getNodeValue() throws DOMException {
        return this.dtm.getNodeValue(this.node);
    }
    
    public final String getStringValue() throws DOMException {
        return this.dtm.getStringValue(this.node).toString();
    }
    
    @Override
    public final void setNodeValue(final String nodeValue) throws DOMException {
        throw new DTMDOMException((short)7);
    }
    
    @Override
    public final short getNodeType() {
        return this.dtm.getNodeType(this.node);
    }
    
    @Override
    public final Node getParentNode() {
        if (this.getNodeType() == 2) {
            return null;
        }
        final int newnode = this.dtm.getParent(this.node);
        return (newnode == -1) ? null : this.dtm.getNode(newnode);
    }
    
    public final Node getOwnerNode() {
        final int newnode = this.dtm.getParent(this.node);
        return (newnode == -1) ? null : this.dtm.getNode(newnode);
    }
    
    @Override
    public final NodeList getChildNodes() {
        return new DTMChildIterNodeList(this.dtm, this.node);
    }
    
    @Override
    public final Node getFirstChild() {
        final int newnode = this.dtm.getFirstChild(this.node);
        return (newnode == -1) ? null : this.dtm.getNode(newnode);
    }
    
    @Override
    public final Node getLastChild() {
        final int newnode = this.dtm.getLastChild(this.node);
        return (newnode == -1) ? null : this.dtm.getNode(newnode);
    }
    
    @Override
    public final Node getPreviousSibling() {
        final int newnode = this.dtm.getPreviousSibling(this.node);
        return (newnode == -1) ? null : this.dtm.getNode(newnode);
    }
    
    @Override
    public final Node getNextSibling() {
        if (this.dtm.getNodeType(this.node) == 2) {
            return null;
        }
        final int newnode = this.dtm.getNextSibling(this.node);
        return (newnode == -1) ? null : this.dtm.getNode(newnode);
    }
    
    @Override
    public final NamedNodeMap getAttributes() {
        return new DTMNamedNodeMap(this.dtm, this.node);
    }
    
    @Override
    public boolean hasAttribute(final String name) {
        return -1 != this.dtm.getAttributeNode(this.node, null, name);
    }
    
    @Override
    public boolean hasAttributeNS(final String namespaceURI, final String localName) {
        return -1 != this.dtm.getAttributeNode(this.node, namespaceURI, localName);
    }
    
    @Override
    public final Document getOwnerDocument() {
        return (Document)this.dtm.getNode(this.dtm.getOwnerDocument(this.node));
    }
    
    @Override
    public final Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
        throw new DTMDOMException((short)7);
    }
    
    @Override
    public final Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
        throw new DTMDOMException((short)7);
    }
    
    @Override
    public final Node removeChild(final Node oldChild) throws DOMException {
        throw new DTMDOMException((short)7);
    }
    
    @Override
    public final Node appendChild(final Node newChild) throws DOMException {
        throw new DTMDOMException((short)7);
    }
    
    @Override
    public final boolean hasChildNodes() {
        return -1 != this.dtm.getFirstChild(this.node);
    }
    
    @Override
    public final Node cloneNode(final boolean deep) {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final DocumentType getDoctype() {
        return null;
    }
    
    @Override
    public final DOMImplementation getImplementation() {
        return DTMNodeProxy.implementation;
    }
    
    @Override
    public final Element getDocumentElement() {
        final int dochandle = this.dtm.getDocument();
        int elementhandle = -1;
        for (int kidhandle = this.dtm.getFirstChild(dochandle); kidhandle != -1; kidhandle = this.dtm.getNextSibling(kidhandle)) {
            switch (this.dtm.getNodeType(kidhandle)) {
                case 1: {
                    if (elementhandle != -1) {
                        elementhandle = -1;
                        kidhandle = this.dtm.getLastChild(dochandle);
                        break;
                    }
                    elementhandle = kidhandle;
                    break;
                }
                case 7:
                case 8:
                case 10: {
                    break;
                }
                default: {
                    elementhandle = -1;
                    kidhandle = this.dtm.getLastChild(dochandle);
                    break;
                }
            }
        }
        if (elementhandle == -1) {
            throw new DTMDOMException((short)9);
        }
        return (Element)this.dtm.getNode(elementhandle);
    }
    
    @Override
    public final Element createElement(final String tagName) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final DocumentFragment createDocumentFragment() {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final Text createTextNode(final String data) {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final Comment createComment(final String data) {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final CDATASection createCDATASection(final String data) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final Attr createAttribute(final String name) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final EntityReference createEntityReference(final String name) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final NodeList getElementsByTagName(final String tagname) {
        final Vector listVector = new Vector();
        final Node retNode = this.dtm.getNode(this.node);
        if (retNode != null) {
            final boolean isTagNameWildCard = "*".equals(tagname);
            if (1 == retNode.getNodeType()) {
                final NodeList nodeList = retNode.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    this.traverseChildren(listVector, nodeList.item(i), tagname, isTagNameWildCard);
                }
            }
            else if (9 == retNode.getNodeType()) {
                this.traverseChildren(listVector, this.dtm.getNode(this.node), tagname, isTagNameWildCard);
            }
        }
        final int size = listVector.size();
        final NodeSet nodeSet = new NodeSet(size);
        for (int i = 0; i < size; ++i) {
            nodeSet.addNode(listVector.elementAt(i));
        }
        return nodeSet;
    }
    
    private final void traverseChildren(final Vector listVector, final Node tempNode, final String tagname, final boolean isTagNameWildCard) {
        if (tempNode == null) {
            return;
        }
        if (tempNode.getNodeType() == 1 && (isTagNameWildCard || tempNode.getNodeName().equals(tagname))) {
            listVector.add(tempNode);
        }
        if (tempNode.hasChildNodes()) {
            final NodeList nodeList = tempNode.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                this.traverseChildren(listVector, nodeList.item(i), tagname, isTagNameWildCard);
            }
        }
    }
    
    @Override
    public final Node importNode(final Node importedNode, final boolean deep) throws DOMException {
        throw new DTMDOMException((short)7);
    }
    
    @Override
    public final Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
        final Vector listVector = new Vector();
        final Node retNode = this.dtm.getNode(this.node);
        if (retNode != null) {
            final boolean isNamespaceURIWildCard = "*".equals(namespaceURI);
            final boolean isLocalNameWildCard = "*".equals(localName);
            if (1 == retNode.getNodeType()) {
                final NodeList nodeList = retNode.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    this.traverseChildren(listVector, nodeList.item(i), namespaceURI, localName, isNamespaceURIWildCard, isLocalNameWildCard);
                }
            }
            else if (9 == retNode.getNodeType()) {
                this.traverseChildren(listVector, this.dtm.getNode(this.node), namespaceURI, localName, isNamespaceURIWildCard, isLocalNameWildCard);
            }
        }
        final int size = listVector.size();
        final NodeSet nodeSet = new NodeSet(size);
        for (int j = 0; j < size; ++j) {
            nodeSet.addNode(listVector.elementAt(j));
        }
        return nodeSet;
    }
    
    private final void traverseChildren(final Vector listVector, final Node tempNode, final String namespaceURI, final String localname, final boolean isNamespaceURIWildCard, final boolean isLocalNameWildCard) {
        if (tempNode == null) {
            return;
        }
        if (tempNode.getNodeType() == 1 && (isLocalNameWildCard || tempNode.getLocalName().equals(localname))) {
            final String nsURI = tempNode.getNamespaceURI();
            if ((namespaceURI == null && nsURI == null) || isNamespaceURIWildCard || (namespaceURI != null && namespaceURI.equals(nsURI))) {
                listVector.add(tempNode);
            }
        }
        if (tempNode.hasChildNodes()) {
            final NodeList nl = tempNode.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                this.traverseChildren(listVector, nl.item(i), namespaceURI, localname, isNamespaceURIWildCard, isLocalNameWildCard);
            }
        }
    }
    
    @Override
    public final Element getElementById(final String elementId) {
        return (Element)this.dtm.getNode(this.dtm.getElementById(elementId));
    }
    
    @Override
    public final Text splitText(final int offset) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final String getData() throws DOMException {
        return this.dtm.getNodeValue(this.node);
    }
    
    @Override
    public final void setData(final String data) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final int getLength() {
        return this.dtm.getNodeValue(this.node).length();
    }
    
    @Override
    public final String substringData(final int offset, final int count) throws DOMException {
        return this.getData().substring(offset, offset + count);
    }
    
    @Override
    public final void appendData(final String arg) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final void insertData(final int offset, final String arg) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final void deleteData(final int offset, final int count) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final void replaceData(final int offset, final int count, final String arg) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final String getTagName() {
        return this.dtm.getNodeName(this.node);
    }
    
    @Override
    public final String getAttribute(final String name) {
        final DTMNamedNodeMap map = new DTMNamedNodeMap(this.dtm, this.node);
        final Node n = map.getNamedItem(name);
        return (null == n) ? "" : n.getNodeValue();
    }
    
    @Override
    public final void setAttribute(final String name, final String value) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final void removeAttribute(final String name) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final Attr getAttributeNode(final String name) {
        final DTMNamedNodeMap map = new DTMNamedNodeMap(this.dtm, this.node);
        return (Attr)map.getNamedItem(name);
    }
    
    @Override
    public final Attr setAttributeNode(final Attr newAttr) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final Attr removeAttributeNode(final Attr oldAttr) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public boolean hasAttributes() {
        return -1 != this.dtm.getFirstAttribute(this.node);
    }
    
    @Override
    public final void normalize() {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final String getAttributeNS(final String namespaceURI, final String localName) {
        Node retNode = null;
        final int n = this.dtm.getAttributeNode(this.node, namespaceURI, localName);
        if (n != -1) {
            retNode = this.dtm.getNode(n);
        }
        return (null == retNode) ? "" : retNode.getNodeValue();
    }
    
    @Override
    public final void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final void removeAttributeNS(final String namespaceURI, final String localName) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final Attr getAttributeNodeNS(final String namespaceURI, final String localName) {
        Attr retAttr = null;
        final int n = this.dtm.getAttributeNode(this.node, namespaceURI, localName);
        if (n != -1) {
            retAttr = (Attr)this.dtm.getNode(n);
        }
        return retAttr;
    }
    
    @Override
    public final Attr setAttributeNodeNS(final Attr newAttr) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final String getName() {
        return this.dtm.getNodeName(this.node);
    }
    
    @Override
    public final boolean getSpecified() {
        return true;
    }
    
    @Override
    public final String getValue() {
        return this.dtm.getNodeValue(this.node);
    }
    
    @Override
    public final void setValue(final String value) {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public final Element getOwnerElement() {
        if (this.getNodeType() != 2) {
            return null;
        }
        final int newnode = this.dtm.getParent(this.node);
        return (newnode == -1) ? null : ((Element)this.dtm.getNode(newnode));
    }
    
    @Override
    public Node adoptNode(final Node source) throws DOMException {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public String getInputEncoding() {
        throw new DTMDOMException((short)9);
    }
    
    public void setEncoding(final String encoding) {
        throw new DTMDOMException((short)9);
    }
    
    public boolean getStandalone() {
        throw new DTMDOMException((short)9);
    }
    
    public void setStandalone(final boolean standalone) {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public boolean getStrictErrorChecking() {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public void setStrictErrorChecking(final boolean strictErrorChecking) {
        throw new DTMDOMException((short)9);
    }
    
    public String getVersion() {
        throw new DTMDOMException((short)9);
    }
    
    public void setVersion(final String version) {
        throw new DTMDOMException((short)9);
    }
    
    @Override
    public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
        return this.getOwnerDocument().setUserData(key, data, handler);
    }
    
    @Override
    public Object getUserData(final String key) {
        return this.getOwnerDocument().getUserData(key);
    }
    
    @Override
    public Object getFeature(final String feature, final String version) {
        return this.isSupported(feature, version) ? this : null;
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
                return null;
            }
            case 6:
            case 10:
            case 11:
            case 12: {
                return null;
            }
            case 2: {
                if (this.getOwnerElement().getNodeType() == 1) {
                    return this.getOwnerElement().lookupNamespaceURI(specifiedPrefix);
                }
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public boolean isDefaultNamespace(final String namespaceURI) {
        return false;
    }
    
    @Override
    public String lookupPrefix(final String namespaceURI) {
        if (namespaceURI == null) {
            return null;
        }
        final short type = this.getNodeType();
        switch (type) {
            case 6:
            case 10:
            case 11:
            case 12: {
                return null;
            }
            case 2: {
                if (this.getOwnerElement().getNodeType() == 1) {
                    return this.getOwnerElement().lookupPrefix(namespaceURI);
                }
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public boolean isSameNode(final Node other) {
        return this == other;
    }
    
    @Override
    public void setTextContent(final String textContent) throws DOMException {
        this.setNodeValue(textContent);
    }
    
    @Override
    public String getTextContent() throws DOMException {
        return this.dtm.getStringValue(this.node).toString();
    }
    
    @Override
    public short compareDocumentPosition(final Node other) throws DOMException {
        return 0;
    }
    
    @Override
    public String getBaseURI() {
        return null;
    }
    
    @Override
    public Node renameNode(final Node n, final String namespaceURI, final String name) throws DOMException {
        return n;
    }
    
    @Override
    public void normalizeDocument() {
    }
    
    @Override
    public DOMConfiguration getDomConfig() {
        return null;
    }
    
    @Override
    public void setDocumentURI(final String documentURI) {
        this.fDocumentURI = documentURI;
    }
    
    @Override
    public String getDocumentURI() {
        return this.fDocumentURI;
    }
    
    public String getActualEncoding() {
        return this.actualEncoding;
    }
    
    public void setActualEncoding(final String value) {
        this.actualEncoding = value;
    }
    
    @Override
    public Text replaceWholeText(final String content) throws DOMException {
        return null;
    }
    
    @Override
    public String getWholeText() {
        return null;
    }
    
    @Override
    public boolean isElementContentWhitespace() {
        return false;
    }
    
    public void setIdAttribute(final boolean id) {
    }
    
    @Override
    public void setIdAttribute(final String name, final boolean makeId) {
    }
    
    @Override
    public void setIdAttributeNode(final Attr at, final boolean makeId) {
    }
    
    @Override
    public void setIdAttributeNS(final String namespaceURI, final String localName, final boolean makeId) {
    }
    
    @Override
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }
    
    @Override
    public boolean isId() {
        return false;
    }
    
    @Override
    public String getXmlEncoding() {
        return this.xmlEncoding;
    }
    
    public void setXmlEncoding(final String xmlEncoding) {
        this.xmlEncoding = xmlEncoding;
    }
    
    @Override
    public boolean getXmlStandalone() {
        return this.xmlStandalone;
    }
    
    @Override
    public void setXmlStandalone(final boolean xmlStandalone) throws DOMException {
        this.xmlStandalone = xmlStandalone;
    }
    
    @Override
    public String getXmlVersion() {
        return this.xmlVersion;
    }
    
    @Override
    public void setXmlVersion(final String xmlVersion) throws DOMException {
        this.xmlVersion = xmlVersion;
    }
    
    static {
        implementation = new DTMNodeProxyImplementation();
    }
    
    static class DTMNodeProxyImplementation implements DOMImplementation
    {
        @Override
        public DocumentType createDocumentType(final String qualifiedName, final String publicId, final String systemId) {
            throw new DTMDOMException((short)9);
        }
        
        @Override
        public Document createDocument(final String namespaceURI, final String qualfiedName, final DocumentType doctype) {
            throw new DTMDOMException((short)9);
        }
        
        @Override
        public boolean hasFeature(final String feature, final String version) {
            return ("CORE".equals(feature.toUpperCase()) || "XML".equals(feature.toUpperCase())) && ("1.0".equals(version) || "2.0".equals(version));
        }
        
        @Override
        public Object getFeature(final String feature, final String version) {
            return null;
        }
    }
}
