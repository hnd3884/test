package com.sun.org.apache.xerces.internal.util;

import java.lang.reflect.Method;
import org.w3c.dom.ls.LSException;
import com.sun.org.apache.xerces.internal.impl.xs.opti.ElementImpl;
import com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl;
import java.util.Map;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import org.w3c.dom.Attr;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import org.w3c.dom.Node;

public class DOMUtil
{
    protected DOMUtil() {
    }
    
    public static void copyInto(final Node src, Node dest) throws DOMException {
        final Document factory = dest.getOwnerDocument();
        final boolean domimpl = factory instanceof DocumentImpl;
        final Node start = src;
        Node parent = src;
        Node node;
        for (Node place = src; place != null; place = place.getFirstChild(), dest = node) {
            node = null;
            final int type = place.getNodeType();
            switch (type) {
                case 4: {
                    node = factory.createCDATASection(place.getNodeValue());
                    break;
                }
                case 8: {
                    node = factory.createComment(place.getNodeValue());
                    break;
                }
                case 1: {
                    final Element element = (Element)(node = factory.createElement(place.getNodeName()));
                    final NamedNodeMap attrs = place.getAttributes();
                    for (int attrCount = attrs.getLength(), i = 0; i < attrCount; ++i) {
                        final Attr attr = (Attr)attrs.item(i);
                        final String attrName = attr.getNodeName();
                        final String attrValue = attr.getNodeValue();
                        element.setAttribute(attrName, attrValue);
                        if (domimpl && !attr.getSpecified()) {
                            ((AttrImpl)element.getAttributeNode(attrName)).setSpecified(false);
                        }
                    }
                    break;
                }
                case 5: {
                    node = factory.createEntityReference(place.getNodeName());
                    break;
                }
                case 7: {
                    node = factory.createProcessingInstruction(place.getNodeName(), place.getNodeValue());
                    break;
                }
                case 3: {
                    node = factory.createTextNode(place.getNodeValue());
                    break;
                }
                default: {
                    throw new IllegalArgumentException("can't copy node type, " + type + " (" + place.getNodeName() + ')');
                }
            }
            dest.appendChild(node);
            if (place.hasChildNodes()) {
                parent = place;
            }
            else {
                for (place = place.getNextSibling(); place == null && parent != start; place = parent.getNextSibling(), parent = parent.getParentNode(), dest = dest.getParentNode()) {}
            }
        }
    }
    
    public static Element getFirstChildElement(final Node parent) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1) {
                return (Element)child;
            }
        }
        return null;
    }
    
    public static Element getFirstVisibleChildElement(final Node parent) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1 && !isHidden(child)) {
                return (Element)child;
            }
        }
        return null;
    }
    
    public static Element getFirstVisibleChildElement(final Node parent, final Map<Node, String> hiddenNodes) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1 && !isHidden(child, hiddenNodes)) {
                return (Element)child;
            }
        }
        return null;
    }
    
    public static Element getLastChildElement(final Node parent) {
        for (Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
            if (child.getNodeType() == 1) {
                return (Element)child;
            }
        }
        return null;
    }
    
    public static Element getLastVisibleChildElement(final Node parent) {
        for (Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
            if (child.getNodeType() == 1 && !isHidden(child)) {
                return (Element)child;
            }
        }
        return null;
    }
    
    public static Element getLastVisibleChildElement(final Node parent, final Map<Node, String> hiddenNodes) {
        for (Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
            if (child.getNodeType() == 1 && !isHidden(child, hiddenNodes)) {
                return (Element)child;
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElement(final Node node) {
        for (Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
            if (sibling.getNodeType() == 1) {
                return (Element)sibling;
            }
        }
        return null;
    }
    
    public static Element getNextVisibleSiblingElement(final Node node) {
        for (Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
            if (sibling.getNodeType() == 1 && !isHidden(sibling)) {
                return (Element)sibling;
            }
        }
        return null;
    }
    
    public static Element getNextVisibleSiblingElement(final Node node, final Map<Node, String> hiddenNodes) {
        for (Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
            if (sibling.getNodeType() == 1 && !isHidden(sibling, hiddenNodes)) {
                return (Element)sibling;
            }
        }
        return null;
    }
    
    public static void setHidden(final Node node) {
        if (node instanceof NodeImpl) {
            ((NodeImpl)node).setReadOnly(true, false);
        }
        else if (node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl) {
            ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).setReadOnly(true, false);
        }
    }
    
    public static void setHidden(final Node node, final Map<Node, String> hiddenNodes) {
        if (node instanceof NodeImpl) {
            ((NodeImpl)node).setReadOnly(true, false);
        }
        else {
            hiddenNodes.put(node, "");
        }
    }
    
    public static void setVisible(final Node node) {
        if (node instanceof NodeImpl) {
            ((NodeImpl)node).setReadOnly(false, false);
        }
        else if (node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl) {
            ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).setReadOnly(false, false);
        }
    }
    
    public static void setVisible(final Node node, final Map<Node, String> hiddenNodes) {
        if (node instanceof NodeImpl) {
            ((NodeImpl)node).setReadOnly(false, false);
        }
        else {
            hiddenNodes.remove(node);
        }
    }
    
    public static boolean isHidden(final Node node) {
        if (node instanceof NodeImpl) {
            return ((NodeImpl)node).getReadOnly();
        }
        return node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl && ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).getReadOnly();
    }
    
    public static boolean isHidden(final Node node, final Map<Node, String> hiddenNodes) {
        if (node instanceof NodeImpl) {
            return ((NodeImpl)node).getReadOnly();
        }
        return hiddenNodes.containsKey(node);
    }
    
    public static Element getFirstChildElement(final Node parent, final String elemName) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1 && child.getNodeName().equals(elemName)) {
                return (Element)child;
            }
        }
        return null;
    }
    
    public static Element getLastChildElement(final Node parent, final String elemName) {
        for (Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
            if (child.getNodeType() == 1 && child.getNodeName().equals(elemName)) {
                return (Element)child;
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElement(final Node node, final String elemName) {
        for (Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
            if (sibling.getNodeType() == 1 && sibling.getNodeName().equals(elemName)) {
                return (Element)sibling;
            }
        }
        return null;
    }
    
    public static Element getFirstChildElementNS(final Node parent, final String uri, final String localpart) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1) {
                final String childURI = child.getNamespaceURI();
                if (childURI != null && childURI.equals(uri) && child.getLocalName().equals(localpart)) {
                    return (Element)child;
                }
            }
        }
        return null;
    }
    
    public static Element getLastChildElementNS(final Node parent, final String uri, final String localpart) {
        for (Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
            if (child.getNodeType() == 1) {
                final String childURI = child.getNamespaceURI();
                if (childURI != null && childURI.equals(uri) && child.getLocalName().equals(localpart)) {
                    return (Element)child;
                }
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElementNS(final Node node, final String uri, final String localpart) {
        for (Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
            if (sibling.getNodeType() == 1) {
                final String siblingURI = sibling.getNamespaceURI();
                if (siblingURI != null && siblingURI.equals(uri) && sibling.getLocalName().equals(localpart)) {
                    return (Element)sibling;
                }
            }
        }
        return null;
    }
    
    public static Element getFirstChildElement(final Node parent, final String[] elemNames) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1) {
                for (int i = 0; i < elemNames.length; ++i) {
                    if (child.getNodeName().equals(elemNames[i])) {
                        return (Element)child;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getLastChildElement(final Node parent, final String[] elemNames) {
        for (Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
            if (child.getNodeType() == 1) {
                for (int i = 0; i < elemNames.length; ++i) {
                    if (child.getNodeName().equals(elemNames[i])) {
                        return (Element)child;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElement(final Node node, final String[] elemNames) {
        for (Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
            if (sibling.getNodeType() == 1) {
                for (int i = 0; i < elemNames.length; ++i) {
                    if (sibling.getNodeName().equals(elemNames[i])) {
                        return (Element)sibling;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getFirstChildElementNS(final Node parent, final String[][] elemNames) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1) {
                for (int i = 0; i < elemNames.length; ++i) {
                    final String uri = child.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) && child.getLocalName().equals(elemNames[i][1])) {
                        return (Element)child;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getLastChildElementNS(final Node parent, final String[][] elemNames) {
        for (Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
            if (child.getNodeType() == 1) {
                for (int i = 0; i < elemNames.length; ++i) {
                    final String uri = child.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) && child.getLocalName().equals(elemNames[i][1])) {
                        return (Element)child;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElementNS(final Node node, final String[][] elemNames) {
        for (Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
            if (sibling.getNodeType() == 1) {
                for (int i = 0; i < elemNames.length; ++i) {
                    final String uri = sibling.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) && sibling.getLocalName().equals(elemNames[i][1])) {
                        return (Element)sibling;
                    }
                }
            }
        }
        return null;
    }
    
    public static Element getFirstChildElement(final Node parent, final String elemName, final String attrName, final String attrValue) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1) {
                final Element element = (Element)child;
                if (element.getNodeName().equals(elemName) && element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
        }
        return null;
    }
    
    public static Element getLastChildElement(final Node parent, final String elemName, final String attrName, final String attrValue) {
        for (Node child = parent.getLastChild(); child != null; child = child.getPreviousSibling()) {
            if (child.getNodeType() == 1) {
                final Element element = (Element)child;
                if (element.getNodeName().equals(elemName) && element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
        }
        return null;
    }
    
    public static Element getNextSiblingElement(final Node node, final String elemName, final String attrName, final String attrValue) {
        for (Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
            if (sibling.getNodeType() == 1) {
                final Element element = (Element)sibling;
                if (element.getNodeName().equals(elemName) && element.getAttribute(attrName).equals(attrValue)) {
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
        final StringBuffer str = new StringBuffer();
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            final short type = child.getNodeType();
            if (type == 3) {
                str.append(child.getNodeValue());
            }
            else if (type == 4) {
                str.append(getChildText(child));
            }
        }
        return str.toString();
    }
    
    public static String getName(final Node node) {
        return node.getNodeName();
    }
    
    public static String getLocalName(final Node node) {
        final String name = node.getLocalName();
        return (name != null) ? name : node.getNodeName();
    }
    
    public static Element getParent(final Element elem) {
        final Node parent = elem.getParentNode();
        if (parent instanceof Element) {
            return (Element)parent;
        }
        return null;
    }
    
    public static Document getDocument(final Node node) {
        return node.getOwnerDocument();
    }
    
    public static Element getRoot(final Document doc) {
        return doc.getDocumentElement();
    }
    
    public static Attr getAttr(final Element elem, final String name) {
        return elem.getAttributeNode(name);
    }
    
    public static Attr getAttrNS(final Element elem, final String nsUri, final String localName) {
        return elem.getAttributeNodeNS(nsUri, localName);
    }
    
    public static Attr[] getAttrs(final Element elem) {
        final NamedNodeMap attrMap = elem.getAttributes();
        final Attr[] attrArray = new Attr[attrMap.getLength()];
        for (int i = 0; i < attrMap.getLength(); ++i) {
            attrArray[i] = (Attr)attrMap.item(i);
        }
        return attrArray;
    }
    
    public static String getValue(final Attr attribute) {
        return attribute.getValue();
    }
    
    public static String getAttrValue(final Element elem, final String name) {
        return elem.getAttribute(name);
    }
    
    public static String getAttrValueNS(final Element elem, final String nsUri, final String localName) {
        return elem.getAttributeNS(nsUri, localName);
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
    
    public static DOMException createDOMException(final short code, final Throwable cause) {
        final DOMException de = new DOMException(code, (cause != null) ? cause.getMessage() : null);
        if (cause != null && ThrowableMethods.fgThrowableMethodsAvailable) {
            try {
                ThrowableMethods.fgThrowableInitCauseMethod.invoke(de, cause);
            }
            catch (final Exception ex) {}
        }
        return de;
    }
    
    public static LSException createLSException(final short code, final Throwable cause) {
        final LSException lse = new LSException(code, (cause != null) ? cause.getMessage() : null);
        if (cause != null && ThrowableMethods.fgThrowableMethodsAvailable) {
            try {
                ThrowableMethods.fgThrowableInitCauseMethod.invoke(lse, cause);
            }
            catch (final Exception ex) {}
        }
        return lse;
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
            catch (final Exception exc) {
                ThrowableMethods.fgThrowableInitCauseMethod = null;
                ThrowableMethods.fgThrowableMethodsAvailable = false;
            }
        }
    }
}
