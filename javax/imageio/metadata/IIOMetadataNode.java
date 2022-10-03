package javax.imageio.metadata;

import org.w3c.dom.UserDataHandler;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class IIOMetadataNode implements Element, NodeList
{
    private String nodeName;
    private String nodeValue;
    private Object userObject;
    private IIOMetadataNode parent;
    private int numChildren;
    private IIOMetadataNode firstChild;
    private IIOMetadataNode lastChild;
    private IIOMetadataNode nextSibling;
    private IIOMetadataNode previousSibling;
    private List attributes;
    
    public IIOMetadataNode() {
        this.nodeName = null;
        this.nodeValue = null;
        this.userObject = null;
        this.parent = null;
        this.numChildren = 0;
        this.firstChild = null;
        this.lastChild = null;
        this.nextSibling = null;
        this.previousSibling = null;
        this.attributes = new ArrayList();
    }
    
    public IIOMetadataNode(final String nodeName) {
        this.nodeName = null;
        this.nodeValue = null;
        this.userObject = null;
        this.parent = null;
        this.numChildren = 0;
        this.firstChild = null;
        this.lastChild = null;
        this.nextSibling = null;
        this.previousSibling = null;
        this.attributes = new ArrayList();
        this.nodeName = nodeName;
    }
    
    private void checkNode(final Node node) throws DOMException {
        if (node == null) {
            return;
        }
        if (!(node instanceof IIOMetadataNode)) {
            throw new IIODOMException((short)4, "Node not an IIOMetadataNode!");
        }
    }
    
    @Override
    public String getNodeName() {
        return this.nodeName;
    }
    
    @Override
    public String getNodeValue() {
        return this.nodeValue;
    }
    
    @Override
    public void setNodeValue(final String nodeValue) {
        this.nodeValue = nodeValue;
    }
    
    @Override
    public short getNodeType() {
        return 1;
    }
    
    @Override
    public Node getParentNode() {
        return this.parent;
    }
    
    @Override
    public NodeList getChildNodes() {
        return this;
    }
    
    @Override
    public Node getFirstChild() {
        return this.firstChild;
    }
    
    @Override
    public Node getLastChild() {
        return this.lastChild;
    }
    
    @Override
    public Node getPreviousSibling() {
        return this.previousSibling;
    }
    
    @Override
    public Node getNextSibling() {
        return this.nextSibling;
    }
    
    @Override
    public NamedNodeMap getAttributes() {
        return new IIONamedNodeMap(this.attributes);
    }
    
    @Override
    public Document getOwnerDocument() {
        return null;
    }
    
    @Override
    public Node insertBefore(final Node node, final Node node2) {
        if (node == null) {
            throw new IllegalArgumentException("newChild == null!");
        }
        this.checkNode(node);
        this.checkNode(node2);
        final IIOMetadataNode iioMetadataNode = (IIOMetadataNode)node;
        final IIOMetadataNode iioMetadataNode2 = (IIOMetadataNode)node2;
        IIOMetadataNode previousSibling;
        IIOMetadataNode nextSibling;
        if (node2 == null) {
            previousSibling = this.lastChild;
            nextSibling = null;
            this.lastChild = iioMetadataNode;
        }
        else {
            previousSibling = iioMetadataNode2.previousSibling;
            nextSibling = iioMetadataNode2;
        }
        if (previousSibling != null) {
            previousSibling.nextSibling = iioMetadataNode;
        }
        if (nextSibling != null) {
            nextSibling.previousSibling = iioMetadataNode;
        }
        iioMetadataNode.parent = this;
        iioMetadataNode.previousSibling = previousSibling;
        iioMetadataNode.nextSibling = nextSibling;
        if (this.firstChild == iioMetadataNode2) {
            this.firstChild = iioMetadataNode;
        }
        ++this.numChildren;
        return iioMetadataNode;
    }
    
    @Override
    public Node replaceChild(final Node node, final Node node2) {
        if (node == null) {
            throw new IllegalArgumentException("newChild == null!");
        }
        this.checkNode(node);
        this.checkNode(node2);
        final IIOMetadataNode iioMetadataNode = (IIOMetadataNode)node;
        final IIOMetadataNode iioMetadataNode2 = (IIOMetadataNode)node2;
        final IIOMetadataNode previousSibling = iioMetadataNode2.previousSibling;
        final IIOMetadataNode nextSibling = iioMetadataNode2.nextSibling;
        if (previousSibling != null) {
            previousSibling.nextSibling = iioMetadataNode;
        }
        if (nextSibling != null) {
            nextSibling.previousSibling = iioMetadataNode;
        }
        iioMetadataNode.parent = this;
        iioMetadataNode.previousSibling = previousSibling;
        iioMetadataNode.nextSibling = nextSibling;
        if (this.firstChild == iioMetadataNode2) {
            this.firstChild = iioMetadataNode;
        }
        if (this.lastChild == iioMetadataNode2) {
            this.lastChild = iioMetadataNode;
        }
        iioMetadataNode2.parent = null;
        iioMetadataNode2.previousSibling = null;
        iioMetadataNode2.nextSibling = null;
        return iioMetadataNode2;
    }
    
    @Override
    public Node removeChild(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("oldChild == null!");
        }
        this.checkNode(node);
        final IIOMetadataNode iioMetadataNode = (IIOMetadataNode)node;
        final IIOMetadataNode previousSibling = iioMetadataNode.previousSibling;
        final IIOMetadataNode nextSibling = iioMetadataNode.nextSibling;
        if (previousSibling != null) {
            previousSibling.nextSibling = nextSibling;
        }
        if (nextSibling != null) {
            nextSibling.previousSibling = previousSibling;
        }
        if (this.firstChild == iioMetadataNode) {
            this.firstChild = nextSibling;
        }
        if (this.lastChild == iioMetadataNode) {
            this.lastChild = previousSibling;
        }
        iioMetadataNode.parent = null;
        iioMetadataNode.previousSibling = null;
        iioMetadataNode.nextSibling = null;
        --this.numChildren;
        return iioMetadataNode;
    }
    
    @Override
    public Node appendChild(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("newChild == null!");
        }
        this.checkNode(node);
        return this.insertBefore(node, null);
    }
    
    @Override
    public boolean hasChildNodes() {
        return this.numChildren > 0;
    }
    
    @Override
    public Node cloneNode(final boolean b) {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode(this.nodeName);
        iioMetadataNode.setUserObject(this.getUserObject());
        if (b) {
            for (IIOMetadataNode iioMetadataNode2 = this.firstChild; iioMetadataNode2 != null; iioMetadataNode2 = iioMetadataNode2.nextSibling) {
                iioMetadataNode.appendChild(iioMetadataNode2.cloneNode(true));
            }
        }
        return iioMetadataNode;
    }
    
    @Override
    public void normalize() {
    }
    
    @Override
    public boolean isSupported(final String s, final String s2) {
        return false;
    }
    
    @Override
    public String getNamespaceURI() throws DOMException {
        return null;
    }
    
    @Override
    public String getPrefix() {
        return null;
    }
    
    @Override
    public void setPrefix(final String s) {
    }
    
    @Override
    public String getLocalName() {
        return this.nodeName;
    }
    
    @Override
    public String getTagName() {
        return this.nodeName;
    }
    
    @Override
    public String getAttribute(final String s) {
        final Attr attributeNode = this.getAttributeNode(s);
        if (attributeNode == null) {
            return "";
        }
        return attributeNode.getValue();
    }
    
    @Override
    public String getAttributeNS(final String s, final String s2) {
        return this.getAttribute(s2);
    }
    
    @Override
    public void setAttribute(final String s, final String s2) {
        boolean b = true;
        final char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            if (charArray[i] >= '\ufffe') {
                b = false;
                break;
            }
        }
        if (!b) {
            throw new IIODOMException((short)5, "Attribute name is illegal!");
        }
        this.removeAttribute(s, false);
        this.attributes.add(new IIOAttr(this, s, s2));
    }
    
    @Override
    public void setAttributeNS(final String s, final String s2, final String s3) {
        this.setAttribute(s2, s3);
    }
    
    @Override
    public void removeAttribute(final String s) {
        this.removeAttribute(s, true);
    }
    
    private void removeAttribute(final String s, final boolean b) {
        for (int size = this.attributes.size(), i = 0; i < size; ++i) {
            final IIOAttr iioAttr = this.attributes.get(i);
            if (s.equals(iioAttr.getName())) {
                iioAttr.setOwnerElement(null);
                this.attributes.remove(i);
                return;
            }
        }
        if (b) {
            throw new IIODOMException((short)8, "No such attribute!");
        }
    }
    
    @Override
    public void removeAttributeNS(final String s, final String s2) {
        this.removeAttribute(s2);
    }
    
    @Override
    public Attr getAttributeNode(final String s) {
        return (Attr)this.getAttributes().getNamedItem(s);
    }
    
    @Override
    public Attr getAttributeNodeNS(final String s, final String s2) {
        return this.getAttributeNode(s2);
    }
    
    @Override
    public Attr setAttributeNode(final Attr attr) throws DOMException {
        final Element ownerElement = attr.getOwnerElement();
        if (ownerElement == null) {
            IIOAttr iioAttr;
            if (attr instanceof IIOAttr) {
                iioAttr = (IIOAttr)attr;
                iioAttr.setOwnerElement(this);
            }
            else {
                iioAttr = new IIOAttr(this, attr.getName(), attr.getValue());
            }
            final Attr attributeNode = this.getAttributeNode(iioAttr.getName());
            if (attributeNode != null) {
                this.removeAttributeNode(attributeNode);
            }
            this.attributes.add(iioAttr);
            return attributeNode;
        }
        if (ownerElement == this) {
            return null;
        }
        throw new DOMException((short)10, "Attribute is already in use");
    }
    
    @Override
    public Attr setAttributeNodeNS(final Attr attributeNode) {
        return this.setAttributeNode(attributeNode);
    }
    
    @Override
    public Attr removeAttributeNode(final Attr attr) {
        this.removeAttribute(attr.getName());
        return attr;
    }
    
    @Override
    public NodeList getElementsByTagName(final String s) {
        final ArrayList list = new ArrayList();
        this.getElementsByTagName(s, list);
        return new IIONodeList(list);
    }
    
    private void getElementsByTagName(final String s, final List list) {
        if (this.nodeName.equals(s)) {
            list.add(this);
        }
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            ((IIOMetadataNode)node).getElementsByTagName(s, list);
        }
    }
    
    @Override
    public NodeList getElementsByTagNameNS(final String s, final String s2) {
        return this.getElementsByTagName(s2);
    }
    
    @Override
    public boolean hasAttributes() {
        return this.attributes.size() > 0;
    }
    
    @Override
    public boolean hasAttribute(final String s) {
        return this.getAttributeNode(s) != null;
    }
    
    @Override
    public boolean hasAttributeNS(final String s, final String s2) {
        return this.hasAttribute(s2);
    }
    
    @Override
    public int getLength() {
        return this.numChildren;
    }
    
    @Override
    public Node item(int n) {
        if (n < 0) {
            return null;
        }
        Node node;
        for (node = this.getFirstChild(); node != null && n-- > 0; node = node.getNextSibling()) {}
        return node;
    }
    
    public Object getUserObject() {
        return this.userObject;
    }
    
    public void setUserObject(final Object userObject) {
        this.userObject = userObject;
    }
    
    @Override
    public void setIdAttribute(final String s, final boolean b) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void setIdAttributeNS(final String s, final String s2, final boolean b) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void setIdAttributeNode(final Attr attr, final boolean b) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public TypeInfo getSchemaTypeInfo() throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Object setUserData(final String s, final Object o, final UserDataHandler userDataHandler) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Object getUserData(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Object getFeature(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public boolean isSameNode(final Node node) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public boolean isEqualNode(final Node node) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String lookupNamespaceURI(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public boolean isDefaultNamespace(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String lookupPrefix(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String getTextContent() throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public void setTextContent(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public short compareDocumentPosition(final Node node) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public String getBaseURI() throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
}
