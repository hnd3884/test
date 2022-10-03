package org.apache.xml.security.utils;

import java.util.Arrays;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import java.lang.ref.WeakReference;
import org.w3c.dom.Element;
import java.util.List;
import java.util.WeakHashMap;
import org.apache.commons.logging.Log;

public class IdResolver
{
    private static Log log;
    private static WeakHashMap docMap;
    private static List names;
    private static int namesLength;
    
    private IdResolver() {
    }
    
    public static void registerElementById(final Element element, final String s) {
        final Document ownerDocument = element.getOwnerDocument();
        WeakHashMap weakHashMap = IdResolver.docMap.get(ownerDocument);
        if (weakHashMap == null) {
            weakHashMap = new WeakHashMap();
            IdResolver.docMap.put(ownerDocument, weakHashMap);
        }
        weakHashMap.put(s, new WeakReference(element));
    }
    
    public static void registerElementById(final Element element, final Attr attr) {
        registerElementById(element, attr.getNodeValue());
    }
    
    public static Element getElementById(final Document document, final String s) {
        final Element elementByIdType = getElementByIdType(document, s);
        if (elementByIdType != null) {
            IdResolver.log.debug((Object)("I could find an Element using the simple getElementByIdType method: " + elementByIdType.getTagName()));
            return elementByIdType;
        }
        final Element elementByIdUsingDOM = getElementByIdUsingDOM(document, s);
        if (elementByIdUsingDOM != null) {
            IdResolver.log.debug((Object)("I could find an Element using the simple getElementByIdUsingDOM method: " + elementByIdUsingDOM.getTagName()));
            return elementByIdUsingDOM;
        }
        final Element elementBySearching = getElementBySearching(document, s);
        if (elementBySearching != null) {
            registerElementById(elementBySearching, s);
            return elementBySearching;
        }
        return null;
    }
    
    private static Element getElementByIdUsingDOM(final Document document, final String s) {
        if (IdResolver.log.isDebugEnabled()) {
            IdResolver.log.debug((Object)("getElementByIdUsingDOM() Search for ID " + s));
        }
        return document.getElementById(s);
    }
    
    private static Element getElementByIdType(final Document document, final String s) {
        if (IdResolver.log.isDebugEnabled()) {
            IdResolver.log.debug((Object)("getElementByIdType() Search for ID " + s));
        }
        final WeakHashMap weakHashMap = IdResolver.docMap.get(document);
        if (weakHashMap != null) {
            final WeakReference weakReference = (WeakReference)weakHashMap.get(s);
            if (weakReference != null) {
                return (Element)weakReference.get();
            }
        }
        return null;
    }
    
    private static Element getElementBySearching(final Node node, final String s) {
        final Element[] array = new Element[IdResolver.namesLength + 1];
        getEl(node, s, array);
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                return array[i];
            }
        }
        return null;
    }
    
    private static int getEl(Node node, final String s, final Element[] array) {
        Node node2 = null;
        Node parentNode = null;
        while (true) {
            switch (node.getNodeType()) {
                case 9:
                case 11: {
                    node2 = node.getFirstChild();
                    break;
                }
                case 1: {
                    final Element element = (Element)node;
                    if (isElement(element, s, array) == 1) {
                        return 1;
                    }
                    node2 = node.getFirstChild();
                    if (node2 != null) {
                        parentNode = element;
                        break;
                    }
                    if (parentNode != null) {
                        node2 = node.getNextSibling();
                        break;
                    }
                    break;
                }
            }
            while (node2 == null && parentNode != null) {
                node2 = parentNode.getNextSibling();
                parentNode = parentNode.getParentNode();
                if (!(parentNode instanceof Element)) {
                    parentNode = null;
                }
            }
            if (node2 == null) {
                return 1;
            }
            node = node2;
            node2 = node.getNextSibling();
        }
    }
    
    public static int isElement(final Element element, final String s, final Element[] array) {
        if (!element.hasAttributes()) {
            return 0;
        }
        final NamedNodeMap attributes = element.getAttributes();
        final int index = IdResolver.names.indexOf(element.getNamespaceURI());
        final int n = (index < 0) ? IdResolver.namesLength : index;
        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
            final Attr attr = (Attr)attributes.item(i);
            final int n2 = (attr.getNamespaceURI() == null) ? n : IdResolver.names.indexOf(attr.getNamespaceURI());
            int n3 = (n2 < 0) ? IdResolver.namesLength : n2;
            final String localName = attr.getLocalName();
            if (localName.length() <= 2) {
                final String nodeValue = attr.getNodeValue();
                if (localName.charAt(0) == 'I') {
                    final char char1 = localName.charAt(1);
                    if (char1 == 'd' && nodeValue.equals(s)) {
                        array[n3] = element;
                        if (n3 == 0) {
                            return 1;
                        }
                    }
                    else if (char1 == 'D' && nodeValue.endsWith(s)) {
                        if (n3 != 3) {
                            n3 = IdResolver.namesLength;
                        }
                        array[n3] = element;
                    }
                }
                else if ("id".equals(localName) && nodeValue.equals(s)) {
                    if (n3 != 2) {
                        n3 = IdResolver.namesLength;
                    }
                    array[n3] = element;
                }
            }
        }
        if (n == 3 && (element.getAttribute("OriginalRequestID").equals(s) || element.getAttribute("RequestID").equals(s) || element.getAttribute("ResponseID").equals(s))) {
            array[3] = element;
        }
        else if (n == 4 && element.getAttribute("AssertionID").equals(s)) {
            array[4] = element;
        }
        else if (n == 5 && (element.getAttribute("RequestID").equals(s) || element.getAttribute("ResponseID").equals(s))) {
            array[5] = element;
        }
        return 0;
    }
    
    static {
        IdResolver.log = LogFactory.getLog(IdResolver.class.getName());
        IdResolver.docMap = new WeakHashMap();
        IdResolver.names = Arrays.asList("http://www.w3.org/2000/09/xmldsig#", "http://www.w3.org/2001/04/xmlenc#", "http://schemas.xmlsoap.org/soap/security/2000-12", "http://www.w3.org/2002/03/xkms#", "urn:oasis:names:tc:SAML:1.0:assertion", "urn:oasis:names:tc:SAML:1.0:protocol");
        IdResolver.namesLength = IdResolver.names.size();
    }
}
