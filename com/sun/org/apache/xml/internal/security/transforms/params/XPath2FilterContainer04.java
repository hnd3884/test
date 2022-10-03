package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;

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
        this.setLocalAttribute("Filter", s2);
        if (s.length() > 2 && !Character.isWhitespace(s.charAt(0))) {
            this.addReturnToSelf();
            this.appendSelf(this.createText(s));
            this.addReturnToSelf();
        }
        else {
            this.appendSelf(this.createText(s));
        }
    }
    
    private XPath2FilterContainer04(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        final String localAttribute = this.getLocalAttribute("Filter");
        if (!localAttribute.equals("intersect") && !localAttribute.equals("subtract") && !localAttribute.equals("union")) {
            throw new XMLSecurityException("attributeValueIllegal", new Object[] { "Filter", localAttribute, "intersect, subtract or union" });
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
        return this.getLocalAttribute("Filter").equals("intersect");
    }
    
    public boolean isSubtract() {
        return this.getLocalAttribute("Filter").equals("subtract");
    }
    
    public boolean isUnion() {
        return this.getLocalAttribute("Filter").equals("union");
    }
    
    public String getXPathFilterStr() {
        return this.getTextFromTextChild();
    }
    
    public Node getXPathFilterTextNode() {
        for (Node node = this.getElement().getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 3) {
                return node;
            }
        }
        return null;
    }
    
    @Override
    public final String getBaseLocalName() {
        return "XPath";
    }
    
    @Override
    public final String getBaseNamespace() {
        return "http://www.w3.org/2002/04/xmldsig-filter2";
    }
}
