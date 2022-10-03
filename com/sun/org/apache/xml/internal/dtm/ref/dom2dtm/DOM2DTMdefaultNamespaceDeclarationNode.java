package com.sun.org.apache.xml.internal.dtm.ref.dom2dtm;

import org.w3c.dom.DOMException;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.Attr;

public class DOM2DTMdefaultNamespaceDeclarationNode implements Attr, TypeInfo
{
    final String NOT_SUPPORTED_ERR = "Unsupported operation on pseudonode";
    Element pseudoparent;
    String prefix;
    String uri;
    String nodename;
    int handle;
    
    DOM2DTMdefaultNamespaceDeclarationNode(final Element pseudoparent, final String prefix, final String uri, final int handle) {
        this.pseudoparent = pseudoparent;
        this.prefix = prefix;
        this.uri = uri;
        this.handle = handle;
        this.nodename = "xmlns:" + prefix;
    }
    
    @Override
    public String getNodeName() {
        return this.nodename;
    }
    
    @Override
    public String getName() {
        return this.nodename;
    }
    
    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2000/xmlns/";
    }
    
    @Override
    public String getPrefix() {
        return this.prefix;
    }
    
    @Override
    public String getLocalName() {
        return this.prefix;
    }
    
    @Override
    public String getNodeValue() {
        return this.uri;
    }
    
    @Override
    public String getValue() {
        return this.uri;
    }
    
    @Override
    public Element getOwnerElement() {
        return this.pseudoparent;
    }
    
    @Override
    public boolean isSupported(final String feature, final String version) {
        return false;
    }
    
    @Override
    public boolean hasChildNodes() {
        return false;
    }
    
    @Override
    public boolean hasAttributes() {
        return false;
    }
    
    @Override
    public Node getParentNode() {
        return null;
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
    public Node getPreviousSibling() {
        return null;
    }
    
    @Override
    public Node getNextSibling() {
        return null;
    }
    
    @Override
    public boolean getSpecified() {
        return false;
    }
    
    @Override
    public void normalize() {
    }
    
    @Override
    public NodeList getChildNodes() {
        return null;
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }
    
    @Override
    public short getNodeType() {
        return 2;
    }
    
    @Override
    public void setNodeValue(final String value) {
        throw new DTMException("Unsupported operation on pseudonode");
    }
    
    @Override
    public void setValue(final String value) {
        throw new DTMException("Unsupported operation on pseudonode");
    }
    
    @Override
    public void setPrefix(final String value) {
        throw new DTMException("Unsupported operation on pseudonode");
    }
    
    @Override
    public Node insertBefore(final Node a, final Node b) {
        throw new DTMException("Unsupported operation on pseudonode");
    }
    
    @Override
    public Node replaceChild(final Node a, final Node b) {
        throw new DTMException("Unsupported operation on pseudonode");
    }
    
    @Override
    public Node appendChild(final Node a) {
        throw new DTMException("Unsupported operation on pseudonode");
    }
    
    @Override
    public Node removeChild(final Node a) {
        throw new DTMException("Unsupported operation on pseudonode");
    }
    
    @Override
    public Document getOwnerDocument() {
        return this.pseudoparent.getOwnerDocument();
    }
    
    @Override
    public Node cloneNode(final boolean deep) {
        throw new DTMException("Unsupported operation on pseudonode");
    }
    
    public int getHandleOfNode() {
        return this.handle;
    }
    
    @Override
    public String getTypeName() {
        return null;
    }
    
    @Override
    public String getTypeNamespace() {
        return null;
    }
    
    @Override
    public boolean isDerivedFrom(final String ns, final String localName, final int derivationMethod) {
        return false;
    }
    
    @Override
    public TypeInfo getSchemaTypeInfo() {
        return this;
    }
    
    @Override
    public boolean isId() {
        return false;
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
}
