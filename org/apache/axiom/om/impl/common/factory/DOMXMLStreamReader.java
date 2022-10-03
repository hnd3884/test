package org.apache.axiom.om.impl.common.factory;

import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import javax.xml.namespace.QName;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Text;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Document;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.util.stax.AbstractXMLStreamReader;

class DOMXMLStreamReader extends AbstractXMLStreamReader implements DTDReader
{
    private final Node root;
    private final boolean dom3;
    private final boolean expandEntityReferences;
    private Node node;
    private int event;
    private boolean attributesLoaded;
    private int attributeCount;
    private Attr[] attributes;
    private int namespaceCount;
    private Attr[] namespaces;
    private NamespaceContext nsContext;
    
    DOMXMLStreamReader(final Node node, final boolean expandEntityReferences) {
        this.attributes = new Attr[8];
        this.namespaces = new Attr[8];
        this.root = node;
        Document ownerDocument;
        if (node.getNodeType() == 9) {
            this.node = node;
            ownerDocument = (Document)node;
        }
        else {
            ownerDocument = node.getOwnerDocument();
        }
        this.dom3 = ownerDocument.getImplementation().hasFeature("XML", "3.0");
        this.expandEntityReferences = expandEntityReferences;
        this.event = 7;
    }
    
    Node currentNode() {
        return this.node;
    }
    
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (DTDReader.PROPERTY.equals(name)) {
            return this;
        }
        return null;
    }
    
    public boolean hasNext() throws XMLStreamException {
        return this.event != 8;
    }
    
    public int next() throws XMLStreamException {
        if (this.event == 8) {
            throw new NoSuchElementException("End of the document reached");
        }
        boolean forceTraverse = false;
    Label_0384:
        while (true) {
            boolean visited;
            if (this.node == null) {
                this.node = this.root;
                visited = false;
            }
            else if (this.event == 7 || this.event == 1 || forceTraverse) {
                final Node firstChild = this.node.getFirstChild();
                if (firstChild == null) {
                    visited = true;
                }
                else {
                    this.node = firstChild;
                    visited = false;
                }
                forceTraverse = false;
            }
            else if (this.node == this.root) {
                this.node = null;
                visited = true;
            }
            else {
                final Node nextSibling = this.node.getNextSibling();
                if (nextSibling == null) {
                    this.node = this.node.getParentNode();
                    visited = true;
                }
                else {
                    this.node = nextSibling;
                    visited = false;
                }
            }
            switch ((this.node == null) ? 9 : this.node.getNodeType()) {
                case 9: {
                    this.event = 8;
                    break Label_0384;
                }
                case 10: {
                    this.event = 11;
                    break Label_0384;
                }
                case 1: {
                    this.event = (visited ? 2 : 1);
                    this.attributesLoaded = false;
                    break Label_0384;
                }
                case 3: {
                    this.event = ((this.dom3 && ((Text)this.node).isElementContentWhitespace()) ? 6 : 4);
                    break Label_0384;
                }
                case 4: {
                    this.event = 12;
                    break Label_0384;
                }
                case 8: {
                    this.event = 5;
                    break Label_0384;
                }
                case 7: {
                    this.event = 3;
                    break Label_0384;
                }
                case 5: {
                    if (!this.expandEntityReferences) {
                        this.event = 9;
                        break Label_0384;
                    }
                    if (!visited) {
                        forceTraverse = true;
                        continue;
                    }
                    continue;
                }
                default: {
                    throw new IllegalStateException("Unexpected node type " + this.node.getNodeType());
                }
            }
        }
        return this.event;
    }
    
    public int getEventType() {
        return this.event;
    }
    
    public String getEncoding() {
        if (this.event == 7) {
            return (this.dom3 && this.node != null) ? ((Document)this.node).getInputEncoding() : null;
        }
        throw new IllegalStateException();
    }
    
    public String getVersion() {
        return (this.dom3 && this.node != null) ? ((Document)this.node).getXmlVersion() : "1.0";
    }
    
    public String getCharacterEncodingScheme() {
        if (this.event == 7) {
            return (this.dom3 && this.node != null) ? ((Document)this.node).getXmlEncoding() : null;
        }
        throw new IllegalStateException();
    }
    
    public boolean isStandalone() {
        return !this.dom3 || this.node == null || ((Document)this.node).getXmlStandalone();
    }
    
    public boolean standaloneSet() {
        return true;
    }
    
    public String getRootName() {
        return ((DocumentType)this.node).getName();
    }
    
    public String getPublicId() {
        return ((DocumentType)this.node).getPublicId();
    }
    
    public String getSystemId() {
        return ((DocumentType)this.node).getSystemId();
    }
    
    public String getLocalName() {
        switch (this.event) {
            case 1:
            case 2: {
                return this.node.getLocalName();
            }
            case 9: {
                return this.node.getNodeName();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public String getNamespaceURI() {
        switch (this.event) {
            case 1:
            case 2: {
                return this.node.getNamespaceURI();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public String getPrefix() {
        switch (this.event) {
            case 1:
            case 2: {
                return this.node.getPrefix();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public QName getName() {
        switch (this.event) {
            case 1:
            case 2: {
                return getQName(this.node);
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    private Attr[] grow(final Attr[] array) {
        final Attr[] newArray = new Attr[array.length * 2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }
    
    private void loadAttributes() {
        if (!this.attributesLoaded) {
            this.attributeCount = 0;
            this.namespaceCount = 0;
            final NamedNodeMap attrs = this.node.getAttributes();
            for (int i = 0, l = attrs.getLength(); i < l; ++i) {
                final Attr attr = (Attr)attrs.item(i);
                if (DOMUtils.isNSDecl(attr)) {
                    if (this.namespaceCount == this.namespaces.length) {
                        this.namespaces = this.grow(this.namespaces);
                    }
                    this.namespaces[this.namespaceCount++] = attr;
                }
                else {
                    if (this.attributeCount == this.attributes.length) {
                        this.attributes = this.grow(this.attributes);
                    }
                    this.attributes[this.attributeCount++] = attr;
                }
            }
            this.attributesLoaded = true;
        }
    }
    
    public int getAttributeCount() {
        if (this.event == 1) {
            this.loadAttributes();
            return this.attributeCount;
        }
        throw new IllegalStateException();
    }
    
    private Attr getAttribute(final int index) {
        if (this.event == 1) {
            this.loadAttributes();
            return this.attributes[index];
        }
        throw new IllegalStateException();
    }
    
    public String getAttributeLocalName(final int index) {
        return this.getAttribute(index).getLocalName();
    }
    
    public String getAttributeNamespace(final int index) {
        return this.getAttribute(index).getNamespaceURI();
    }
    
    public String getAttributePrefix(final int index) {
        return this.getAttribute(index).getPrefix();
    }
    
    public QName getAttributeName(final int index) {
        return getQName(this.getAttribute(index));
    }
    
    public String getAttributeValue(final int index) {
        return this.getAttribute(index).getValue();
    }
    
    public String getAttributeType(final int index) {
        if (this.event == 1) {
            return "CDATA";
        }
        throw new IllegalStateException();
    }
    
    public boolean isAttributeSpecified(final int index) {
        return this.getAttribute(index).getSpecified();
    }
    
    public String getAttributeValue(final String namespaceURI, final String localName) {
        return ((Element)this.node).getAttributeNS((namespaceURI == null || namespaceURI.length() == 0) ? null : namespaceURI, localName);
    }
    
    public int getNamespaceCount() {
        switch (this.event) {
            case 1:
            case 2: {
                this.loadAttributes();
                return this.namespaceCount;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    private Attr getNamespace(final int index) {
        switch (this.event) {
            case 1:
            case 2: {
                this.loadAttributes();
                return this.namespaces[index];
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public String getNamespacePrefix(final int index) {
        return DOMUtils.getNSDeclPrefix(this.getNamespace(index));
    }
    
    public String getNamespaceURI(final int index) {
        return this.getNamespace(index).getValue();
    }
    
    private String internalGetText() {
        switch (this.event) {
            case 4:
            case 5:
            case 6:
            case 12: {
                return this.node.getNodeValue();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public String getText() {
        switch (this.event) {
            case 11: {
                return ((DocumentType)this.node).getInternalSubset();
            }
            case 9: {
                return null;
            }
            default: {
                return this.internalGetText();
            }
        }
    }
    
    public int getTextStart() {
        this.internalGetText();
        return 0;
    }
    
    public int getTextLength() {
        return this.internalGetText().length();
    }
    
    public char[] getTextCharacters() {
        return this.internalGetText().toCharArray();
    }
    
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        final String text = this.internalGetText();
        final int copied = Math.min(length, text.length() - sourceStart);
        text.getChars(sourceStart, sourceStart + copied, target, targetStart);
        return copied;
    }
    
    public String getPITarget() {
        if (this.event == 3) {
            return ((ProcessingInstruction)this.node).getTarget();
        }
        throw new IllegalStateException();
    }
    
    public String getPIData() {
        if (this.event == 3) {
            return ((ProcessingInstruction)this.node).getData();
        }
        throw new IllegalStateException();
    }
    
    public NamespaceContext getNamespaceContext() {
        if (this.nsContext == null) {
            this.nsContext = (NamespaceContext)new DOMNamespaceContext(this);
        }
        return this.nsContext;
    }
    
    public String getNamespaceURI(final String prefix) {
        Node current = this.node;
        do {
            final NamedNodeMap attributes = current.getAttributes();
            if (attributes != null) {
                for (int i = 0, l = attributes.getLength(); i < l; ++i) {
                    final Attr attr = (Attr)attributes.item(i);
                    if (DOMUtils.isNSDecl(attr)) {
                        String candidatePrefix = DOMUtils.getNSDeclPrefix(attr);
                        if (candidatePrefix == null) {
                            candidatePrefix = "";
                        }
                        if (candidatePrefix.equals(prefix)) {
                            return attr.getValue();
                        }
                    }
                }
            }
            current = current.getParentNode();
        } while (current != null);
        return null;
    }
    
    public void close() throws XMLStreamException {
    }
    
    private static QName getQName(final Node node) {
        final String prefix = node.getPrefix();
        return new QName(node.getNamespaceURI(), node.getLocalName(), (prefix == null) ? "" : prefix);
    }
}
