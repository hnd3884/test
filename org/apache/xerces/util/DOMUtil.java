package org.apache.xerces.util;

import java.lang.reflect.Method;
import org.w3c.dom.ls.LSException;
import org.apache.xerces.impl.xs.opti.ElementImpl;
import org.apache.xerces.impl.xs.opti.NodeImpl;
import java.util.Hashtable;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.apache.xerces.dom.AttrImpl;
import org.w3c.dom.Attr;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Node;

public class DOMUtil
{
    protected DOMUtil() {
    }
    
    public static void copyInto(final Node node, Node parentNode) throws DOMException {
        final Document ownerDocument = parentNode.getOwnerDocument();
        final boolean b = ownerDocument instanceof DocumentImpl;
        Node parentNode2 = node;
        Object o = null;
        for (Node node2 = node; node2 != null; node2 = node2.getFirstChild(), parentNode = (Node)o) {
            final short nodeType = node2.getNodeType();
            switch (nodeType) {
                case 4: {
                    o = ownerDocument.createCDATASection(node2.getNodeValue());
                    break;
                }
                case 8: {
                    o = ownerDocument.createComment(node2.getNodeValue());
                    break;
                }
                case 1: {
                    final Element element = (Element)(o = ownerDocument.createElement(node2.getNodeName()));
                    final NamedNodeMap attributes = node2.getAttributes();
                    for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                        final Attr attr = (Attr)attributes.item(i);
                        final String nodeName = attr.getNodeName();
                        element.setAttribute(nodeName, attr.getNodeValue());
                        if (b && !attr.getSpecified()) {
                            ((AttrImpl)element.getAttributeNode(nodeName)).setSpecified(false);
                        }
                    }
                    break;
                }
                case 5: {
                    o = ownerDocument.createEntityReference(node2.getNodeName());
                    break;
                }
                case 7: {
                    o = ownerDocument.createProcessingInstruction(node2.getNodeName(), node2.getNodeValue());
                    break;
                }
                case 3: {
                    o = ownerDocument.createTextNode(node2.getNodeValue());
                    break;
                }
                default: {
                    throw new IllegalArgumentException("can't copy node type, " + nodeType + " (" + node2.getNodeName() + ')');
                }
            }
            parentNode.appendChild((Node)o);
            if (node2.hasChildNodes()) {
                parentNode2 = node2;
            }
            else {
                for (node2 = node2.getNextSibling(); node2 == null && parentNode2 != node; node2 = parentNode2.getNextSibling(), parentNode2 = parentNode2.getParentNode(), parentNode = parentNode.getParentNode()) {}
            }
        }
    }
    
    public static Element getFirstChildElement(final Node node) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getFirstVisibleChildElement(final Node node) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1 && !isHidden(node2)) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getFirstVisibleChildElement(final Node node, final Hashtable hashtable) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1 && !isHidden(node2, hashtable)) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getLastChildElement(final Node node) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() == 1) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getLastVisibleChildElement(final Node node) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() == 1 && !isHidden(node2)) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getLastVisibleChildElement(final Node node, final Hashtable hashtable) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() == 1 && !isHidden(node2, hashtable)) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElement(final Node node) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getNextVisibleSiblingElement(final Node node) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1 && !isHidden(node2)) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getNextVisibleSiblingElement(final Node node, final Hashtable hashtable) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1 && !isHidden(node2, hashtable)) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static void setHidden(final Node node) {
        if (node instanceof NodeImpl) {
            ((NodeImpl)node).setReadOnly(true, false);
        }
        else if (node instanceof org.apache.xerces.dom.NodeImpl) {
            ((org.apache.xerces.dom.NodeImpl)node).setReadOnly(true, false);
        }
    }
    
    public static void setHidden(final Node node, final Hashtable hashtable) {
        if (node instanceof NodeImpl) {
            ((NodeImpl)node).setReadOnly(true, false);
        }
        else {
            hashtable.put(node, "");
        }
    }
    
    public static void setVisible(final Node node) {
        if (node instanceof NodeImpl) {
            ((NodeImpl)node).setReadOnly(false, false);
        }
        else if (node instanceof org.apache.xerces.dom.NodeImpl) {
            ((org.apache.xerces.dom.NodeImpl)node).setReadOnly(false, false);
        }
    }
    
    public static void setVisible(final Node node, final Hashtable hashtable) {
        if (node instanceof NodeImpl) {
            ((NodeImpl)node).setReadOnly(false, false);
        }
        else {
            hashtable.remove(node);
        }
    }
    
    public static boolean isHidden(final Node node) {
        if (node instanceof NodeImpl) {
            return ((NodeImpl)node).getReadOnly();
        }
        return node instanceof org.apache.xerces.dom.NodeImpl && ((org.apache.xerces.dom.NodeImpl)node).getReadOnly();
    }
    
    public static boolean isHidden(final Node node, final Hashtable hashtable) {
        if (node instanceof NodeImpl) {
            return ((NodeImpl)node).getReadOnly();
        }
        return hashtable.containsKey(node);
    }
    
    public static Element getFirstChildElement(final Node node, final String s) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1 && node2.getNodeName().equals(s)) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getLastChildElement(final Node node, final String s) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() == 1 && node2.getNodeName().equals(s)) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElement(final Node node, final String s) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1 && node2.getNodeName().equals(s)) {
                return (Element)node2;
            }
        }
        return null;
    }
    
    public static Element getFirstChildElementNS(final Node node, final String s, final String s2) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                final String namespaceURI = node2.getNamespaceURI();
                if (namespaceURI != null && namespaceURI.equals(s) && node2.getLocalName().equals(s2)) {
                    return (Element)node2;
                }
            }
        }
        return null;
    }
    
    public static Element getLastChildElementNS(final Node node, final String s, final String s2) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() == 1) {
                final String namespaceURI = node2.getNamespaceURI();
                if (namespaceURI != null && namespaceURI.equals(s) && node2.getLocalName().equals(s2)) {
                    return (Element)node2;
                }
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElementNS(final Node node, final String s, final String s2) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                final String namespaceURI = node2.getNamespaceURI();
                if (namespaceURI != null && namespaceURI.equals(s) && node2.getLocalName().equals(s2)) {
                    return (Element)node2;
                }
            }
        }
        return null;
    }
    
    public static Element getFirstChildElement(final Node node, final String[] array) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                for (int i = 0; i < array.length; ++i) {
                    if (node2.getNodeName().equals(array[i])) {
                        return (Element)node2;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getLastChildElement(final Node node, final String[] array) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() == 1) {
                for (int i = 0; i < array.length; ++i) {
                    if (node2.getNodeName().equals(array[i])) {
                        return (Element)node2;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElement(final Node node, final String[] array) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                for (int i = 0; i < array.length; ++i) {
                    if (node2.getNodeName().equals(array[i])) {
                        return (Element)node2;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getFirstChildElementNS(final Node node, final String[][] array) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                for (int i = 0; i < array.length; ++i) {
                    final String namespaceURI = node2.getNamespaceURI();
                    if (namespaceURI != null && namespaceURI.equals(array[i][0]) && node2.getLocalName().equals(array[i][1])) {
                        return (Element)node2;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getLastChildElementNS(final Node node, final String[][] array) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() == 1) {
                for (int i = 0; i < array.length; ++i) {
                    final String namespaceURI = node2.getNamespaceURI();
                    if (namespaceURI != null && namespaceURI.equals(array[i][0]) && node2.getLocalName().equals(array[i][1])) {
                        return (Element)node2;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElementNS(final Node node, final String[][] array) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                for (int i = 0; i < array.length; ++i) {
                    final String namespaceURI = node2.getNamespaceURI();
                    if (namespaceURI != null && namespaceURI.equals(array[i][0]) && node2.getLocalName().equals(array[i][1])) {
                        return (Element)node2;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getFirstChildElement(final Node node, final String s, final String s2, final String s3) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                final Element element = (Element)node2;
                if (element.getNodeName().equals(s) && element.getAttribute(s2).equals(s3)) {
                    return element;
                }
            }
        }
        return null;
    }
    
    public static Element getLastChildElement(final Node node, final String s, final String s2, final String s3) {
        for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
            if (node2.getNodeType() == 1) {
                final Element element = (Element)node2;
                if (element.getNodeName().equals(s) && element.getAttribute(s2).equals(s3)) {
                    return element;
                }
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElement(final Node node, final String s, final String s2, final String s3) {
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                final Element element = (Element)node2;
                if (element.getNodeName().equals(s) && element.getAttribute(s2).equals(s3)) {
                    return element;
                }
            }
        }
        return null;
    }
    
    public static String getChildText(final Node node) {
        if (node == null) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            final short nodeType = node2.getNodeType();
            if (nodeType == 3) {
                sb.append(node2.getNodeValue());
            }
            else if (nodeType == 4) {
                sb.append(getChildText(node2));
            }
        }
        return sb.toString();
    }
    
    public static String getName(final Node node) {
        return node.getNodeName();
    }
    
    public static String getLocalName(final Node node) {
        final String localName = node.getLocalName();
        return (localName != null) ? localName : node.getNodeName();
    }
    
    public static Element getParent(final Element element) {
        final Node parentNode = element.getParentNode();
        if (parentNode instanceof Element) {
            return (Element)parentNode;
        }
        return null;
    }
    
    public static Document getDocument(final Node node) {
        return node.getOwnerDocument();
    }
    
    public static Element getRoot(final Document document) {
        return document.getDocumentElement();
    }
    
    public static Attr getAttr(final Element element, final String s) {
        return element.getAttributeNode(s);
    }
    
    public static Attr getAttrNS(final Element element, final String s, final String s2) {
        return element.getAttributeNodeNS(s, s2);
    }
    
    public static Attr[] getAttrs(final Element element) {
        final NamedNodeMap attributes = element.getAttributes();
        final Attr[] array = new Attr[attributes.getLength()];
        for (int i = 0; i < attributes.getLength(); ++i) {
            array[i] = (Attr)attributes.item(i);
        }
        return array;
    }
    
    public static String getValue(final Attr attr) {
        return attr.getValue();
    }
    
    public static String getAttrValue(final Element element, final String s) {
        return element.getAttribute(s);
    }
    
    public static String getAttrValueTrimmed(final Element element, final String s) {
        return XMLChar.trim(element.getAttribute(s));
    }
    
    public static String getAttrValueNS(final Element element, final String s, final String s2) {
        return element.getAttributeNS(s, s2);
    }
    
    public static String getPrefix(final Node node) {
        return node.getPrefix();
    }
    
    public static String getNamespaceURI(final Node node) {
        return node.getNamespaceURI();
    }
    
    public static String getAnnotation(final Node node) {
        if (node instanceof ElementImpl) {
            return ((ElementImpl)node).getAnnotation();
        }
        return null;
    }
    
    public static String getSyntheticAnnotation(final Node node) {
        if (node instanceof ElementImpl) {
            return ((ElementImpl)node).getSyntheticAnnotation();
        }
        return null;
    }
    
    public static DOMException createDOMException(final short n, final Throwable t) {
        final DOMException ex = new DOMException(n, (t != null) ? t.getMessage() : null);
        if (t != null && ThrowableMethods.fgThrowableMethodsAvailable) {
            try {
                ThrowableMethods.fgThrowableInitCauseMethod.invoke(ex, t);
            }
            catch (final Exception ex2) {}
        }
        return ex;
    }
    
    public static LSException createLSException(final short n, final Throwable t) {
        final LSException ex = new LSException(n, (t != null) ? t.getMessage() : null);
        if (t != null && ThrowableMethods.fgThrowableMethodsAvailable) {
            try {
                ThrowableMethods.fgThrowableInitCauseMethod.invoke(ex, t);
            }
            catch (final Exception ex2) {}
        }
        return ex;
    }
    
    static class ThrowableMethods
    {
        private static Method fgThrowableInitCauseMethod;
        private static boolean fgThrowableMethodsAvailable;
        
        private ThrowableMethods() {
        }
        
        static {
            ThrowableMethods.fgThrowableInitCauseMethod = null;
            ThrowableMethods.fgThrowableMethodsAvailable = false;
            try {
                ThrowableMethods.fgThrowableInitCauseMethod = Throwable.class.getMethod("initCause", Throwable.class);
                ThrowableMethods.fgThrowableMethodsAvailable = true;
            }
            catch (final Exception ex) {
                ThrowableMethods.fgThrowableInitCauseMethod = null;
                ThrowableMethods.fgThrowableMethodsAvailable = false;
            }
        }
    }
}
