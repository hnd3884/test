package org.apache.xerces.dom;

import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.apache.xerces.util.URI;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ElementTraversal;
import org.w3c.dom.Element;

public class ElementImpl extends ParentNode implements Element, ElementTraversal, TypeInfo
{
    static final long serialVersionUID = 3717253516652722278L;
    protected String name;
    protected AttributeMap attributes;
    
    public ElementImpl(final CoreDocumentImpl coreDocumentImpl, final String name) {
        super(coreDocumentImpl);
        this.name = name;
        this.needsSyncData(true);
    }
    
    protected ElementImpl() {
    }
    
    void rename(final String name) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking) {
            if (name.indexOf(58) != -1) {
                throw new DOMException((short)14, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
            }
            if (!CoreDocumentImpl.isXMLName(name, this.ownerDocument.isXML11Version())) {
                throw new DOMException((short)5, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null));
            }
        }
        this.name = name;
        this.reconcileDefaultAttributes();
    }
    
    public short getNodeType() {
        return 1;
    }
    
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    public NamedNodeMap getAttributes() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this, null);
        }
        return this.attributes;
    }
    
    public Node cloneNode(final boolean b) {
        final ElementImpl elementImpl = (ElementImpl)super.cloneNode(b);
        if (this.attributes != null) {
            elementImpl.attributes = (AttributeMap)this.attributes.cloneMap(elementImpl);
        }
        return elementImpl;
    }
    
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes != null) {
            final Attr xmlBaseAttribute = this.getXMLBaseAttribute();
            if (xmlBaseAttribute != null) {
                final String nodeValue = xmlBaseAttribute.getNodeValue();
                if (nodeValue.length() != 0) {
                    try {
                        final URI uri = new URI(nodeValue, true);
                        if (uri.isAbsoluteURI()) {
                            return uri.toString();
                        }
                        final String s = (this.ownerNode != null) ? this.ownerNode.getBaseURI() : null;
                        if (s != null) {
                            try {
                                uri.absolutize(new URI(s));
                                return uri.toString();
                            }
                            catch (final URI.MalformedURIException ex) {
                                return null;
                            }
                        }
                        return null;
                    }
                    catch (final URI.MalformedURIException ex2) {
                        return null;
                    }
                }
            }
        }
        return (this.ownerNode != null) ? this.ownerNode.getBaseURI() : null;
    }
    
    protected Attr getXMLBaseAttribute() {
        return (Attr)this.attributes.getNamedItem("xml:base");
    }
    
    protected void setOwnerDocument(final CoreDocumentImpl coreDocumentImpl) {
        super.setOwnerDocument(coreDocumentImpl);
        if (this.attributes != null) {
            this.attributes.setOwnerDocument(coreDocumentImpl);
        }
    }
    
    public String getAttribute(final String s) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return "";
        }
        final Attr attr = (Attr)this.attributes.getNamedItem(s);
        return (attr == null) ? "" : attr.getValue();
    }
    
    public Attr getAttributeNode(final String s) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return null;
        }
        return (Attr)this.attributes.getNamedItem(s);
    }
    
    public NodeList getElementsByTagName(final String s) {
        return new DeepNodeListImpl(this, s);
    }
    
    public String getTagName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }
    
    public void normalize() {
        if (this.isNormalized()) {
            return;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        Object nextSibling;
        for (Object firstChild = this.firstChild; firstChild != null; firstChild = nextSibling) {
            nextSibling = ((ChildNode)firstChild).nextSibling;
            if (((NodeImpl)firstChild).getNodeType() == 3) {
                if (nextSibling != null && ((NodeImpl)nextSibling).getNodeType() == 3) {
                    ((Text)firstChild).appendData(((NodeImpl)nextSibling).getNodeValue());
                    this.removeChild((Node)nextSibling);
                    nextSibling = firstChild;
                }
                else if (((NodeImpl)firstChild).getNodeValue() == null || ((NodeImpl)firstChild).getNodeValue().length() == 0) {
                    this.removeChild((Node)firstChild);
                }
            }
            else if (((NodeImpl)firstChild).getNodeType() == 1) {
                ((ChildNode)firstChild).normalize();
            }
        }
        if (this.attributes != null) {
            for (int i = 0; i < this.attributes.getLength(); ++i) {
                this.attributes.item(i).normalize();
            }
        }
        this.isNormalized(true);
    }
    
    public void removeAttribute(final String s) {
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return;
        }
        this.attributes.safeRemoveNamedItem(s);
    }
    
    public Attr removeAttributeNode(final Attr attr) throws DOMException {
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
        }
        return (Attr)this.attributes.removeItem(attr, true);
    }
    
    public void setAttribute(final String s, final String s2) {
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final Attr attributeNode = this.getAttributeNode(s);
        if (attributeNode == null) {
            final Attr attribute = this.getOwnerDocument().createAttribute(s);
            if (this.attributes == null) {
                this.attributes = new AttributeMap(this, null);
            }
            attribute.setNodeValue(s2);
            this.attributes.setNamedItem(attribute);
        }
        else {
            attributeNode.setNodeValue(s2);
        }
    }
    
    public Attr setAttributeNode(final Attr namedItem) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (namedItem.getOwnerDocument() != this.ownerDocument) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this, null);
        }
        return (Attr)this.attributes.setNamedItem(namedItem);
    }
    
    public String getAttributeNS(final String s, final String s2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return "";
        }
        final Attr attr = (Attr)this.attributes.getNamedItemNS(s, s2);
        return (attr == null) ? "" : attr.getValue();
    }
    
    public void setAttributeNS(final String s, final String s2, final String s3) {
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final int index = s2.indexOf(58);
        String substring;
        String substring2;
        if (index < 0) {
            substring = null;
            substring2 = s2;
        }
        else {
            substring = s2.substring(0, index);
            substring2 = s2.substring(index + 1);
        }
        Attr namedItemNS = this.getAttributeNodeNS(s, substring2);
        if (namedItemNS == null) {
            final Attr attributeNS = this.getOwnerDocument().createAttributeNS(s, s2);
            if (this.attributes == null) {
                this.attributes = new AttributeMap(this, null);
            }
            attributeNS.setNodeValue(s3);
            this.attributes.setNamedItemNS(attributeNS);
        }
        else {
            if (namedItemNS instanceof AttrNSImpl) {
                ((AttrNSImpl)namedItemNS).name = ((substring != null) ? (substring + ":" + substring2) : substring2);
            }
            else {
                namedItemNS = ((CoreDocumentImpl)this.getOwnerDocument()).createAttributeNS(s, s2, substring2);
                this.attributes.setNamedItemNS(namedItemNS);
            }
            namedItemNS.setNodeValue(s3);
        }
    }
    
    public void removeAttributeNS(final String s, final String s2) {
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return;
        }
        this.attributes.safeRemoveNamedItemNS(s, s2);
    }
    
    public Attr getAttributeNodeNS(final String s, final String s2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return null;
        }
        return (Attr)this.attributes.getNamedItemNS(s, s2);
    }
    
    public Attr setAttributeNodeNS(final Attr namedItemNS) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (namedItemNS.getOwnerDocument() != this.ownerDocument) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this, null);
        }
        return (Attr)this.attributes.setNamedItemNS(namedItemNS);
    }
    
    protected int setXercesAttributeNode(final Attr attr) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this, null);
        }
        return this.attributes.addItem(attr);
    }
    
    protected int getXercesAttribute(final String s, final String s2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return -1;
        }
        return this.attributes.getNamedItemIndex(s, s2);
    }
    
    public boolean hasAttributes() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.attributes != null && this.attributes.getLength() != 0;
    }
    
    public boolean hasAttribute(final String s) {
        return this.getAttributeNode(s) != null;
    }
    
    public boolean hasAttributeNS(final String s, final String s2) {
        return this.getAttributeNodeNS(s, s2) != null;
    }
    
    public NodeList getElementsByTagNameNS(final String s, final String s2) {
        return new DeepNodeListImpl(this, s, s2);
    }
    
    public boolean isEqualNode(final Node node) {
        if (!super.isEqualNode(node)) {
            return false;
        }
        final boolean hasAttributes = this.hasAttributes();
        if (hasAttributes != node.hasAttributes()) {
            return false;
        }
        if (hasAttributes) {
            final NamedNodeMap attributes = this.getAttributes();
            final NamedNodeMap attributes2 = node.getAttributes();
            final int length = attributes.getLength();
            if (length != attributes2.getLength()) {
                return false;
            }
            for (int i = 0; i < length; ++i) {
                final Node item = attributes.item(i);
                if (item.getLocalName() == null) {
                    final Node namedItem = attributes2.getNamedItem(item.getNodeName());
                    if (namedItem == null || !((NodeImpl)item).isEqualNode(namedItem)) {
                        return false;
                    }
                }
                else {
                    final Node namedItemNS = attributes2.getNamedItemNS(item.getNamespaceURI(), item.getLocalName());
                    if (namedItemNS == null || !((NodeImpl)item).isEqualNode(namedItemNS)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public void setIdAttributeNode(final Attr attr, final boolean b) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (attr.getOwnerElement() != this) {
                throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
            }
        }
        ((AttrImpl)attr).isIdAttribute(b);
        if (!b) {
            this.ownerDocument.removeIdentifier(attr.getValue());
        }
        else {
            this.ownerDocument.putIdentifier(attr.getValue(), this);
        }
    }
    
    public void setIdAttribute(final String s, final boolean b) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final Attr attributeNode = this.getAttributeNode(s);
        if (attributeNode == null) {
            throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (attributeNode.getOwnerElement() != this) {
                throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
            }
        }
        ((AttrImpl)attributeNode).isIdAttribute(b);
        if (!b) {
            this.ownerDocument.removeIdentifier(attributeNode.getValue());
        }
        else {
            this.ownerDocument.putIdentifier(attributeNode.getValue(), this);
        }
    }
    
    public void setIdAttributeNS(final String s, final String s2, final boolean b) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        final Attr attributeNodeNS = this.getAttributeNodeNS(s, s2);
        if (attributeNodeNS == null) {
            throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (attributeNodeNS.getOwnerElement() != this) {
                throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
            }
        }
        ((AttrImpl)attributeNodeNS).isIdAttribute(b);
        if (!b) {
            this.ownerDocument.removeIdentifier(attributeNodeNS.getValue());
        }
        else {
            this.ownerDocument.putIdentifier(attributeNodeNS.getValue(), this);
        }
    }
    
    public String getTypeName() {
        return null;
    }
    
    public String getTypeNamespace() {
        return null;
    }
    
    public boolean isDerivedFrom(final String s, final String s2, final int n) {
        return false;
    }
    
    public TypeInfo getSchemaTypeInfo() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this;
    }
    
    public void setReadOnly(final boolean b, final boolean b2) {
        super.setReadOnly(b, b2);
        if (this.attributes != null) {
            this.attributes.setReadOnly(b, true);
        }
    }
    
    protected void synchronizeData() {
        this.needsSyncData(false);
        final boolean mutationEvents = this.ownerDocument.getMutationEvents();
        this.ownerDocument.setMutationEvents(false);
        this.setupDefaultAttributes();
        this.ownerDocument.setMutationEvents(mutationEvents);
    }
    
    void moveSpecifiedAttributes(final ElementImpl elementImpl) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (elementImpl.hasAttributes()) {
            if (this.attributes == null) {
                this.attributes = new AttributeMap(this, null);
            }
            this.attributes.moveSpecifiedAttributes(elementImpl.attributes);
        }
    }
    
    protected void setupDefaultAttributes() {
        final NamedNodeMapImpl defaultAttributes = this.getDefaultAttributes();
        if (defaultAttributes != null) {
            this.attributes = new AttributeMap(this, defaultAttributes);
        }
    }
    
    protected void reconcileDefaultAttributes() {
        if (this.attributes != null) {
            this.attributes.reconcileDefaults(this.getDefaultAttributes());
        }
    }
    
    protected NamedNodeMapImpl getDefaultAttributes() {
        final DocumentTypeImpl documentTypeImpl = (DocumentTypeImpl)this.ownerDocument.getDoctype();
        if (documentTypeImpl == null) {
            return null;
        }
        final ElementDefinitionImpl elementDefinitionImpl = (ElementDefinitionImpl)documentTypeImpl.getElements().getNamedItem(this.getNodeName());
        if (elementDefinitionImpl == null) {
            return null;
        }
        return (NamedNodeMapImpl)elementDefinitionImpl.getAttributes();
    }
    
    public final int getChildElementCount() {
        int n = 0;
        for (Element element = this.getFirstElementChild(); element != null; element = ((ElementImpl)element).getNextElementSibling()) {
            ++n;
        }
        return n;
    }
    
    public final Element getFirstElementChild() {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            switch (node.getNodeType()) {
                case 1: {
                    return (Element)node;
                }
                case 5: {
                    final Element firstElementChild = this.getFirstElementChild(node);
                    if (firstElementChild != null) {
                        return firstElementChild;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    public final Element getLastElementChild() {
        for (Node node = this.getLastChild(); node != null; node = node.getPreviousSibling()) {
            switch (node.getNodeType()) {
                case 1: {
                    return (Element)node;
                }
                case 5: {
                    final Element lastElementChild = this.getLastElementChild(node);
                    if (lastElementChild != null) {
                        return lastElementChild;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    public final Element getNextElementSibling() {
        for (Node node = this.getNextLogicalSibling(this); node != null; node = this.getNextLogicalSibling(node)) {
            switch (node.getNodeType()) {
                case 1: {
                    return (Element)node;
                }
                case 5: {
                    final Element firstElementChild = this.getFirstElementChild(node);
                    if (firstElementChild != null) {
                        return firstElementChild;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    public final Element getPreviousElementSibling() {
        for (Node node = this.getPreviousLogicalSibling(this); node != null; node = this.getPreviousLogicalSibling(node)) {
            switch (node.getNodeType()) {
                case 1: {
                    return (Element)node;
                }
                case 5: {
                    final Element lastElementChild = this.getLastElementChild(node);
                    if (lastElementChild != null) {
                        return lastElementChild;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    private Element getFirstElementChild(Node parentNode) {
        final Node node = parentNode;
        while (parentNode != null) {
            if (parentNode.getNodeType() == 1) {
                return (Element)parentNode;
            }
            Node node2 = parentNode.getFirstChild();
            while (node2 == null && node != parentNode) {
                node2 = parentNode.getNextSibling();
                if (node2 == null) {
                    parentNode = parentNode.getParentNode();
                    if (parentNode == null || node == parentNode) {
                        return null;
                    }
                    continue;
                }
            }
            parentNode = node2;
        }
        return null;
    }
    
    private Element getLastElementChild(Node parentNode) {
        final Node node = parentNode;
        while (parentNode != null) {
            if (parentNode.getNodeType() == 1) {
                return (Element)parentNode;
            }
            Node node2 = parentNode.getLastChild();
            while (node2 == null && node != parentNode) {
                node2 = parentNode.getPreviousSibling();
                if (node2 == null) {
                    parentNode = parentNode.getParentNode();
                    if (parentNode == null || node == parentNode) {
                        return null;
                    }
                    continue;
                }
            }
            parentNode = node2;
        }
        return null;
    }
    
    private Node getNextLogicalSibling(final Node node) {
        Node node2 = node.getNextSibling();
        if (node2 == null) {
            for (Node node3 = node.getParentNode(); node3 != null && node3.getNodeType() == 5; node3 = node3.getParentNode()) {
                node2 = node3.getNextSibling();
                if (node2 != null) {
                    break;
                }
            }
        }
        return node2;
    }
    
    private Node getPreviousLogicalSibling(final Node node) {
        Node node2 = node.getPreviousSibling();
        if (node2 == null) {
            for (Node node3 = node.getParentNode(); node3 != null && node3.getNodeType() == 5; node3 = node3.getParentNode()) {
                node2 = node3.getPreviousSibling();
                if (node2 != null) {
                    break;
                }
            }
        }
        return node2;
    }
}
