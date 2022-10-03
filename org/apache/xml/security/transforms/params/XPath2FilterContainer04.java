package org.apache.xml.security.transforms.params;

import org.w3c.dom.NodeList;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.utils.ElementProxy;

public class XPath2FilterContainer04 extends ElementProxy implements TransformParam
{
    private static final String _ATT_FILTER = "Filter";
    private static final String _ATT_FILTER_VALUE_INTERSECT = "intersect";
    private static final String _ATT_FILTER_VALUE_SUBTRACT = "subtract";
    private static final String _ATT_FILTER_VALUE_UNION = "union";
    public static final String _TAG_XPATH2 = "XPath";
    public static final String XPathFilter2NS = "http://www.w3.org/2002/04/xmldsig-filter2";
    
    private XPath2FilterContainer04() {
    }
    
    private XPath2FilterContainer04(final Document document, final String s, final String s2) {
        super(document);
        super._constructionElement.setAttributeNS(null, "Filter", s2);
        if (s.length() > 2 && !Character.isWhitespace(s.charAt(0))) {
            super._constructionElement.appendChild(document.createTextNode("\n" + s + "\n"));
        }
        else {
            super._constructionElement.appendChild(document.createTextNode(s));
        }
    }
    
    private XPath2FilterContainer04(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        final String attributeNS = super._constructionElement.getAttributeNS(null, "Filter");
        if (!attributeNS.equals("intersect") && !attributeNS.equals("subtract") && !attributeNS.equals("union")) {
            throw new XMLSecurityException("attributeValueIllegal", new Object[] { "Filter", attributeNS, "intersect, subtract or union" });
        }
    }
    
    public static XPath2FilterContainer04 newInstanceIntersect(final Document document, final String s) {
        return new XPath2FilterContainer04(document, s, "intersect");
    }
    
    public static XPath2FilterContainer04 newInstanceSubtract(final Document document, final String s) {
        return new XPath2FilterContainer04(document, s, "subtract");
    }
    
    public static XPath2FilterContainer04 newInstanceUnion(final Document document, final String s) {
        return new XPath2FilterContainer04(document, s, "union");
    }
    
    public static XPath2FilterContainer04 newInstance(final Element element, final String s) throws XMLSecurityException {
        return new XPath2FilterContainer04(element, s);
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
        return "http://www.w3.org/2002/04/xmldsig-filter2";
    }
}
