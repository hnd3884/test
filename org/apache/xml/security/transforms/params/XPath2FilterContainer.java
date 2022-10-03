package org.apache.xml.security.transforms.params;

import org.apache.xml.security.utils.HelperNodeList;
import org.w3c.dom.NodeList;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.utils.ElementProxy;

public class XPath2FilterContainer extends ElementProxy implements TransformParam
{
    private static final String _ATT_FILTER = "Filter";
    private static final String _ATT_FILTER_VALUE_INTERSECT = "intersect";
    private static final String _ATT_FILTER_VALUE_SUBTRACT = "subtract";
    private static final String _ATT_FILTER_VALUE_UNION = "union";
    public static final String INTERSECT = "intersect";
    public static final String SUBTRACT = "subtract";
    public static final String UNION = "union";
    public static final String _TAG_XPATH2 = "XPath";
    public static final String XPathFilter2NS = "http://www.w3.org/2002/06/xmldsig-filter2";
    
    private XPath2FilterContainer() {
    }
    
    private XPath2FilterContainer(final Document document, final String s, final String s2) {
        super(document);
        super._constructionElement.setAttributeNS(null, "Filter", s2);
        super._constructionElement.appendChild(document.createTextNode(s));
    }
    
    private XPath2FilterContainer(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        final String attributeNS = super._constructionElement.getAttributeNS(null, "Filter");
        if (!attributeNS.equals("intersect") && !attributeNS.equals("subtract") && !attributeNS.equals("union")) {
            throw new XMLSecurityException("attributeValueIllegal", new Object[] { "Filter", attributeNS, "intersect, subtract or union" });
        }
    }
    
    public static XPath2FilterContainer newInstanceIntersect(final Document document, final String s) {
        return new XPath2FilterContainer(document, s, "intersect");
    }
    
    public static XPath2FilterContainer newInstanceSubtract(final Document document, final String s) {
        return new XPath2FilterContainer(document, s, "subtract");
    }
    
    public static XPath2FilterContainer newInstanceUnion(final Document document, final String s) {
        return new XPath2FilterContainer(document, s, "union");
    }
    
    public static NodeList newInstances(final Document document, final String[][] array) {
        final HelperNodeList list = new HelperNodeList();
        list.appendChild(document.createTextNode("\n"));
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i][0];
            final String s2 = array[i][1];
            if (!s.equals("intersect") && !s.equals("subtract") && !s.equals("union")) {
                throw new IllegalArgumentException("The type(" + i + ")=\"" + s + "\" is illegal");
            }
            list.appendChild(new XPath2FilterContainer(document, s2, s).getElement());
            list.appendChild(document.createTextNode("\n"));
        }
        return list;
    }
    
    public static XPath2FilterContainer newInstance(final Element element, final String s) throws XMLSecurityException {
        return new XPath2FilterContainer(element, s);
    }
    
    public boolean isIntersect() {
        return super._constructionElement.getAttributeNS(null, "Filter").equals("intersect");
    }
    
    public boolean isSubtract() {
        return super._constructionElement.getAttributeNS(null, "Filter").equals("subtract");
    }
    
    public boolean isUnion() {
        return super._constructionElement.getAttributeNS(null, "Filter").equals("union");
    }
    
    public String getXPathFilterStr() {
        return this.getTextFromTextChild();
    }
    
    public Node getXPathFilterTextNode() {
        final NodeList childNodes = super._constructionElement.getChildNodes();
        for (int length = childNodes.getLength(), i = 0; i < length; ++i) {
            if (childNodes.item(i).getNodeType() == 3) {
                return childNodes.item(i);
            }
        }
        return null;
    }
    
    public final String getBaseLocalName() {
        return "XPath";
    }
    
    public final String getBaseNamespace() {
        return "http://www.w3.org/2002/06/xmldsig-filter2";
    }
}
