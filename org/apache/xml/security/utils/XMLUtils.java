package org.apache.xml.security.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import java.io.IOException;
import org.apache.xml.security.c14n.Canonicalizer;
import java.io.OutputStream;
import org.w3c.dom.NamedNodeMap;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.util.Map;

public class XMLUtils
{
    static String dsPrefix;
    static String xmlnsDsPrefix;
    static Map namePrefixes;
    
    private XMLUtils() {
    }
    
    public static Element getNextElement(Node nextSibling) {
        while (nextSibling != null && nextSibling.getNodeType() != 1) {
            nextSibling = nextSibling.getNextSibling();
        }
        return (Element)nextSibling;
    }
    
    public static void getSet(final Node node, final Set set, final Node node2, final boolean b) {
        if (node2 != null && isDescendantOrSelf(node2, node)) {
            return;
        }
        getSetRec(node, set, node2, b);
    }
    
    static final void getSetRec(final Node node, final Set set, final Node node2, final boolean b) {
        if (node == node2) {
            return;
        }
        switch (node.getNodeType()) {
            case 1: {
                set.add(node);
                if (node.hasAttributes()) {
                    final NamedNodeMap attributes = node.getAttributes();
                    for (int i = 0; i < attributes.getLength(); ++i) {
                        set.add(attributes.item(i));
                    }
                }
            }
            case 9: {
                for (Node node3 = node.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                    if (node3.getNodeType() == 3) {
                        set.add(node3);
                        while (node3 != null && node3.getNodeType() == 3) {
                            node3 = node3.getNextSibling();
                        }
                        if (node3 == null) {
                            return;
                        }
                    }
                    getSetRec(node3, set, node2, b);
                }
                return;
            }
            case 8: {
                if (b) {
                    set.add(node);
                }
                return;
            }
            case 10: {
                return;
            }
            default: {
                set.add(node);
            }
        }
    }
    
    public static void outputDOM(final Node node, final OutputStream outputStream) {
        outputDOM(node, outputStream, false);
    }
    
    public static void outputDOM(final Node node, final OutputStream outputStream, final boolean b) {
        try {
            if (b) {
                outputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
            }
            outputStream.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(node));
        }
        catch (final IOException ex) {}
        catch (final InvalidCanonicalizerException ex2) {
            ex2.printStackTrace();
        }
        catch (final CanonicalizationException ex3) {
            ex3.printStackTrace();
        }
    }
    
    public static void outputDOMc14nWithComments(final Node node, final OutputStream outputStream) {
        try {
            outputStream.write(Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(node));
        }
        catch (final IOException ex) {}
        catch (final InvalidCanonicalizerException ex2) {}
        catch (final CanonicalizationException ex3) {}
    }
    
    public static String getFullTextChildrenFromElement(final Element element) {
        final StringBuffer sb = new StringBuffer();
        final NodeList childNodes = element.getChildNodes();
        for (int length = childNodes.getLength(), i = 0; i < length; ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 3) {
                sb.append(((Text)item).getData());
            }
        }
        return sb.toString();
    }
    
    public static Element createElementInSignatureSpace(final Document document, final String s) {
        if (document == null) {
            throw new RuntimeException("Document is null");
        }
        if (XMLUtils.dsPrefix == null || XMLUtils.dsPrefix.length() == 0) {
            final Element elementNS = document.createElementNS("http://www.w3.org/2000/09/xmldsig#", s);
            elementNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
            return elementNS;
        }
        String string = XMLUtils.namePrefixes.get(s);
        if (string == null) {
            final StringBuffer sb = new StringBuffer(XMLUtils.dsPrefix);
            sb.append(':');
            sb.append(s);
            string = sb.toString();
            XMLUtils.namePrefixes.put(s, string);
        }
        final Element elementNS2 = document.createElementNS("http://www.w3.org/2000/09/xmldsig#", string);
        elementNS2.setAttributeNS("http://www.w3.org/2000/xmlns/", XMLUtils.xmlnsDsPrefix, "http://www.w3.org/2000/09/xmldsig#");
        return elementNS2;
    }
    
    public static boolean elementIsInSignatureSpace(final Element element, final String s) {
        return element != null && "http://www.w3.org/2000/09/xmldsig#" == element.getNamespaceURI() && element.getLocalName().equals(s);
    }
    
    public static boolean elementIsInEncryptionSpace(final Element element, final String s) {
        return element != null && "http://www.w3.org/2001/04/xmlenc#" == element.getNamespaceURI() && element.getLocalName().equals(s);
    }
    
    public static Document getOwnerDocument(final Node node) {
        if (node.getNodeType() == 9) {
            return (Document)node;
        }
        try {
            return node.getOwnerDocument();
        }
        catch (final NullPointerException ex) {
            throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + ex.getMessage() + "\"");
        }
    }
    
    public static Document getOwnerDocument(final Set set) {
        Throwable t = null;
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            final Node node = (Node)iterator.next();
            final short nodeType = node.getNodeType();
            if (nodeType == 9) {
                return (Document)node;
            }
            try {
                if (nodeType == 2) {
                    return ((Attr)node).getOwnerElement().getOwnerDocument();
                }
                return node.getOwnerDocument();
            }
            catch (final NullPointerException ex) {
                t = ex;
                continue;
            }
            break;
        }
        throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + ((t == null) ? "" : t.getMessage()) + "\"");
    }
    
    public static Element createDSctx(final Document document, final String s, final String s2) {
        if (s == null || s.trim().length() == 0) {
            throw new IllegalArgumentException("You must supply a prefix");
        }
        final Element elementNS = document.createElementNS(null, "namespaceContext");
        elementNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + s.trim(), s2);
        return elementNS;
    }
    
    public static void addReturnToElement(final Element element) {
        element.appendChild(element.getOwnerDocument().createTextNode("\n"));
    }
    
    public static Set convertNodelistToSet(final NodeList list) {
        if (list == null) {
            return new HashSet();
        }
        final int length = list.getLength();
        final HashSet set = new HashSet(length);
        for (int i = 0; i < length; ++i) {
            set.add((Object)list.item(i));
        }
        return set;
    }
    
    public static void circumventBug2650(final Document document) {
        final Element documentElement = document.getDocumentElement();
        if (documentElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns") == null) {
            documentElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
        }
        circumventBug2650internal(document);
    }
    
    private static void circumventBug2650internal(Node node) {
        Node parentNode = null;
        Node node2 = null;
        while (true) {
            switch (node.getNodeType()) {
                case 1: {
                    final Element element = (Element)node;
                    if (!element.hasChildNodes()) {
                        break;
                    }
                    if (element.hasAttributes()) {
                        final NamedNodeMap attributes = element.getAttributes();
                        final int length = attributes.getLength();
                        for (Node node3 = element.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                            if (node3.getNodeType() == 1) {
                                final Element element2 = (Element)node3;
                                for (int i = 0; i < length; ++i) {
                                    final Attr attr = (Attr)attributes.item(i);
                                    if ("http://www.w3.org/2000/xmlns/" == attr.getNamespaceURI()) {
                                        if (!element2.hasAttributeNS("http://www.w3.org/2000/xmlns/", attr.getLocalName())) {
                                            element2.setAttributeNS("http://www.w3.org/2000/xmlns/", attr.getName(), attr.getNodeValue());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                case 5:
                case 9: {
                    parentNode = node;
                    node2 = node.getFirstChild();
                    break;
                }
            }
            while (node2 == null && parentNode != null) {
                node2 = parentNode.getNextSibling();
                parentNode = parentNode.getParentNode();
            }
            if (node2 == null) {
                break;
            }
            node = node2;
            node2 = node.getNextSibling();
        }
    }
    
    public static Element selectDsNode(Node nextSibling, final String s, int n) {
        while (nextSibling != null) {
            if (s.equals(nextSibling.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#" == nextSibling.getNamespaceURI()) {
                if (n == 0) {
                    return (Element)nextSibling;
                }
                --n;
            }
            nextSibling = nextSibling.getNextSibling();
        }
        return null;
    }
    
    public static Element selectXencNode(Node nextSibling, final String s, int n) {
        while (nextSibling != null) {
            if (s.equals(nextSibling.getLocalName()) && "http://www.w3.org/2001/04/xmlenc#" == nextSibling.getNamespaceURI()) {
                if (n == 0) {
                    return (Element)nextSibling;
                }
                --n;
            }
            nextSibling = nextSibling.getNextSibling();
        }
        return null;
    }
    
    public static Text selectDsNodeText(final Node node, final String s, final int n) {
        final Element selectDsNode = selectDsNode(node, s, n);
        if (selectDsNode == null) {
            return null;
        }
        Node node2;
        for (node2 = selectDsNode.getFirstChild(); node2 != null && node2.getNodeType() != 3; node2 = node2.getNextSibling()) {}
        return (Text)node2;
    }
    
    public static Text selectNodeText(final Node node, final String s, final String s2, final int n) {
        final Element selectNode = selectNode(node, s, s2, n);
        if (selectNode == null) {
            return null;
        }
        Node node2;
        for (node2 = selectNode.getFirstChild(); node2 != null && node2.getNodeType() != 3; node2 = node2.getNextSibling()) {}
        return (Text)node2;
    }
    
    public static Element selectNode(Node nextSibling, final String s, final String s2, int n) {
        while (nextSibling != null) {
            if (s2.equals(nextSibling.getLocalName()) && s == nextSibling.getNamespaceURI()) {
                if (n == 0) {
                    return (Element)nextSibling;
                }
                --n;
            }
            nextSibling = nextSibling.getNextSibling();
        }
        return null;
    }
    
    public static Element[] selectDsNodes(final Node node, final String s) {
        return selectNodes(node, "http://www.w3.org/2000/09/xmldsig#", s);
    }
    
    public static Element[] selectNodes(Node nextSibling, final String s, final String s2) {
        int n = 20;
        Element[] array = new Element[n];
        int n2 = 0;
        while (nextSibling != null) {
            if (s2.equals(nextSibling.getLocalName()) && s == nextSibling.getNamespaceURI()) {
                array[n2++] = (Element)nextSibling;
                if (n <= n2) {
                    final int n3 = n << 2;
                    final Element[] array2 = new Element[n3];
                    System.arraycopy(array, 0, array2, 0, n);
                    array = array2;
                    n = n3;
                }
            }
            nextSibling = nextSibling.getNextSibling();
        }
        final Element[] array3 = new Element[n2];
        System.arraycopy(array, 0, array3, 0, n2);
        return array3;
    }
    
    public static Set excludeNodeFromSet(final Node node, final Set set) {
        final HashSet set2 = new HashSet();
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            final Node node2 = (Node)iterator.next();
            if (!isDescendantOrSelf(node, node2)) {
                set2.add(node2);
            }
        }
        return set2;
    }
    
    public static boolean isDescendantOrSelf(final Node node, final Node node2) {
        if (node == node2) {
            return true;
        }
        Node node3 = node2;
        while (node3 != null) {
            if (node3 == node) {
                return true;
            }
            if (node3.getNodeType() == 2) {
                node3 = ((Attr)node3).getOwnerElement();
            }
            else {
                node3 = node3.getParentNode();
            }
        }
        return false;
    }
    
    static {
        XMLUtils.dsPrefix = null;
        XMLUtils.xmlnsDsPrefix = null;
        XMLUtils.namePrefixes = new HashMap();
    }
}
