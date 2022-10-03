package com.sun.xml.internal.messaging.saaj.soap.impl;

import org.w3c.dom.NamedNodeMap;
import java.net.URISyntaxException;
import java.net.URI;
import org.w3c.dom.DOMException;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;
import java.util.Iterator;
import org.w3c.dom.Attr;
import com.sun.xml.internal.messaging.saaj.util.NamespaceContextIterator;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.util.logging.Level;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import org.w3c.dom.Document;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

public class ElementImpl extends ElementNSImpl implements SOAPElement, SOAPBodyElement
{
    public static final String DSIG_NS;
    public static final String XENC_NS;
    public static final String WSU_NS;
    private AttributeManager encodingStyleAttribute;
    protected QName elementQName;
    protected static final Logger log;
    public static final String XMLNS_URI;
    public static final String XML_URI;
    
    public ElementImpl(final SOAPDocumentImpl ownerDoc, final Name name) {
        super(ownerDoc, name.getURI(), name.getQualifiedName(), name.getLocalName());
        this.encodingStyleAttribute = new AttributeManager();
        this.elementQName = NameImpl.convertToQName(name);
    }
    
    public ElementImpl(final SOAPDocumentImpl ownerDoc, final QName name) {
        super(ownerDoc, name.getNamespaceURI(), getQualifiedName(name), name.getLocalPart());
        this.encodingStyleAttribute = new AttributeManager();
        this.elementQName = name;
    }
    
    public ElementImpl(final SOAPDocumentImpl ownerDoc, final String uri, final String qualifiedName) {
        super(ownerDoc, uri, qualifiedName);
        this.encodingStyleAttribute = new AttributeManager();
        this.elementQName = new QName(uri, getLocalPart(qualifiedName), getPrefix(qualifiedName));
    }
    
    public void ensureNamespaceIsDeclared(final String prefix, final String uri) {
        final String alreadyDeclaredUri = this.getNamespaceURI(prefix);
        if (alreadyDeclaredUri != null) {
            if (alreadyDeclaredUri.equals(uri)) {
                return;
            }
        }
        try {
            this.addNamespaceDeclaration(prefix, uri);
        }
        catch (final SOAPException ex) {}
    }
    
    @Override
    public Document getOwnerDocument() {
        final Document doc = super.getOwnerDocument();
        if (doc instanceof SOAPDocument) {
            return ((SOAPDocument)doc).getDocument();
        }
        return doc;
    }
    
    @Override
    public SOAPElement addChildElement(final Name name) throws SOAPException {
        return this.addElement(name);
    }
    
    @Override
    public SOAPElement addChildElement(final QName qname) throws SOAPException {
        return this.addElement(qname);
    }
    
    @Override
    public SOAPElement addChildElement(final String localName) throws SOAPException {
        final String nsUri = this.getNamespaceURI("");
        final Name name = (nsUri == null || nsUri.isEmpty()) ? NameImpl.createFromUnqualifiedName(localName) : NameImpl.createFromQualifiedName(localName, nsUri);
        return this.addChildElement(name);
    }
    
    @Override
    public SOAPElement addChildElement(final String localName, final String prefix) throws SOAPException {
        final String uri = this.getNamespaceURI(prefix);
        if (uri == null) {
            ElementImpl.log.log(Level.SEVERE, "SAAJ0101.impl.parent.of.body.elem.mustbe.body", new String[] { prefix });
            throw new SOAPExceptionImpl("Unable to locate namespace for prefix " + prefix);
        }
        return this.addChildElement(localName, prefix, uri);
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        if ("xmlns".equals(prefix)) {
            return ElementImpl.XMLNS_URI;
        }
        if ("xml".equals(prefix)) {
            return ElementImpl.XML_URI;
        }
        if ("".equals(prefix)) {
            for (Node currentAncestor = this; currentAncestor != null && !(currentAncestor instanceof Document); currentAncestor = currentAncestor.getParentNode()) {
                if (currentAncestor instanceof ElementImpl) {
                    final QName name = ((ElementImpl)currentAncestor).getElementQName();
                    if (((Element)currentAncestor).hasAttributeNS(ElementImpl.XMLNS_URI, "xmlns")) {
                        final String uri = ((Element)currentAncestor).getAttributeNS(ElementImpl.XMLNS_URI, "xmlns");
                        if ("".equals(uri)) {
                            return null;
                        }
                        return uri;
                    }
                }
            }
        }
        else if (prefix != null) {
            for (Node currentAncestor = this; currentAncestor != null && !(currentAncestor instanceof Document); currentAncestor = currentAncestor.getParentNode()) {
                if (((Element)currentAncestor).hasAttributeNS(ElementImpl.XMLNS_URI, prefix)) {
                    return ((Element)currentAncestor).getAttributeNS(ElementImpl.XMLNS_URI, prefix);
                }
            }
        }
        return null;
    }
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        final ElementImpl copy = new ElementImpl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
        return replaceElementWithSOAPElement(this, copy);
    }
    
    @Override
    public QName createQName(final String localName, final String prefix) throws SOAPException {
        final String uri = this.getNamespaceURI(prefix);
        if (uri == null) {
            ElementImpl.log.log(Level.SEVERE, "SAAJ0102.impl.cannot.locate.ns", new Object[] { prefix });
            throw new SOAPException("Unable to locate namespace for prefix " + prefix);
        }
        return new QName(uri, localName, prefix);
    }
    
    public String getNamespacePrefix(final String uri) {
        final NamespaceContextIterator eachNamespace = this.getNamespaceContextNodes();
        while (eachNamespace.hasNext()) {
            final Attr namespaceDecl = eachNamespace.nextNamespaceAttr();
            if (namespaceDecl.getNodeValue().equals(uri)) {
                final String candidatePrefix = namespaceDecl.getLocalName();
                if ("xmlns".equals(candidatePrefix)) {
                    return "";
                }
                return candidatePrefix;
            }
        }
        for (Node currentAncestor = this; currentAncestor != null && !(currentAncestor instanceof Document); currentAncestor = currentAncestor.getParentNode()) {
            if (uri.equals(currentAncestor.getNamespaceURI())) {
                return currentAncestor.getPrefix();
            }
        }
        return null;
    }
    
    protected Attr getNamespaceAttr(String prefix) {
        final NamespaceContextIterator eachNamespace = this.getNamespaceContextNodes();
        if (!"".equals(prefix)) {
            prefix = ":" + prefix;
        }
        while (eachNamespace.hasNext()) {
            final Attr namespaceDecl = eachNamespace.nextNamespaceAttr();
            if (!"".equals(prefix)) {
                if (namespaceDecl.getNodeName().endsWith(prefix)) {
                    return namespaceDecl;
                }
                continue;
            }
            else {
                if (namespaceDecl.getNodeName().equals("xmlns")) {
                    return namespaceDecl;
                }
                continue;
            }
        }
        return null;
    }
    
    public NamespaceContextIterator getNamespaceContextNodes() {
        return this.getNamespaceContextNodes(true);
    }
    
    public NamespaceContextIterator getNamespaceContextNodes(final boolean traverseStack) {
        return new NamespaceContextIterator(this, traverseStack);
    }
    
    @Override
    public SOAPElement addChildElement(final String localName, final String prefix, final String uri) throws SOAPException {
        final SOAPElement newElement = this.createElement(NameImpl.create(localName, prefix, uri));
        this.addNode(newElement);
        return this.convertToSoapElement(newElement);
    }
    
    @Override
    public SOAPElement addChildElement(final SOAPElement element) throws SOAPException {
        final String elementURI = element.getElementName().getURI();
        final String localName = element.getLocalName();
        if ("http://schemas.xmlsoap.org/soap/envelope/".equals(elementURI) || "http://www.w3.org/2003/05/soap-envelope".equals(elementURI)) {
            if ("Envelope".equalsIgnoreCase(localName) || "Header".equalsIgnoreCase(localName) || "Body".equalsIgnoreCase(localName)) {
                ElementImpl.log.severe("SAAJ0103.impl.cannot.add.fragements");
                throw new SOAPExceptionImpl("Cannot add fragments which contain elements which are in the SOAP namespace");
            }
            if ("Fault".equalsIgnoreCase(localName) && !"Body".equalsIgnoreCase(this.getLocalName())) {
                ElementImpl.log.severe("SAAJ0154.impl.adding.fault.to.nonbody");
                throw new SOAPExceptionImpl("Cannot add a SOAPFault as a child of " + this.getLocalName());
            }
            if ("Detail".equalsIgnoreCase(localName) && !"Fault".equalsIgnoreCase(this.getLocalName())) {
                ElementImpl.log.severe("SAAJ0155.impl.adding.detail.nonfault");
                throw new SOAPExceptionImpl("Cannot add a Detail as a child of " + this.getLocalName());
            }
            if ("Fault".equalsIgnoreCase(localName)) {
                if (!elementURI.equals(this.getElementName().getURI())) {
                    ElementImpl.log.severe("SAAJ0158.impl.version.mismatch.fault");
                    throw new SOAPExceptionImpl("SOAP Version mismatch encountered when trying to add SOAPFault to SOAPBody");
                }
                final Iterator it = this.getChildElements();
                if (it.hasNext()) {
                    ElementImpl.log.severe("SAAJ0156.impl.adding.fault.error");
                    throw new SOAPExceptionImpl("Cannot add SOAPFault as a child of a non-Empty SOAPBody");
                }
            }
        }
        final String encodingStyle = element.getEncodingStyle();
        final ElementImpl importedElement = (ElementImpl)this.importElement(element);
        this.addNode(importedElement);
        if (encodingStyle != null) {
            importedElement.setEncodingStyle(encodingStyle);
        }
        return this.convertToSoapElement(importedElement);
    }
    
    protected Element importElement(final Element element) {
        final Document document = this.getOwnerDocument();
        final Document oldDocument = element.getOwnerDocument();
        if (!oldDocument.equals(document)) {
            return (Element)document.importNode(element, true);
        }
        return element;
    }
    
    protected SOAPElement addElement(final Name name) throws SOAPException {
        final SOAPElement newElement = this.createElement(name);
        this.addNode(newElement);
        return newElement;
    }
    
    protected SOAPElement addElement(final QName name) throws SOAPException {
        final SOAPElement newElement = this.createElement(name);
        this.addNode(newElement);
        return newElement;
    }
    
    protected SOAPElement createElement(final Name name) {
        if (this.isNamespaceQualified(name)) {
            return (SOAPElement)this.getOwnerDocument().createElementNS(name.getURI(), name.getQualifiedName());
        }
        return (SOAPElement)this.getOwnerDocument().createElement(name.getQualifiedName());
    }
    
    protected SOAPElement createElement(final QName name) {
        if (this.isNamespaceQualified(name)) {
            return (SOAPElement)this.getOwnerDocument().createElementNS(name.getNamespaceURI(), getQualifiedName(name));
        }
        return (SOAPElement)this.getOwnerDocument().createElement(getQualifiedName(name));
    }
    
    protected void addNode(final Node newElement) throws SOAPException {
        this.insertBefore(newElement, null);
        if (this.getOwnerDocument() instanceof DocumentFragment) {
            return;
        }
        if (newElement instanceof ElementImpl) {
            final ElementImpl element = (ElementImpl)newElement;
            final QName elementName = element.getElementQName();
            if (!"".equals(elementName.getNamespaceURI())) {
                element.ensureNamespaceIsDeclared(elementName.getPrefix(), elementName.getNamespaceURI());
            }
        }
    }
    
    protected SOAPElement findChild(final NameImpl name) {
        final Iterator eachChild = this.getChildElementNodes();
        while (eachChild.hasNext()) {
            final SOAPElement child = eachChild.next();
            if (child.getElementName().equals(name)) {
                return child;
            }
        }
        return null;
    }
    
    @Override
    public SOAPElement addTextNode(final String text) throws SOAPException {
        if (text.startsWith("<![CDATA[") || text.startsWith("<![cdata[")) {
            return this.addCDATA(text.substring("<![CDATA[".length(), text.length() - 3));
        }
        return this.addText(text);
    }
    
    protected SOAPElement addCDATA(final String text) throws SOAPException {
        final Text cdata = this.getOwnerDocument().createCDATASection(text);
        this.addNode(cdata);
        return this;
    }
    
    protected SOAPElement addText(final String text) throws SOAPException {
        final Text textNode = this.getOwnerDocument().createTextNode(text);
        this.addNode(textNode);
        return this;
    }
    
    @Override
    public SOAPElement addAttribute(final Name name, final String value) throws SOAPException {
        this.addAttributeBare(name, value);
        if (!"".equals(name.getURI())) {
            this.ensureNamespaceIsDeclared(name.getPrefix(), name.getURI());
        }
        return this;
    }
    
    @Override
    public SOAPElement addAttribute(final QName qname, final String value) throws SOAPException {
        this.addAttributeBare(qname, value);
        if (!"".equals(qname.getNamespaceURI())) {
            this.ensureNamespaceIsDeclared(qname.getPrefix(), qname.getNamespaceURI());
        }
        return this;
    }
    
    private void addAttributeBare(final Name name, final String value) {
        this.addAttributeBare(name.getURI(), name.getPrefix(), name.getQualifiedName(), value);
    }
    
    private void addAttributeBare(final QName name, final String value) {
        this.addAttributeBare(name.getNamespaceURI(), name.getPrefix(), getQualifiedName(name), value);
    }
    
    private void addAttributeBare(String uri, final String prefix, final String qualifiedName, final String value) {
        uri = ((uri.length() == 0) ? null : uri);
        if (qualifiedName.equals("xmlns")) {
            uri = ElementImpl.XMLNS_URI;
        }
        if (uri == null) {
            this.setAttribute(qualifiedName, value);
        }
        else {
            this.setAttributeNS(uri, qualifiedName, value);
        }
    }
    
    @Override
    public SOAPElement addNamespaceDeclaration(final String prefix, final String uri) throws SOAPException {
        if (prefix.length() > 0) {
            this.setAttributeNS(ElementImpl.XMLNS_URI, "xmlns:" + prefix, uri);
        }
        else {
            this.setAttributeNS(ElementImpl.XMLNS_URI, "xmlns", uri);
        }
        return this;
    }
    
    @Override
    public String getAttributeValue(final Name name) {
        return getAttributeValueFrom(this, name);
    }
    
    @Override
    public String getAttributeValue(final QName qname) {
        return getAttributeValueFrom(this, qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix(), getQualifiedName(qname));
    }
    
    @Override
    public Iterator getAllAttributes() {
        final Iterator i = getAllAttributesFrom(this);
        final ArrayList list = new ArrayList();
        while (i.hasNext()) {
            final Name name = i.next();
            if (!"xmlns".equalsIgnoreCase(name.getPrefix())) {
                list.add(name);
            }
        }
        return list.iterator();
    }
    
    @Override
    public Iterator getAllAttributesAsQNames() {
        final Iterator i = getAllAttributesFrom(this);
        final ArrayList list = new ArrayList();
        while (i.hasNext()) {
            final Name name = i.next();
            if (!"xmlns".equalsIgnoreCase(name.getPrefix())) {
                list.add(NameImpl.convertToQName(name));
            }
        }
        return list.iterator();
    }
    
    @Override
    public Iterator getNamespacePrefixes() {
        return this.doGetNamespacePrefixes(false);
    }
    
    @Override
    public Iterator getVisibleNamespacePrefixes() {
        return this.doGetNamespacePrefixes(true);
    }
    
    protected Iterator doGetNamespacePrefixes(final boolean deep) {
        return new Iterator() {
            String next = null;
            String last = null;
            NamespaceContextIterator eachNamespace = ElementImpl.this.getNamespaceContextNodes(deep);
            
            void findNext() {
                while (this.next == null && this.eachNamespace.hasNext()) {
                    final String attributeKey = this.eachNamespace.nextNamespaceAttr().getNodeName();
                    if (attributeKey.startsWith("xmlns:")) {
                        this.next = attributeKey.substring("xmlns:".length());
                    }
                }
            }
            
            @Override
            public boolean hasNext() {
                this.findNext();
                return this.next != null;
            }
            
            @Override
            public Object next() {
                this.findNext();
                if (this.next == null) {
                    throw new NoSuchElementException();
                }
                this.last = this.next;
                this.next = null;
                return this.last;
            }
            
            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                this.eachNamespace.remove();
                this.next = null;
                this.last = null;
            }
        };
    }
    
    @Override
    public Name getElementName() {
        return NameImpl.convertToName(this.elementQName);
    }
    
    @Override
    public QName getElementQName() {
        return this.elementQName;
    }
    
    @Override
    public boolean removeAttribute(final Name name) {
        return this.removeAttribute(name.getURI(), name.getLocalName());
    }
    
    @Override
    public boolean removeAttribute(final QName name) {
        return this.removeAttribute(name.getNamespaceURI(), name.getLocalPart());
    }
    
    private boolean removeAttribute(final String uri, final String localName) {
        final String nonzeroLengthUri = (uri == null || uri.length() == 0) ? null : uri;
        final Attr attribute = this.getAttributeNodeNS(nonzeroLengthUri, localName);
        if (attribute == null) {
            return false;
        }
        this.removeAttributeNode(attribute);
        return true;
    }
    
    @Override
    public boolean removeNamespaceDeclaration(final String prefix) {
        final Attr declaration = this.getNamespaceAttr(prefix);
        if (declaration == null) {
            return false;
        }
        try {
            this.removeAttributeNode(declaration);
        }
        catch (final DOMException ex) {}
        return true;
    }
    
    @Override
    public Iterator getChildElements() {
        return getChildElementsFrom(this);
    }
    
    protected SOAPElement convertToSoapElement(final Element element) {
        if (element instanceof SOAPElement) {
            return (SOAPElement)element;
        }
        return replaceElementWithSOAPElement(element, (ElementImpl)this.createElement(NameImpl.copyElementName(element)));
    }
    
    protected static SOAPElement replaceElementWithSOAPElement(final Element element, final ElementImpl copy) {
        final Iterator eachAttribute = getAllAttributesFrom(element);
        while (eachAttribute.hasNext()) {
            final Name name = eachAttribute.next();
            copy.addAttributeBare(name, getAttributeValueFrom(element, name));
        }
        final Iterator eachChild = getChildElementsFrom(element);
        while (eachChild.hasNext()) {
            final Node nextChild = eachChild.next();
            copy.insertBefore(nextChild, null);
        }
        final Node parent = element.getParentNode();
        if (parent != null) {
            parent.replaceChild(copy, element);
        }
        return copy;
    }
    
    protected Iterator getChildElementNodes() {
        return new Iterator() {
            Iterator eachNode = ElementImpl.this.getChildElements();
            Node next = null;
            Node last = null;
            
            @Override
            public boolean hasNext() {
                if (this.next == null) {
                    while (this.eachNode.hasNext()) {
                        final Node node = this.eachNode.next();
                        if (node instanceof SOAPElement) {
                            this.next = node;
                            break;
                        }
                    }
                }
                return this.next != null;
            }
            
            @Override
            public Object next() {
                if (this.hasNext()) {
                    this.last = this.next;
                    this.next = null;
                    return this.last;
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                final Node target = this.last;
                this.last = null;
                ElementImpl.this.removeChild(target);
            }
        };
    }
    
    @Override
    public Iterator getChildElements(final Name name) {
        return this.getChildElements(name.getURI(), name.getLocalName());
    }
    
    @Override
    public Iterator getChildElements(final QName qname) {
        return this.getChildElements(qname.getNamespaceURI(), qname.getLocalPart());
    }
    
    private Iterator getChildElements(final String nameUri, final String nameLocal) {
        return new Iterator() {
            Iterator eachElement = ElementImpl.this.getChildElementNodes();
            Node next = null;
            Node last = null;
            
            @Override
            public boolean hasNext() {
                if (this.next == null) {
                    while (this.eachElement.hasNext()) {
                        final Node element = this.eachElement.next();
                        String elementUri = element.getNamespaceURI();
                        elementUri = ((elementUri == null) ? "" : elementUri);
                        final String elementName = element.getLocalName();
                        if (elementUri.equals(nameUri) && elementName.equals(nameLocal)) {
                            this.next = element;
                            break;
                        }
                    }
                }
                return this.next != null;
            }
            
            @Override
            public Object next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.next;
                this.next = null;
                return this.last;
            }
            
            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                final Node target = this.last;
                this.last = null;
                ElementImpl.this.removeChild(target);
            }
        };
    }
    
    @Override
    public void removeContents() {
        Node temp;
        for (Node currentChild = this.getFirstChild(); currentChild != null; currentChild = temp) {
            temp = currentChild.getNextSibling();
            if (currentChild instanceof javax.xml.soap.Node) {
                ((javax.xml.soap.Node)currentChild).detachNode();
            }
            else {
                final Node parent = currentChild.getParentNode();
                if (parent != null) {
                    parent.removeChild(currentChild);
                }
            }
        }
    }
    
    @Override
    public void setEncodingStyle(final String encodingStyle) throws SOAPException {
        if (!"".equals(encodingStyle)) {
            try {
                new URI(encodingStyle);
            }
            catch (final URISyntaxException m) {
                ElementImpl.log.log(Level.SEVERE, "SAAJ0105.impl.encoding.style.mustbe.valid.URI", new String[] { encodingStyle });
                throw new IllegalArgumentException("Encoding style (" + encodingStyle + ") should be a valid URI");
            }
        }
        this.encodingStyleAttribute.setValue(encodingStyle);
        this.tryToFindEncodingStyleAttributeName();
    }
    
    @Override
    public String getEncodingStyle() {
        String encodingStyle = this.encodingStyleAttribute.getValue();
        if (encodingStyle != null) {
            return encodingStyle;
        }
        final String soapNamespace = this.getSOAPNamespace();
        if (soapNamespace != null) {
            final Attr attr = this.getAttributeNodeNS(soapNamespace, "encodingStyle");
            if (attr != null) {
                encodingStyle = attr.getValue();
                try {
                    this.setEncodingStyle(encodingStyle);
                }
                catch (final SOAPException ex) {}
                return encodingStyle;
            }
        }
        return null;
    }
    
    @Override
    public String getValue() {
        final javax.xml.soap.Node valueNode = this.getValueNode();
        return (valueNode == null) ? null : valueNode.getValue();
    }
    
    @Override
    public void setValue(final String value) {
        final Node valueNode = this.getValueNodeStrict();
        if (valueNode != null) {
            valueNode.setNodeValue(value);
        }
        else {
            try {
                this.addTextNode(value);
            }
            catch (final SOAPException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
    
    protected Node getValueNodeStrict() {
        final Node node = this.getFirstChild();
        if (node == null) {
            return null;
        }
        if (node.getNextSibling() == null && node.getNodeType() == 3) {
            return node;
        }
        ElementImpl.log.severe("SAAJ0107.impl.elem.child.not.single.text");
        throw new IllegalStateException();
    }
    
    protected javax.xml.soap.Node getValueNode() {
        final Iterator i = this.getChildElements();
        while (i.hasNext()) {
            final javax.xml.soap.Node n = i.next();
            if (n.getNodeType() == 3 || n.getNodeType() == 4) {
                this.normalize();
                return n;
            }
        }
        return null;
    }
    
    @Override
    public void setParentElement(final SOAPElement element) throws SOAPException {
        if (element == null) {
            ElementImpl.log.severe("SAAJ0106.impl.no.null.to.parent.elem");
            throw new SOAPException("Cannot pass NULL to setParentElement");
        }
        element.addChildElement(this);
        this.findEncodingStyleAttributeName();
    }
    
    protected void findEncodingStyleAttributeName() throws SOAPException {
        final String soapNamespace = this.getSOAPNamespace();
        if (soapNamespace != null) {
            final String soapNamespacePrefix = this.getNamespacePrefix(soapNamespace);
            if (soapNamespacePrefix != null) {
                this.setEncodingStyleNamespace(soapNamespace, soapNamespacePrefix);
            }
        }
    }
    
    protected void setEncodingStyleNamespace(final String soapNamespace, final String soapNamespacePrefix) throws SOAPException {
        final Name encodingStyleAttributeName = NameImpl.create("encodingStyle", soapNamespacePrefix, soapNamespace);
        this.encodingStyleAttribute.setName(encodingStyleAttributeName);
    }
    
    @Override
    public SOAPElement getParentElement() {
        final Node parentNode = this.getParentNode();
        if (parentNode instanceof SOAPDocument) {
            return null;
        }
        return (SOAPElement)parentNode;
    }
    
    protected String getSOAPNamespace() {
        String soapNamespace = null;
        for (SOAPElement antecedent = this; antecedent != null; antecedent = antecedent.getParentElement()) {
            final Name antecedentName = antecedent.getElementName();
            final String antecedentNamespace = antecedentName.getURI();
            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(antecedentNamespace) || "http://www.w3.org/2003/05/soap-envelope".equals(antecedentNamespace)) {
                soapNamespace = antecedentNamespace;
                break;
            }
        }
        return soapNamespace;
    }
    
    @Override
    public void detachNode() {
        final Node parent = this.getParentNode();
        if (parent != null) {
            parent.removeChild(this);
        }
        this.encodingStyleAttribute.clearNameAndValue();
    }
    
    public void tryToFindEncodingStyleAttributeName() {
        try {
            this.findEncodingStyleAttributeName();
        }
        catch (final SOAPException ex) {}
    }
    
    @Override
    public void recycleNode() {
        this.detachNode();
    }
    
    protected static Attr getNamespaceAttrFrom(final Element element, final String prefix) {
        final NamespaceContextIterator eachNamespace = new NamespaceContextIterator(element);
        while (eachNamespace.hasNext()) {
            final Attr namespaceDecl = eachNamespace.nextNamespaceAttr();
            final String declaredPrefix = NameImpl.getLocalNameFromTagName(namespaceDecl.getNodeName());
            if (declaredPrefix.equals(prefix)) {
                return namespaceDecl;
            }
        }
        return null;
    }
    
    protected static Iterator getAllAttributesFrom(final Element element) {
        final NamedNodeMap attributes = element.getAttributes();
        return new Iterator() {
            int attributesLength = attributes.getLength();
            int attributeIndex = 0;
            String currentName;
            
            @Override
            public boolean hasNext() {
                return this.attributeIndex < this.attributesLength;
            }
            
            @Override
            public Object next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                final Node current = attributes.item(this.attributeIndex++);
                this.currentName = current.getNodeName();
                final String prefix = NameImpl.getPrefixFromTagName(this.currentName);
                if (prefix.length() == 0) {
                    return NameImpl.createFromUnqualifiedName(this.currentName);
                }
                final Name attributeName = NameImpl.createFromQualifiedName(this.currentName, current.getNamespaceURI());
                return attributeName;
            }
            
            @Override
            public void remove() {
                if (this.currentName == null) {
                    throw new IllegalStateException();
                }
                attributes.removeNamedItem(this.currentName);
            }
        };
    }
    
    protected static String getAttributeValueFrom(final Element element, final Name name) {
        return getAttributeValueFrom(element, name.getURI(), name.getLocalName(), name.getPrefix(), name.getQualifiedName());
    }
    
    private static String getAttributeValueFrom(final Element element, final String uri, final String localName, final String prefix, final String qualifiedName) {
        final String nonzeroLengthUri = (uri == null || uri.length() == 0) ? null : uri;
        final boolean mustUseGetAttributeNodeNS = nonzeroLengthUri != null;
        if (!mustUseGetAttributeNodeNS) {
            Attr attribute = null;
            attribute = element.getAttributeNode(qualifiedName);
            return (attribute == null) ? null : attribute.getValue();
        }
        if (!element.hasAttributeNS(uri, localName)) {
            return null;
        }
        final String attrValue = element.getAttributeNS(nonzeroLengthUri, localName);
        return attrValue;
    }
    
    protected static Iterator getChildElementsFrom(final Element element) {
        return new Iterator() {
            Node next = element.getFirstChild();
            Node nextNext = null;
            Node last = null;
            
            @Override
            public boolean hasNext() {
                if (this.next != null) {
                    return true;
                }
                if (this.next == null && this.nextNext != null) {
                    this.next = this.nextNext;
                }
                return this.next != null;
            }
            
            @Override
            public Object next() {
                if (this.hasNext()) {
                    this.last = this.next;
                    this.next = null;
                    if (element instanceof ElementImpl && this.last instanceof Element) {
                        this.last = ((ElementImpl)element).convertToSoapElement((Element)this.last);
                    }
                    this.nextNext = this.last.getNextSibling();
                    return this.last;
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                final Node target = this.last;
                this.last = null;
                element.removeChild(target);
            }
        };
    }
    
    public static String getQualifiedName(final QName name) {
        final String prefix = name.getPrefix();
        final String localName = name.getLocalPart();
        String qualifiedName = null;
        if (prefix != null && prefix.length() > 0) {
            qualifiedName = prefix + ":" + localName;
        }
        else {
            qualifiedName = localName;
        }
        return qualifiedName;
    }
    
    public static String getLocalPart(final String qualifiedName) {
        if (qualifiedName == null) {
            throw new IllegalArgumentException("Cannot get local name for a \"null\" qualified name");
        }
        final int index = qualifiedName.indexOf(58);
        if (index < 0) {
            return qualifiedName;
        }
        return qualifiedName.substring(index + 1);
    }
    
    public static String getPrefix(final String qualifiedName) {
        if (qualifiedName == null) {
            throw new IllegalArgumentException("Cannot get prefix for a  \"null\" qualified name");
        }
        final int index = qualifiedName.indexOf(58);
        if (index < 0) {
            return "";
        }
        return qualifiedName.substring(0, index);
    }
    
    protected boolean isNamespaceQualified(final Name name) {
        return !"".equals(name.getURI());
    }
    
    protected boolean isNamespaceQualified(final QName name) {
        return !"".equals(name.getNamespaceURI());
    }
    
    @Override
    public void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value) {
        final int index = qualifiedName.indexOf(58);
        String localName;
        if (index < 0) {
            localName = qualifiedName;
        }
        else {
            localName = qualifiedName.substring(index + 1);
        }
        super.setAttributeNS(namespaceURI, qualifiedName, value);
        final String tmpURI = this.getNamespaceURI();
        boolean isIDNS = false;
        if (tmpURI != null && (tmpURI.equals(ElementImpl.DSIG_NS) || tmpURI.equals(ElementImpl.XENC_NS))) {
            isIDNS = true;
        }
        if (localName.equals("Id")) {
            if (namespaceURI == null || namespaceURI.equals("")) {
                this.setIdAttribute(localName, true);
            }
            else if (isIDNS || ElementImpl.WSU_NS.equals(namespaceURI)) {
                this.setIdAttributeNS(namespaceURI, localName, true);
            }
        }
    }
    
    static {
        DSIG_NS = "http://www.w3.org/2000/09/xmldsig#".intern();
        XENC_NS = "http://www.w3.org/2001/04/xmlenc#".intern();
        WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".intern();
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
        XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();
        XML_URI = "http://www.w3.org/XML/1998/namespace".intern();
    }
    
    class AttributeManager
    {
        Name attributeName;
        String attributeValue;
        
        AttributeManager() {
            this.attributeName = null;
            this.attributeValue = null;
        }
        
        public void setName(final Name newName) throws SOAPException {
            this.clearAttribute();
            this.attributeName = newName;
            this.reconcileAttribute();
        }
        
        public void clearName() {
            this.clearAttribute();
            this.attributeName = null;
        }
        
        public void setValue(final String value) throws SOAPException {
            this.attributeValue = value;
            this.reconcileAttribute();
        }
        
        public Name getName() {
            return this.attributeName;
        }
        
        public String getValue() {
            return this.attributeValue;
        }
        
        public void clearNameAndValue() {
            this.attributeName = null;
            this.attributeValue = null;
        }
        
        private void reconcileAttribute() throws SOAPException {
            if (this.attributeName != null) {
                ElementImpl.this.removeAttribute(this.attributeName);
                if (this.attributeValue != null) {
                    ElementImpl.this.addAttribute(this.attributeName, this.attributeValue);
                }
            }
        }
        
        private void clearAttribute() {
            if (this.attributeName != null) {
                ElementImpl.this.removeAttribute(this.attributeName);
            }
        }
    }
}
