package org.jcp.xml.dsig.internal.dom;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.AbstractSet;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import java.util.List;
import javax.xml.crypto.dsig.spec.XPathType;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.XMLCryptoContext;
import java.util.Set;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.apache.xml.security.utils.IdResolver;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DOMUtils
{
    private DOMUtils() {
    }
    
    public static Document getOwnerDocument(final Node node) {
        if (node.getNodeType() == 9) {
            return (Document)node;
        }
        return node.getOwnerDocument();
    }
    
    public static Element createElement(final Document document, final String s, final String s2, final String s3) {
        return document.createElementNS(s2, (s3 == null) ? s : (s3 + ":" + s));
    }
    
    public static void setAttribute(final Element element, final String s, final String s2) {
        if (s2 == null) {
            return;
        }
        element.setAttributeNS(null, s, s2);
    }
    
    public static void setAttributeID(final Element element, final String s, final String s2) {
        if (s2 == null) {
            return;
        }
        element.setAttributeNS(null, s, s2);
        IdResolver.registerElementById(element, s2);
    }
    
    public static Element getFirstChildElement(final Node node) {
        Node node2;
        for (node2 = node.getFirstChild(); node2 != null && node2.getNodeType() != 1; node2 = node2.getNextSibling()) {}
        return (Element)node2;
    }
    
    public static Element getLastChildElement(final Node node) {
        Node node2;
        for (node2 = node.getLastChild(); node2 != null && node2.getNodeType() != 1; node2 = node2.getPreviousSibling()) {}
        return (Element)node2;
    }
    
    public static Element getNextSiblingElement(final Node node) {
        Node node2;
        for (node2 = node.getNextSibling(); node2 != null && node2.getNodeType() != 1; node2 = node2.getNextSibling()) {}
        return (Element)node2;
    }
    
    public static String getAttributeValue(final Element element, final String s) {
        final Attr attributeNodeNS = element.getAttributeNodeNS(null, s);
        return (attributeNodeNS == null) ? null : attributeNodeNS.getValue();
    }
    
    public static Set nodeSet(final NodeList list) {
        return new NodeSet(list);
    }
    
    public static String getNSPrefix(final XMLCryptoContext xmlCryptoContext, final String s) {
        if (xmlCryptoContext != null) {
            return xmlCryptoContext.getNamespacePrefix(s, xmlCryptoContext.getDefaultNamespacePrefix());
        }
        return null;
    }
    
    public static String getSignaturePrefix(final XMLCryptoContext xmlCryptoContext) {
        return getNSPrefix(xmlCryptoContext, "http://www.w3.org/2000/09/xmldsig#");
    }
    
    public static void removeAllChildren(final Node node) {
        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            node.removeChild(childNodes.item(i));
        }
    }
    
    public static boolean nodesEqual(final Node node, final Node node2) {
        return node == node2 || node.getNodeType() == node2.getNodeType();
    }
    
    public static void appendChild(final Node node, final Node node2) {
        final Document ownerDocument = getOwnerDocument(node);
        if (node2.getOwnerDocument() != ownerDocument) {
            node.appendChild(ownerDocument.importNode(node2, true));
        }
        else {
            node.appendChild(node2);
        }
    }
    
    public static boolean paramsEqual(final AlgorithmParameterSpec algorithmParameterSpec, final AlgorithmParameterSpec algorithmParameterSpec2) {
        if (algorithmParameterSpec == algorithmParameterSpec2) {
            return true;
        }
        if (algorithmParameterSpec instanceof XPathFilter2ParameterSpec && algorithmParameterSpec2 instanceof XPathFilter2ParameterSpec) {
            return paramsEqual((XPathFilter2ParameterSpec)algorithmParameterSpec, (XPathFilter2ParameterSpec)algorithmParameterSpec2);
        }
        if (algorithmParameterSpec instanceof ExcC14NParameterSpec && algorithmParameterSpec2 instanceof ExcC14NParameterSpec) {
            return paramsEqual((ExcC14NParameterSpec)algorithmParameterSpec, (ExcC14NParameterSpec)algorithmParameterSpec2);
        }
        if (algorithmParameterSpec instanceof XPathFilterParameterSpec && algorithmParameterSpec2 instanceof XPathFilterParameterSpec) {
            return paramsEqual((XPathFilterParameterSpec)algorithmParameterSpec, (XPathFilterParameterSpec)algorithmParameterSpec2);
        }
        return algorithmParameterSpec instanceof XSLTTransformParameterSpec && algorithmParameterSpec2 instanceof XSLTTransformParameterSpec && paramsEqual((XSLTTransformParameterSpec)algorithmParameterSpec, (XSLTTransformParameterSpec)algorithmParameterSpec2);
    }
    
    private static boolean paramsEqual(final XPathFilter2ParameterSpec xPathFilter2ParameterSpec, final XPathFilter2ParameterSpec xPathFilter2ParameterSpec2) {
        final List xPathList = xPathFilter2ParameterSpec.getXPathList();
        final List xPathList2 = xPathFilter2ParameterSpec2.getXPathList();
        final int size = xPathList.size();
        if (size != xPathList2.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            final XPathType xPathType = xPathList.get(i);
            final XPathType xPathType2 = xPathList2.get(i);
            if (!xPathType.getExpression().equals(xPathType2.getExpression()) || !xPathType.getNamespaceMap().equals(xPathType2.getNamespaceMap()) || xPathType.getFilter() != xPathType2.getFilter()) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean paramsEqual(final ExcC14NParameterSpec excC14NParameterSpec, final ExcC14NParameterSpec excC14NParameterSpec2) {
        return excC14NParameterSpec.getPrefixList().equals(excC14NParameterSpec2.getPrefixList());
    }
    
    private static boolean paramsEqual(final XPathFilterParameterSpec xPathFilterParameterSpec, final XPathFilterParameterSpec xPathFilterParameterSpec2) {
        return xPathFilterParameterSpec.getXPath().equals(xPathFilterParameterSpec2.getXPath()) && xPathFilterParameterSpec.getNamespaceMap().equals(xPathFilterParameterSpec2.getNamespaceMap());
    }
    
    private static boolean paramsEqual(final XSLTTransformParameterSpec xsltTransformParameterSpec, final XSLTTransformParameterSpec xsltTransformParameterSpec2) {
        final XMLStructure stylesheet = xsltTransformParameterSpec2.getStylesheet();
        return stylesheet instanceof DOMStructure && nodesEqual(((DOMStructure)xsltTransformParameterSpec.getStylesheet()).getNode(), ((DOMStructure)stylesheet).getNode());
    }
    
    static class NodeSet extends AbstractSet
    {
        private NodeList nl;
        
        public NodeSet(final NodeList nl) {
            this.nl = nl;
        }
        
        public int size() {
            return this.nl.getLength();
        }
        
        public Iterator iterator() {
            return new Iterator() {
                int index = 0;
                
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                
                public Object next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return NodeSet.this.nl.item(this.index++);
                }
                
                public boolean hasNext() {
                    return this.index < NodeSet.this.nl.getLength();
                }
            };
        }
    }
}
