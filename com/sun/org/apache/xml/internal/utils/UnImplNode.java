package com.sun.org.apache.xml.internal.utils;

import org.w3c.dom.TypeInfo;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.EntityReference;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class UnImplNode implements Node, Element, NodeList, Document
{
    protected String fDocumentURI;
    protected String actualEncoding;
    private String xmlEncoding;
    private boolean xmlStandalone;
    private String xmlVersion;
    
    public void error(final String msg) {
        System.out.println("DOM ERROR! class: " + this.getClass().getName());
        throw new RuntimeException(XMLMessages.createXMLMessage(msg, null));
    }
    
    public void error(final String msg, final Object[] args) {
        System.out.println("DOM ERROR! class: " + this.getClass().getName());
        throw new RuntimeException(XMLMessages.createXMLMessage(msg, args));
    }
    
    @Override
    public Node appendChild(final Node newChild) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public boolean hasChildNodes() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return false;
    }
    
    @Override
    public short getNodeType() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return 0;
    }
    
    @Override
    public Node getParentNode() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public NodeList getChildNodes() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Node getFirstChild() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Node getLastChild() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Node getNextSibling() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public int getLength() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return 0;
    }
    
    @Override
    public Node item(final int index) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Document getOwnerDocument() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public String getTagName() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public String getNodeName() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public void normalize() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    @Override
    public NodeList getElementsByTagName(final String name) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Attr removeAttributeNode(final Attr oldAttr) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Attr setAttributeNode(final Attr newAttr) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public boolean hasAttribute(final String name) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return false;
    }
    
    @Override
    public boolean hasAttributeNS(final String name, final String x) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return false;
    }
    
    @Override
    public Attr getAttributeNode(final String name) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public void removeAttribute(final String name) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    @Override
    public void setAttribute(final String name, final String value) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    @Override
    public String getAttribute(final String name) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public boolean hasAttributes() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return false;
    }
    
    @Override
    public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Attr setAttributeNodeNS(final Attr newAttr) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Attr getAttributeNodeNS(final String namespaceURI, final String localName) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public void removeAttributeNS(final String namespaceURI, final String localName) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    @Override
    public void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    @Override
    public String getAttributeNS(final String namespaceURI, final String localName) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Node getPreviousSibling() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public String getNodeValue() throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public void setNodeValue(final String nodeValue) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    public void setValue(final String value) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    public Element getOwnerElement() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    public boolean getSpecified() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return false;
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Node removeChild(final Node oldChild) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public boolean isSupported(final String feature, final String version) {
        return false;
    }
    
    @Override
    public String getNamespaceURI() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public String getPrefix() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public void setPrefix(final String prefix) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    @Override
    public String getLocalName() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public DocumentType getDoctype() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public DOMImplementation getImplementation() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Element getDocumentElement() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Element createElement(final String tagName) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public DocumentFragment createDocumentFragment() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Text createTextNode(final String data) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Comment createComment(final String data) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public CDATASection createCDATASection(final String data) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Attr createAttribute(final String name) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public EntityReference createEntityReference(final String name) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Node importNode(final Node importedNode, final boolean deep) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Element getElementById(final String elementId) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    public void setData(final String data) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    public String substringData(final int offset, final int count) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    public void appendData(final String arg) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    public void insertData(final int offset, final String arg) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    public void deleteData(final int offset, final int count) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    public void replaceData(final int offset, final int count, final String arg) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    public Text splitText(final int offset) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public Node adoptNode(final Node source) throws DOMException {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    @Override
    public String getInputEncoding() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    public void setInputEncoding(final String encoding) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    public boolean getStandalone() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return false;
    }
    
    public void setStandalone(final boolean standalone) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    @Override
    public boolean getStrictErrorChecking() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return false;
    }
    
    @Override
    public void setStrictErrorChecking(final boolean strictErrorChecking) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
    }
    
    public String getVersion() {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
        return null;
    }
    
    public void setVersion(final String version) {
        this.error("ER_FUNCTION_NOT_SUPPORTED");
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
        return this.getNodeValue();
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
    
    public Text replaceWholeText(final String content) throws DOMException {
        return null;
    }
    
    public String getWholeText() {
        return null;
    }
    
    public boolean isWhitespaceInElementContent() {
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
}
