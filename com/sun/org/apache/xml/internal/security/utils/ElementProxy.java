package com.sun.org.apache.xml.internal.security.utils;

import java.util.concurrent.ConcurrentHashMap;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.w3c.dom.Attr;
import java.math.BigInteger;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.org.slf4j.internal.Logger;

public abstract class ElementProxy
{
    protected static final Logger LOG;
    private Element wrappedElement;
    protected String baseURI;
    private Document wrappedDoc;
    private static Map<String, String> prefixMappings;
    
    public ElementProxy() {
    }
    
    public ElementProxy(final Document wrappedDoc) {
        if (wrappedDoc == null) {
            throw new RuntimeException("Document is null");
        }
        this.wrappedDoc = wrappedDoc;
        this.wrappedElement = this.createElementForFamilyLocal(this.getBaseNamespace(), this.getBaseLocalName());
    }
    
    public ElementProxy(final Element element, final String baseURI) throws XMLSecurityException {
        if (element == null) {
            throw new XMLSecurityException("ElementProxy.nullElement");
        }
        ElementProxy.LOG.debug("setElement(\"{}\", \"{}\")", element.getTagName(), baseURI);
        this.setElement(element);
        this.baseURI = baseURI;
        this.guaranteeThatElementInCorrectSpace();
    }
    
    public abstract String getBaseNamespace();
    
    public abstract String getBaseLocalName();
    
    protected Element createElementForFamilyLocal(final String s, final String s2) {
        final Document document = this.getDocument();
        Element element;
        if (s == null) {
            element = document.createElementNS(null, s2);
        }
        else {
            final String defaultPrefix = getDefaultPrefix(this.getBaseNamespace());
            if (defaultPrefix == null || defaultPrefix.length() == 0) {
                element = document.createElementNS(s, s2);
                element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", s);
            }
            else {
                element = document.createElementNS(s, defaultPrefix + ":" + s2);
                element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + defaultPrefix, s);
            }
        }
        return element;
    }
    
    public static Element createElementForFamily(final Document document, final String s, final String s2) {
        final String defaultPrefix = getDefaultPrefix(s);
        Element element;
        if (s == null) {
            element = document.createElementNS(null, s2);
        }
        else if (defaultPrefix == null || defaultPrefix.length() == 0) {
            element = document.createElementNS(s, s2);
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", s);
        }
        else {
            element = document.createElementNS(s, defaultPrefix + ":" + s2);
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + defaultPrefix, s);
        }
        return element;
    }
    
    public void setElement(final Element element, final String baseURI) throws XMLSecurityException {
        if (element == null) {
            throw new XMLSecurityException("ElementProxy.nullElement");
        }
        ElementProxy.LOG.debug("setElement({}, \"{}\")", element.getTagName(), baseURI);
        this.setElement(element);
        this.baseURI = baseURI;
    }
    
    public final Element getElement() {
        return this.wrappedElement;
    }
    
    public final NodeList getElementPlusReturns() {
        final HelperNodeList list = new HelperNodeList();
        list.appendChild(this.createText("\n"));
        list.appendChild(this.getElement());
        list.appendChild(this.createText("\n"));
        return list;
    }
    
    protected Text createText(final String s) {
        return this.wrappedDoc.createTextNode(s);
    }
    
    public Document getDocument() {
        if (this.wrappedDoc == null) {
            this.wrappedDoc = XMLUtils.getOwnerDocument(this.wrappedElement);
        }
        return this.wrappedDoc;
    }
    
    public String getBaseURI() {
        return this.baseURI;
    }
    
    void guaranteeThatElementInCorrectSpace() throws XMLSecurityException {
        final String baseLocalName = this.getBaseLocalName();
        final String baseNamespace = this.getBaseNamespace();
        final String localName = this.getElement().getLocalName();
        final String namespaceURI = this.getElement().getNamespaceURI();
        if (!baseNamespace.equals(namespaceURI) && !baseLocalName.equals(localName)) {
            throw new XMLSecurityException("xml.WrongElement", new Object[] { namespaceURI + ":" + localName, baseNamespace + ":" + baseLocalName });
        }
    }
    
    public void addBigIntegerElement(final BigInteger bigInteger, final String s) {
        if (bigInteger != null) {
            final Element elementInSignatureSpace = XMLUtils.createElementInSignatureSpace(this.getDocument(), s);
            elementInSignatureSpace.appendChild(elementInSignatureSpace.getOwnerDocument().createTextNode(XMLUtils.encodeToString(XMLUtils.getBytes(bigInteger, bigInteger.bitLength()))));
            this.appendSelf(elementInSignatureSpace);
            this.addReturnToSelf();
        }
    }
    
    protected void addReturnToSelf() {
        XMLUtils.addReturnToElement(this.getElement());
    }
    
    public void addBase64Element(final byte[] array, final String s) {
        if (array != null) {
            final Element elementInSignatureSpace = XMLUtils.createElementInSignatureSpace(this.getDocument(), s);
            elementInSignatureSpace.appendChild(this.getDocument().createTextNode(XMLUtils.encodeToString(array)));
            this.appendSelf(elementInSignatureSpace);
            if (!XMLUtils.ignoreLineBreaks()) {
                this.appendSelf(this.createText("\n"));
            }
        }
    }
    
    public void addTextElement(final String s, final String s2) {
        final Element elementInSignatureSpace = XMLUtils.createElementInSignatureSpace(this.getDocument(), s2);
        this.appendOther(elementInSignatureSpace, this.createText(s));
        this.appendSelf(elementInSignatureSpace);
        this.addReturnToSelf();
    }
    
    public void addBase64Text(final byte[] array) {
        if (array != null) {
            this.appendSelf(XMLUtils.ignoreLineBreaks() ? this.createText(XMLUtils.encodeToString(array)) : this.createText("\n" + XMLUtils.encodeToString(array) + "\n"));
        }
    }
    
    protected void appendSelf(final ElementProxy elementProxy) {
        this.getElement().appendChild(elementProxy.getElement());
    }
    
    protected void appendSelf(final Node node) {
        this.getElement().appendChild(node);
    }
    
    protected void appendOther(final Element element, final Node node) {
        element.appendChild(node);
    }
    
    public void addText(final String s) {
        if (s != null) {
            this.appendSelf(this.createText(s));
        }
    }
    
    public BigInteger getBigIntegerFromChildElement(final String s, final String s2) {
        final Element selectNode = XMLUtils.selectNode(this.getFirstChild(), s2, s, 0);
        if (selectNode != null) {
            return new BigInteger(1, XMLUtils.decode(XMLUtils.getFullTextChildrenFromNode(selectNode)));
        }
        return null;
    }
    
    public String getTextFromChildElement(final String s, final String s2) {
        return XMLUtils.selectNode(this.getFirstChild(), s2, s, 0).getTextContent();
    }
    
    public byte[] getBytesFromTextChild() throws XMLSecurityException {
        return XMLUtils.decode(this.getTextFromTextChild());
    }
    
    public String getTextFromTextChild() {
        return XMLUtils.getFullTextChildrenFromNode(this.getElement());
    }
    
    public int length(final String s, final String s2) {
        int n = 0;
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (s2.equals(node.getLocalName()) && s.equals(node.getNamespaceURI())) {
                ++n;
            }
        }
        return n;
    }
    
    public void setXPathNamespaceContext(final String s, final String s2) throws XMLSecurityException {
        if (s == null || s.length() == 0) {
            throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
        }
        if ("xmlns".equals(s)) {
            throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
        }
        String string;
        if (s.startsWith("xmlns:")) {
            string = s;
        }
        else {
            string = "xmlns:" + s;
        }
        final Attr attributeNodeNS = this.getElement().getAttributeNodeNS("http://www.w3.org/2000/xmlns/", string);
        if (attributeNodeNS == null) {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", string, s2);
            return;
        }
        if (!attributeNodeNS.getNodeValue().equals(s2)) {
            throw new XMLSecurityException("namespacePrefixAlreadyUsedByOtherURI", new Object[] { string, this.getElement().getAttributeNS(null, string) });
        }
    }
    
    public static void setDefaultPrefix(final String s, final String s2) throws XMLSecurityException {
        JavaUtils.checkRegisterPermission();
        setNamespacePrefix(s, s2);
    }
    
    private static void setNamespacePrefix(final String s, final String xencPrefix) throws XMLSecurityException {
        if (ElementProxy.prefixMappings.containsValue(xencPrefix)) {
            final String s2 = ElementProxy.prefixMappings.get(s);
            if (!s2.equals(xencPrefix)) {
                throw new XMLSecurityException("prefix.AlreadyAssigned", new Object[] { xencPrefix, s, s2 });
            }
        }
        if ("http://www.w3.org/2000/09/xmldsig#".equals(s)) {
            XMLUtils.setDsPrefix(xencPrefix);
        }
        else if ("http://www.w3.org/2009/xmldsig11#".equals(s)) {
            XMLUtils.setDs11Prefix(xencPrefix);
        }
        else if ("http://www.w3.org/2001/04/xmlenc#".equals(s)) {
            XMLUtils.setXencPrefix(xencPrefix);
        }
        ElementProxy.prefixMappings.put(s, xencPrefix);
    }
    
    public static void registerDefaultPrefixes() throws XMLSecurityException {
        setNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", "ds");
        setNamespacePrefix("http://www.w3.org/2001/04/xmlenc#", "xenc");
        setNamespacePrefix("http://www.w3.org/2009/xmlenc11#", "xenc11");
        setNamespacePrefix("http://www.xmlsecurity.org/experimental#", "experimental");
        setNamespacePrefix("http://www.w3.org/2002/04/xmldsig-filter2", "dsig-xpath-old");
        setNamespacePrefix("http://www.w3.org/2002/06/xmldsig-filter2", "dsig-xpath");
        setNamespacePrefix("http://www.w3.org/2001/10/xml-exc-c14n#", "ec");
        setNamespacePrefix("http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter", "xx");
        setNamespacePrefix("http://www.w3.org/2009/xmldsig11#", "dsig11");
    }
    
    public static String getDefaultPrefix(final String s) {
        return ElementProxy.prefixMappings.get(s);
    }
    
    protected void setElement(final Element wrappedElement) {
        this.wrappedElement = wrappedElement;
    }
    
    protected void setDocument(final Document wrappedDoc) {
        this.wrappedDoc = wrappedDoc;
    }
    
    protected String getLocalAttribute(final String s) {
        return this.getElement().getAttributeNS(null, s);
    }
    
    protected void setLocalAttribute(final String s, final String s2) {
        this.getElement().setAttributeNS(null, s, s2);
    }
    
    protected void setLocalIdAttribute(final String s, final String value) {
        if (value != null) {
            final Attr attributeNS = this.getDocument().createAttributeNS(null, s);
            attributeNS.setValue(value);
            this.getElement().setAttributeNodeNS(attributeNS);
            this.getElement().setIdAttributeNode(attributeNS, true);
        }
        else {
            this.getElement().removeAttributeNS(null, s);
        }
    }
    
    protected Node getFirstChild() {
        return this.getElement().getFirstChild();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ElementProxy.class);
        ElementProxy.prefixMappings = new ConcurrentHashMap<String, String>();
    }
}
