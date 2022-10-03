package org.apache.xml.security.utils;

import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Text;
import org.apache.xml.security.exceptions.Base64DecodingException;
import java.math.BigInteger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.xml.security.exceptions.XMLSecurityException;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;

public abstract class ElementProxy
{
    static Log log;
    public static final int MODE_CREATE = 0;
    public static final int MODE_PROCESS = 1;
    public static final int MODE_UNKNOWN = 2;
    public static final int MODE_SIGN = 0;
    public static final int MODE_VERIFY = 1;
    public static final int MODE_ENCRYPT = 0;
    public static final int MODE_DECRYPT = 1;
    protected int _state;
    protected Element _constructionElement;
    protected String _baseURI;
    protected Document _doc;
    static HashMap _prefixMappings;
    static HashMap _prefixMappingsBindings;
    
    public abstract String getBaseNamespace();
    
    public abstract String getBaseLocalName();
    
    public ElementProxy() {
        this._state = 2;
        this._constructionElement = null;
        this._baseURI = null;
        this._doc = null;
    }
    
    public ElementProxy(final Document doc) {
        this._state = 2;
        this._constructionElement = null;
        this._baseURI = null;
        this._doc = null;
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        this._doc = doc;
        this._state = 0;
        this._constructionElement = this.createElementForFamilyLocal(this._doc, this.getBaseNamespace(), this.getBaseLocalName());
    }
    
    protected Element createElementForFamilyLocal(final Document document, final String s, final String s2) {
        Element element;
        if (s == null) {
            element = document.createElementNS(null, s2);
        }
        else {
            final String baseNamespace = this.getBaseNamespace();
            final String defaultPrefix = getDefaultPrefix(baseNamespace);
            if (defaultPrefix == null || defaultPrefix.length() == 0) {
                element = document.createElementNS(s, s2);
                element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", s);
            }
            else {
                final String defaultPrefixBindings = getDefaultPrefixBindings(baseNamespace);
                final StringBuffer sb = new StringBuffer(defaultPrefix);
                sb.append(':');
                sb.append(s2);
                element = document.createElementNS(s, sb.toString());
                element.setAttributeNS("http://www.w3.org/2000/xmlns/", defaultPrefixBindings, s);
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
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", getDefaultPrefixBindings(s), s);
        }
        return element;
    }
    
    public void setElement(final Element constructionElement, final String baseURI) throws XMLSecurityException {
        if (constructionElement == null) {
            throw new XMLSecurityException("ElementProxy.nullElement");
        }
        if (ElementProxy.log.isDebugEnabled()) {
            ElementProxy.log.debug((Object)("setElement(" + constructionElement.getTagName() + ", \"" + baseURI + "\""));
        }
        this._doc = constructionElement.getOwnerDocument();
        this._state = 1;
        this._constructionElement = constructionElement;
        this._baseURI = baseURI;
    }
    
    public ElementProxy(final Element constructionElement, final String baseURI) throws XMLSecurityException {
        this._state = 2;
        this._constructionElement = null;
        this._baseURI = null;
        this._doc = null;
        if (constructionElement == null) {
            throw new XMLSecurityException("ElementProxy.nullElement");
        }
        if (ElementProxy.log.isDebugEnabled()) {
            ElementProxy.log.debug((Object)("setElement(\"" + constructionElement.getTagName() + "\", \"" + baseURI + "\")"));
        }
        this._doc = constructionElement.getOwnerDocument();
        this._state = 1;
        this._constructionElement = constructionElement;
        this._baseURI = baseURI;
        this.guaranteeThatElementInCorrectSpace();
    }
    
    public final Element getElement() {
        return this._constructionElement;
    }
    
    public final NodeList getElementPlusReturns() {
        final HelperNodeList list = new HelperNodeList();
        list.appendChild(this._doc.createTextNode("\n"));
        list.appendChild(this.getElement());
        list.appendChild(this._doc.createTextNode("\n"));
        return list;
    }
    
    public Document getDocument() {
        return this._doc;
    }
    
    public String getBaseURI() {
        return this._baseURI;
    }
    
    public void guaranteeThatElementInCorrectSpace() throws XMLSecurityException {
        final String baseLocalName = this.getBaseLocalName();
        final String baseNamespace = this.getBaseNamespace();
        final String localName = this._constructionElement.getLocalName();
        final String namespaceURI = this._constructionElement.getNamespaceURI();
        if (baseNamespace != namespaceURI || !baseLocalName.equals(localName)) {
            throw new XMLSecurityException("xml.WrongElement", new Object[] { namespaceURI + ":" + localName, baseNamespace + ":" + baseLocalName });
        }
    }
    
    public void addBigIntegerElement(final BigInteger bigInteger, final String s) {
        if (bigInteger != null) {
            final Element elementInSignatureSpace = XMLUtils.createElementInSignatureSpace(this._doc, s);
            Base64.fillElementWithBigInteger(elementInSignatureSpace, bigInteger);
            this._constructionElement.appendChild(elementInSignatureSpace);
            XMLUtils.addReturnToElement(this._constructionElement);
        }
    }
    
    public void addBase64Element(final byte[] array, final String s) {
        if (array != null) {
            this._constructionElement.appendChild(Base64.encodeToElement(this._doc, s, array));
            this._constructionElement.appendChild(this._doc.createTextNode("\n"));
        }
    }
    
    public void addTextElement(final String s, final String s2) {
        final Element elementInSignatureSpace = XMLUtils.createElementInSignatureSpace(this._doc, s2);
        elementInSignatureSpace.appendChild(this._doc.createTextNode(s));
        this._constructionElement.appendChild(elementInSignatureSpace);
        XMLUtils.addReturnToElement(this._constructionElement);
    }
    
    public void addBase64Text(final byte[] array) {
        if (array != null) {
            this._constructionElement.appendChild(this._doc.createTextNode("\n" + Base64.encode(array) + "\n"));
        }
    }
    
    public void addText(final String s) {
        if (s != null) {
            this._constructionElement.appendChild(this._doc.createTextNode(s));
        }
    }
    
    public BigInteger getBigIntegerFromChildElement(final String s, final String s2) throws Base64DecodingException {
        return Base64.decodeBigIntegerFromText(XMLUtils.selectNodeText(this._constructionElement.getFirstChild(), s2, s, 0));
    }
    
    public byte[] getBytesFromChildElement(final String s, final String s2) throws XMLSecurityException {
        return Base64.decode(XMLUtils.selectNode(this._constructionElement.getFirstChild(), s2, s, 0));
    }
    
    public String getTextFromChildElement(final String s, final String s2) {
        return ((Text)XMLUtils.selectNode(this._constructionElement.getFirstChild(), s2, s, 0).getFirstChild()).getData();
    }
    
    public byte[] getBytesFromTextChild() throws XMLSecurityException {
        return Base64.decode(((Text)this._constructionElement.getFirstChild()).getData());
    }
    
    public String getTextFromTextChild() {
        return XMLUtils.getFullTextChildrenFromElement(this._constructionElement);
    }
    
    public int length(final String s, final String s2) {
        int n = 0;
        for (Node node = this._constructionElement.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (s2.equals(node.getLocalName()) && s == node.getNamespaceURI()) {
                ++n;
            }
        }
        return n;
    }
    
    public void setXPathNamespaceContext(final String s, final String s2) throws XMLSecurityException {
        if (s == null || s.length() == 0) {
            throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
        }
        if (s.equals("xmlns")) {
            throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
        }
        String string;
        if (s.startsWith("xmlns:")) {
            string = s;
        }
        else {
            string = "xmlns:" + s;
        }
        final Attr attributeNodeNS = this._constructionElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", string);
        if (attributeNodeNS == null) {
            this._constructionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", string, s2);
            return;
        }
        if (!attributeNodeNS.getNodeValue().equals(s2)) {
            throw new XMLSecurityException("namespacePrefixAlreadyUsedByOtherURI", new Object[] { string, this._constructionElement.getAttributeNS(null, string) });
        }
    }
    
    public static void setDefaultPrefix(final String s, final String dsPrefix) throws XMLSecurityException {
        if (ElementProxy._prefixMappings.containsValue(dsPrefix)) {
            final Object value = ElementProxy._prefixMappings.get(s);
            if (!value.equals(dsPrefix)) {
                throw new XMLSecurityException("prefix.AlreadyAssigned", new Object[] { dsPrefix, s, value });
            }
        }
        if ("http://www.w3.org/2000/09/xmldsig#".equals(s)) {
            XMLUtils.dsPrefix = dsPrefix;
            XMLUtils.xmlnsDsPrefix = "xmlns:" + dsPrefix;
        }
        ElementProxy._prefixMappings.put(s, dsPrefix.intern());
        ElementProxy._prefixMappingsBindings.put(s, ("xmlns:" + dsPrefix).intern());
    }
    
    public static String getDefaultPrefix(final String s) {
        return ElementProxy._prefixMappings.get(s);
    }
    
    public static String getDefaultPrefixBindings(final String s) {
        return ElementProxy._prefixMappingsBindings.get(s);
    }
    
    static {
        ElementProxy.log = LogFactory.getLog(ElementProxy.class.getName());
        ElementProxy._prefixMappings = new HashMap();
        ElementProxy._prefixMappingsBindings = new HashMap();
    }
}
